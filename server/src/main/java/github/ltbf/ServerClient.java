package github.ltbf;

import github.ltbf.registry.ServiceRegistry;
import github.ltbf.registry.impl.ServiceRegistryImpl;
import github.ltbf.transport.netty.server.NettyServerTransport;
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

        // 服务端
        NettyServerTransport server = new NettyServerTransport("127.0.0.1",8888);

        // 服务注册
        IUserService userService = new UserServiceImpl();
        IProductService productService = new ProductServiceImpl();
        server.publicService(userService);
        server.publicService(productService);

        // 开启服务
        server.start();

    }
}
