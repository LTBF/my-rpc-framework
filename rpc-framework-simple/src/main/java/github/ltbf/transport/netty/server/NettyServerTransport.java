package github.ltbf.transport.netty.server;

import github.ltbf.dto.RpcRequest;
import github.ltbf.dto.RpcResponse;
import github.ltbf.provider.ServiceProvider;
import github.ltbf.provider.impl.ServiceProviderImpl;
import github.ltbf.registry.ServiceRegistry;
import github.ltbf.registry.impl.ServiceRegistryImpl;
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

import java.net.InetSocketAddress;

/**
 * 服务端，
 * @author shkstart
 * @create 2020-10-02 16:06
 */
public class NettyServerTransport {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerTransport.class);
    // 服务地址
    private String host;
    // 服务端口
    private int port;
    // 序列化类
    private KryoSerializer kryoSerializer;
    // 服务提供者
    private ServiceProvider serviceProvider;
    // 服务注册中心
    private ServiceRegistry serviceRegistry;

    public NettyServerTransport(String host, int port){
        this.host = host;
        this.port = port;
        kryoSerializer = new KryoSerializer();
        serviceProvider = new ServiceProviderImpl();
        serviceRegistry = new ServiceRegistryImpl();
    }

    /**
     * 服务发布,添加到本地，注册中心注册
     * @param service
     */
    public void publicService(Object service){
        serviceProvider.addServiceProvider(service);
        serviceRegistry.registerService(service, new InetSocketAddress(host, port));
    }

    /**
     * 服务启动
     */
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
                    // TCP默认开启了 Nagle 算法
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 表示系统用于临时存放已完成三次握手的请求的队列的最大长度,如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 开启TCP心跳机制
                    .option(ChannelOption.SO_KEEPALIVE, true);

            // 同步绑定端口，等待直到绑定成功
            ChannelFuture f = serverBootstrap.bind(port).sync();
            // 等待服务端监听端口关闭
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
