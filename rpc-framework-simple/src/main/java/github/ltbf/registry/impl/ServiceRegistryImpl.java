package github.ltbf.registry.impl;

import github.ltbf.registry.ServiceRegistry;
import github.ltbf.util.zk.CuratorHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * zookeeper注册中心，服务注册以及查找
 * @author shkstart
 * @create 2020-09-30 15:23
 */
@Slf4j
public class ServiceRegistryImpl implements ServiceRegistry {

    private CuratorFramework zkClient;

    public ServiceRegistryImpl(){
        // 一个zookeeper客户端连接
        zkClient = CuratorHelper.getZKClient();
        zkClient.start();
    }

    /**
     * 将服务提供者注册到服务中心
     * @param service 服务提供者
     * @param inetSocketAddress 服务提供者地址
     */
    @Override
    public void registerService(Object service, InetSocketAddress inetSocketAddress) {

        String serviceProviderName = service.getClass().getName();
        // 服务提供者实现的所有接口
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if(interfaces.length == 0){
            throw new RuntimeException("服务" + serviceProviderName + "未实现任何接口，无法注册中心注册");
        }

        for (Class<?> anInterface : interfaces) {
            // 根节点下注册子节点：服务
            String servicePath = CuratorHelper.ZK_REGISTER_ROOT_PATH + "/" + anInterface.getCanonicalName();
            // 根节点下注册字节点：服务地址
            servicePath += inetSocketAddress.toString();
            // 创建临时节点
            CuratorHelper.createEphemeraNode(zkClient, servicePath);
        }

        log.info("注册中心服务{}注册成功，其注册的服务：{}" , serviceProviderName, interfaces);

    }

    /**
     * zookeeper注册中心查找服务地址
     * @param serviceName 服务名
     * @return
     */
    @Override
    public InetSocketAddress lookupService(String serviceName) {

        List<String> childrenNodes = CuratorHelper.getChildrenNodes(zkClient, serviceName);

        if(childrenNodes.size() == 0){
            throw new RuntimeException("注册中心，未查到该服务");
        }

        String serviceAddress = childrenNodes.get(0);
        log.info("注册中心查找{}服务成功：" + serviceAddress);

        String[] strs = serviceAddress.split(":");
        return new InetSocketAddress(strs[0], new Integer(strs[1]));


    }
}
