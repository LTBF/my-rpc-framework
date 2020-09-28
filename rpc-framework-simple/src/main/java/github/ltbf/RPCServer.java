package github.ltbf;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author shkstart
 * @create 2020-09-28 11:58
 */

public class RPCServer {

    private static final Logger logger = LoggerFactory.getLogger(RPCServer.class);
    private ExecutorService threadPool;

    public RPCServer(){
        this.threadPool = new ThreadPoolExecutor(10, 100, 1,
                TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(100));
    }

    /**
     * 服务端注册服务
     * */
    public void register(Object service, int port){
        try(ServerSocket serverSocket = new ServerSocket(port);){
            logger.info("server starts...");
            while(true){
                Socket socket = serverSocket.accept();
                logger.info("client connected...");
                threadPool.execute(new WorkThread(socket, service));
            }
        }
        catch (IOException e){
            logger.error("occur IOException:" + e);
        }
    }



}
