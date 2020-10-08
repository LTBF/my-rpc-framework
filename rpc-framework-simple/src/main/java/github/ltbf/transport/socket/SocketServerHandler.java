package github.ltbf.transport.socket;

import github.ltbf.dto.RpcRequest;
import github.ltbf.provider.ServiceProvider;
import github.ltbf.registry.ServiceRegistry;
import github.ltbf.transport.RpcRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author shkstart
 * @create 2020-09-28 13:26
 */
public class SocketServerHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(SocketRpcServer.class);
    private Socket socket;
    private ServiceProvider serviceProvider;
    private RpcRequestHandler rpcRequestHandler;

    public SocketServerHandler() {
    }

    public SocketServerHandler(Socket socket, ServiceProvider serviceProvider) {
        this.socket = socket;
        this.serviceProvider = serviceProvider;
        rpcRequestHandler = new RpcRequestHandler();
    }

    @Override
    public void run() {
        // try-with-resources
        try(ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())){
            // 读入对象
            RpcRequest rpcRequest = (RpcRequest)ois.readObject();
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceProvider.getServiceProvider(interfaceName);
            Object result = rpcRequestHandler.handle(rpcRequest, service);
            oos.writeObject(result);
            oos.flush();
        }
        catch(IOException | ClassNotFoundException  e) {
            logger.error("occur excetpin on " + e);
        }
    }

}
