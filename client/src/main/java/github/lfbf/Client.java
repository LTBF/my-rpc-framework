package github.lfbf;

import github.ltbf.dao.Product;
import github.ltbf.transport.RpcClient;
import github.ltbf.transport.netty.NettyRpcClient;
import github.ltbf.transport.socket.RpcClientProxy;
import github.ltbf.service.IProductService;

/**
 * @author shkstart
 * @create 2020-09-28 15:44
 */
public class Client {

    public static void main(String[] args) {

        RpcClient rpcClient = new NettyRpcClient("127.0.0.1", 8888);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient);
        /*
        IUserService userService = rpcClientProxy.getProxy(IUserService.class);
        User user = userService.findById(111);
        System.out.println(user);
        */
        IProductService productService = rpcClientProxy.getProxy(IProductService.class);
        Product product = productService.selectNumById(1010);
        System.out.println(product);
    }
}
