package github.ltbf.transport.netty.server;

import github.ltbf.dto.RpcRequest;
import github.ltbf.provider.ServiceProvider;
import github.ltbf.provider.impl.ServiceProviderImpl;
import github.ltbf.registry.ServiceRegistry;
import github.ltbf.registry.impl.ServiceRegistryImpl;
import github.ltbf.transport.RpcRequestHandler;
import github.ltbf.util.concurrent.ThreadPoolFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;


/**
 * @author shkstart
 * @create 2020-10-02 16:15
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static ServiceProvider serviceProvider;
    private static RpcRequestHandler rpcRequestHandler;
    private static ExecutorService threadPool;

    static {
        serviceProvider = new ServiceProviderImpl();
        rpcRequestHandler = new RpcRequestHandler();
        threadPool = ThreadPoolFactory.createDefaultThreadPool("netty-server-handler-rpc-pool");
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        // 交给线程池去执行，可以减少避免阻塞（也可以提交个taskQueue)
        threadPool.execute(() -> {
            try {
                RpcRequest rpcRequest = (RpcRequest) msg;
                logger.info(String.format("server receive msg: %s", rpcRequest));
                String interfaceName = rpcRequest.getInterfaceName();
                Object service = serviceProvider.getServiceProvider(interfaceName);
                Object result = rpcRequestHandler.handle(rpcRequest,service);
                logger.info(String.format("server get result: %s", result.toString()));
                ChannelFuture f = ctx.writeAndFlush(result);
                f.addListener(ChannelFutureListener.CLOSE);
            }
            finally {
                ReferenceCountUtil.release(msg);
            }
        });

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("server catch exception");
        cause.printStackTrace();
        ctx.close();
    }
}
