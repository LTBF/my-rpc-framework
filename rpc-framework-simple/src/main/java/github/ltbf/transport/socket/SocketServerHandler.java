package github.ltbf.transport.socket;

import github.ltbf.dto.RpcRequest;
import github.ltbf.provider.ServiceProvider;
import github.ltbf.transport.RpcRequestHandler;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class SocketServerHandler implements Runnable {
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
            log.error("occur excetpin on " + e);
        }
    }

}
