package github.lfbf;

import github.ltbf.dao.Product;
import github.ltbf.transport.ClientTransport;
import github.ltbf.transport.netty.client.NettyClientTransport;
import github.ltbf.transport.RpcClientProxy;
import github.ltbf.service.IProductService;

import java.net.InetSocketAddress;

/**
 * @author shkstart
 * @create 2020-09-28 15:44
 */
public class Client {

    public static void main(String[] args) {

        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 8888);
        ClientTransport rpcClient = new NettyClientTransport(inetSocketAddress);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient);
        IProductService productService = rpcClientProxy.getProxy(IProductService.class);
        Product product = productService.selectNumById(1010);
        System.out.println(product);
    }
}
