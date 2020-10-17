package github.ltbf.transport;

import github.ltbf.dto.RpcRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @author shkstart
 * @create 2020-09-28 15:47
 */
@AllArgsConstructor
@Slf4j
public class RpcClientProxy implements InvocationHandler {

    private ClientTransport rpcClient;


    public <T>T getProxy(Class<T> clazz){
        return (T)Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = RpcRequest.builder().methodName(method.getName())
                .interfaceName(method.getDeclaringClass().getName())   // 根据方法获取类名
                .paramTypes(method.getParameterTypes())
                .parameters(args)
                .requestId(UUID.randomUUID().toString()).build();
        log.info("client invoke method ing...");

        return rpcClient.sendRpcRequest(rpcRequest);
    }
}
