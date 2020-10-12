package github.lfbf;

import github.ltbf.dao.Product;
import github.ltbf.registry.ServiceDiscovery;
import github.ltbf.registry.impl.ZkServiceDiscovery;
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

        // 服务发现类
        ServiceDiscovery serviceDiscovery = new ZkServiceDiscovery();
        // 数据传输方式：Socket / Netty
        ClientTransport rpcClient = new NettyClientTransport(serviceDiscovery);
        // 代理对象Handler类
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient);
        // 代理对象
        IProductService productService = rpcClientProxy.getProxy(IProductService.class);
        // 调用代理对象方法
        Product product = productService.selectNumById(1010);

        System.out.println(product);
    }
}
