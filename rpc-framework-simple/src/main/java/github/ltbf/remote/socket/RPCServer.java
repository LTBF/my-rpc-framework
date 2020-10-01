package github.ltbf.remote.socket;

import github.ltbf.registry.IServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * @author shkstart
 * @create 2020-09-28 11:58
 */

public class RPCServer {

    private static final Logger logger = LoggerFactory.getLogger(RPCServer.class);
    private ExecutorService threadPool;
    private IServiceRegistry serviceRegistry;

    public RPCServer() {
    }

    public RPCServer(IServiceRegistry serviceRegistry){

        BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(100);

        this.threadPool = new ThreadPoolExecutor(10, 100, 1,
                TimeUnit.MINUTES, blockingQueue);
        this.serviceRegistry = serviceRegistry;
    }

    /**
     * 服务端开启服务
     * */
    public void start(int port){

        try(ServerSocket serverSocket = new ServerSocket(port)){
            logger.info("server started...");
            Socket socket;
            while((socket = serverSocket.accept()) != null){   // 为了shutdown线程池
                logger.info("one client connected...");
                threadPool.execute(new WorkThread(socket, serviceRegistry));

            }
            threadPool.shutdown();
        }
        catch(IOException e){
            logger.error("occur IOException:" + e);
        }
    }



}
