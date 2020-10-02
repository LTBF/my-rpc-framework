package github.ltbf.transport.socket;

import github.ltbf.dto.RpcRequest;
import github.ltbf.transport.RpcClient;
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
public class RpcClientProxy implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);
    private RpcClient rpcClient;


    public <T>T getProxy(Class<T> clazz){
        return (T)Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = RpcRequest.builder().methodName(method.getName())
                .interfaceName(method.getDeclaringClass().getName())   // 根据方法获取类名
                .paramTypes(method.getParameterTypes())
                .parameters(args).build();
        logger.info("client invoke method ing...");

        return rpcClient.sendRpcRequest(rpcRequest);
    }
}