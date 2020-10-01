package github.ltbf.remote.socket;

import github.ltbf.dto.RpcRequest;
import github.ltbf.dto.RpcResponse;
import github.ltbf.enumeration.RpcResponseCode;
import github.ltbf.registry.IServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.spi.RegisterableService;
import javax.imageio.spi.ServiceRegistry;
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
    private IServiceRegistry serviceRegistry;

    public WorkThread() {
    }

    public WorkThread(Socket socket, IServiceRegistry serviceRegistry) {
        this.socket = socket;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void run() {
        // try-with-resources
        try(ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())){
            // 读入对象
            RpcRequest rpcRequest = (RpcRequest)ois.readObject();
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object result = handle(rpcRequest, service);
            oos.writeObject(result);
            oos.flush();
        }
        catch(IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException  e) {
            logger.error("occur excetpin on " + e);
        }
    }

    private Object handle(RpcRequest rpcRequest, Object service) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {

        // 未注册的服务
        if(service == null){
            return RpcResponse.fail(RpcResponseCode.NOT_FOUND_CLASS);
        }

        Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
        // 未实现的方法
        if(method == null){
            return RpcResponse.fail(RpcResponseCode.NOT_FOUND_METHOD);
        }
        // 调用方法
        Object result = method.invoke(service, rpcRequest.getParameters());
        logger.info("server call method[" + method.getName() + "] success...");

        return RpcResponse.success(result);
    }
}
