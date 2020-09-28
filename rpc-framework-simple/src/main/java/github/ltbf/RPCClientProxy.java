package github.ltbf;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;

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
        RPCRequest rpcRequest = RPCRequest.builder().methodName(method.getName())
                .interfaceName(proxy.getClass().getName())
                .paramTypes(method.getParameterTypes())
                .parameters(args).build();
        logger.info("client ready to connect...");

        try(Socket socket = new Socket(host, port);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());){

            oos.writeObject(rpcRequest);
            //oos.flush();
            Object result = ois.readObject();
            return result;
        }
        catch (IOException | ClassNotFoundException e){
            logger.error("IOExcepton:" + e);
        }

        return null;
    }
}
