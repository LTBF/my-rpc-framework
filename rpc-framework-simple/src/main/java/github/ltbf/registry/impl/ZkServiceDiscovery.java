package github.ltbf.registry.impl;

import github.ltbf.registry.ServiceDiscovery;
import github.ltbf.util.zk.CuratorHelper;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * zookeeper服务发现实现类
 * @author shkstart
 * @create 2020-10-12 16:41
 */
public class ZkServiceDiscovery implements ServiceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(ZkServiceDiscovery.class);

    private final CuratorFramework zkClient;

    public ZkServiceDiscovery(){
        this.zkClient = CuratorHelper.getZKClient();
        zkClient.start();
    }

    /**
     * TODO 负载均衡
     * 根据服务名，查找zookeeper返回一个服务提供者，这里找的是第一个
     * @param serviceName 服务名称
     * @return
     */
    @Override
    public InetSocketAddress lookupService(String serviceName) {

        String serviceAddress = CuratorHelper.getChildrenNodes(zkClient, serviceName).get(0);
        logger.info("成功找到服务地址：", serviceAddress);
        String[] strs = serviceAddress.split(":");
        return new InetSocketAddress(strs[0], Integer.valueOf(strs[1]));
    }
}
