package github.ltbf.transport.netty.server;

import github.ltbf.dto.RpcRequest;
import github.ltbf.dto.RpcResponse;
import github.ltbf.serialize.kryo.KryoSerializer;
import github.ltbf.transport.netty.codec.NettyKryoDecoder;
import github.ltbf.transport.netty.codec.NettyKryoEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shkstart
 * @create 2020-10-02 16:06
 */
public class NettyServerTransport {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerTransport.class);

    private int port;
    private KryoSerializer kryoSerializer;

    public NettyServerTransport(int port){
        this.port = port;
        kryoSerializer = new KryoSerializer();
    }

    public void start(){
        // 负责接受连接请求的线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 负责处理连接IO的线程组
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            // 配置引导类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)    // 绑定线程组
                    .channel(NioServerSocketChannel.class)    // 指定通道类型，反射
                    .handler(new LoggingHandler(LogLevel.INFO))   // ？？？
                    .childHandler(new ChannelInitializer<SocketChannel>() {    // 数据读写操作
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // 编解码器
                            ch.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RpcRequest.class));
                            ch.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, RpcResponse.class));
                            // 数据处理
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    })
                    // 设置tcp缓冲区
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_KEEPALIVE, true);

            // 异步绑定端口
            ChannelFuture f = serverBootstrap.bind(port).sync();
            // 异步监听通道关闭
            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error("occur exception when start server:", e);
        } finally {
            // 优雅的关闭线程组
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }



}
