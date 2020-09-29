package github.ltbf;

import github.ltbf.dto.RpcRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author shkstart
 * @create 2020-09-28 15:47
 */
@AllArgsConstructor
public class RPCClientProxy implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(RPCClientProxy.class);
    private String host;
    private Integer port;

    public <T>T getProxy(Class<T> clazz){
        return (T)Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[]{clazz}, this);
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = RpcRequest.builder().methodName(method.getName())
                .interfaceName(proxy.getClass().getInterfaces()[0].getName())
                .paramTypes(method.getParameterTypes())
                .parameters(args).build();
        logger.info("client invoke method ing...");

        return RpcClient.sendRpcRequest(rpcRequest, host, port);
    }
}
