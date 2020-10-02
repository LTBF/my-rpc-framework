package github.ltbf.transport.netty;

import github.ltbf.dto.RpcRequest;
import github.ltbf.dto.RpcResponse;
import github.ltbf.registry.ServiceRegistry;
import github.ltbf.registry.impl.DefaultServiceRegistry;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * @author shkstart
 * @create 2020-10-02 16:15
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static ServiceRegistry serviceRegistry;
    static {
        serviceRegistry = new DefaultServiceRegistry();
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            RpcRequest rpcRequest = (RpcRequest) msg;
            logger.info(String.format("server receive msg: %s", rpcRequest));
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object result = null;
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParamTypes());
            result = method.invoke(service,rpcRequest.getParameters());

            logger.info(String.format("server get result: %s", result.toString()));
            ChannelFuture f = ctx.writeAndFlush(RpcResponse.success(result));
            f.addListener(ChannelFutureListener.CLOSE);
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
            logger.error("occur exception on :" + e);
        }
        finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("server catch exception");
        cause.printStackTrace();
        ctx.close();
    }
}
