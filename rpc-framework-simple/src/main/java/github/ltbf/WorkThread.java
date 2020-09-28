package github.ltbf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.PriorityQueue;

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
            RPCRequest rpcRequest = (RPCRequest)ois.readObject();
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            Object result = method.invoke(service, rpcRequest.getParameters());
            oos.writeObject(result);
            oos.flush();
        }
        catch(IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
            logger.error("excetpin on " + e);
        }
    }

    /**
     * 能否根据接口名，获取实现类，提供多种服务???
     * */
    public void runTest(){
        try(ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream()) ){

            // 读入对象
            RPCRequest rpcRequest = (RPCRequest)ois.readObject();

        }
        catch(IOException | ClassNotFoundException e){
            logger.error("excetpin on " + e);
        }
    }

}
