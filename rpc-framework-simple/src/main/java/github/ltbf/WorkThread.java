package github.ltbf;

import github.ltbf.dto.RpcRequest;
import github.ltbf.dto.RpcResponse;
import github.ltbf.enumeration.RpcResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.Response;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * @author shkstart
 * @create 2020-09-28 13:26
 */
public class WorkThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RPCServer.class);
    private Socket socket;
    private Object service;

    public WorkThread() {
    }

    public WorkThread(Socket socket, Object service) {
        this.socket = socket;
        this.service = service;
    }

    @Override
    public void run() {
        // try-with-resources
        try(ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())){
            // 读入对象
            RpcRequest rpcRequest = (RpcRequest)ois.readObject();
            Object result = invokeTargetMethod(rpcRequest);
            if(!(result instanceof Response)){
                result = RpcResponse.success(result);
            }
            oos.writeObject(result);
            oos.flush();
        }
        catch(IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
            logger.error("occur excetpin on " + e);
        }
    }
    public Object invokeTargetMethod(RpcRequest rpcRequest) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> cls = Class.forName(rpcRequest.getInterfaceName());
        // 判断类是否实现了对应的接口
        if (!cls.isAssignableFrom(service.getClass())) {
            return RpcResponse.fail(RpcResponseCode.NOT_FOUND_CLASS);
        }
        Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
        if (null == method) {
            return RpcResponse.fail(RpcResponseCode.NOT_FOUND_METHOD);
        }
        return method.invoke(service, rpcRequest.getParameters());
    }
}
