package github.ltbf.transport.netty.client;

import github.ltbf.dto.RpcRequest;
import github.ltbf.dto.RpcResponse;
import github.ltbf.serialize.Serializer;
import github.ltbf.serialize.kryo.KryoSerializer;
import github.ltbf.transport.netty.codec.NettyKryoDecoder;
import github.ltbf.transport.netty.codec.NettyKryoEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shkstart
 * @create 2020-10-04 16:14
 * Netty客户端初始化配置类
 */
public class NettyClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private static Bootstrap bootstrap;
    private static EventLoopGroup eventLoopGroup;

    /**
     * 初始化配置信息
     * */
    static{

        // 1.创建客户端启动引导/辅助类：Bootstrap
        bootstrap = new Bootstrap();
        // 2.NioEventLoopGroup对象实例
        eventLoopGroup = new NioEventLoopGroup();
        Serializer serializer = new KryoSerializer();
        // 3.指定线程组
        bootstrap.group(eventLoopGroup)
                // 4.指定通道IO模型
                .channel(NioSocketChannel.class)
                // 是否开启TCP心跳机制
                .option(ChannelOption.SO_KEEPALIVE, true)
                // 是否开启Nagle算法，默认开启
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch){
                        ChannelPipeline pipeline = ch.pipeline();
                        // 解码器，客户端接收RpcResponse对象
                        pipeline.addLast(new NettyKryoDecoder(serializer, RpcResponse.class));
                        // 编码器，客户端发送RpcRequest对象
                        pipeline.addLast(new NettyKryoEncoder(serializer, RpcRequest.class));
                        // 5.自定义消息的业务处理逻辑
                        pipeline.addLast(new NettyClientHandler());
                    }
                });
    }

    /**
     * 关闭Netty客户端
     */
    public static void close(){
        logger.info("shutdown the Netty Client");
        eventLoopGroup.shutdownGracefully();
    }

    /**
     * 返回Netty客户端初始化完成对象
     * @return 配置好的Netty客户端（还未连接）
     */
    public static Bootstrap initializeBootstrap(){
        return bootstrap;
    }

}
