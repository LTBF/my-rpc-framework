package github.ltbf;

import github.ltbf.registry.IServiceRegistry;
import github.ltbf.registry.impl.DefaultServiceRegistry;
import github.ltbf.remote.socket.RPCServer;
import github.ltbf.service.IProductService;
import github.ltbf.service.IUserService;
import github.ltbf.service.impl.ProductServiceImpl;
import github.ltbf.service.impl.UserServiceImpl;


/**
 * @author shkstart
 * @create 2020-09-28 11:51
 */
public class ServerClient {

    public static void main(String[] args) {

        // 服务注册
        //todo Map记录服务，实现多服务，包扫描
        IUserService userService = new UserServiceImpl();
        IProductService productService = new ProductServiceImpl();
        IServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(userService);
        serviceRegistry.register(productService);

        // 开启服务
        RPCServer rpcServer = new RPCServer(serviceRegistry);
        rpcServer.start(8888);

    }
}
