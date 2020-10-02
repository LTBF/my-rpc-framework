package github.ltbf.transport.netty.client;

import github.ltbf.dto.RpcRequest;
import github.ltbf.dto.RpcResponse;
import github.ltbf.serialize.Serializer;
import github.ltbf.serialize.kryo.KryoSerializer;
import github.ltbf.transport.RpcClient;
import github.ltbf.transport.netty.codec.NettyKryoDecoder;
import github.ltbf.transport.netty.codec.NettyKryoEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shkstart
 * @create 2020-10-02 13:14
 */
@AllArgsConstructor
public class NettyRpcClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyRpcClient.class);
    private String host;
    private int port;
    private static Bootstrap bootstrap;

    /**
     * 初始化相关资源
     * */
    static{

        // 1.创建客户端启动引导/辅助类：Bootstrap
        bootstrap = new Bootstrap();
        // 2.NioEventLoopGroup对象实例
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        Serializer serializer = new KryoSerializer();

        // 3.指定线程组
        bootstrap.group(eventLoopGroup)
                // 4.指定通道IO模型
                .channel(NioSocketChannel.class)
                // ???
                .option(ChannelOption.SO_KEEPALIVE, true)
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
     * 发送消息
     * */

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {

        try{
            // 6.尝试连接, 同步方式
            ChannelFuture f = bootstrap.connect(host, port).sync();
            logger.info("client connect {}", host + ":" + port);
            // 获取连接相关联的channel
            Channel futureChannel = f.channel();

            if(futureChannel != null){
                // 向channel中写信息，并异步监听，打印日志
                futureChannel.writeAndFlush(rpcRequest).addListener(future -> {
                    if(future.isSuccess()){
                        logger.info(String.format("clent sent message: %s",rpcRequest.toString()));
                    }
                    else{
                        logger.error("client send fail" + future.cause());
                    }
                });
                // 等待服务端监听端口关闭,该方法进行阻塞,等待服务端链路关闭之后继续执行,这种模式一般都是使用Netty模块主动向服务端发送请求，然后最后结束才使用
                futureChannel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                RpcResponse rpcResponse = futureChannel.attr(key).get();
                return rpcResponse.getData();
            }

        }
        catch (InterruptedException e){
            logger.error("occur exception on:" + e);
        }

        return null;
    }
}
