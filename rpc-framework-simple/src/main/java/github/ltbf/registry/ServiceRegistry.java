package github.ltbf.registry;

import java.net.InetSocketAddress;

/**
 * @author shkstart
 * @create 2020-09-30 15:22
 * zookeeper注册中心的接口，负责注册服务以及查找服务
 */
public interface ServiceRegistry {

    /**
     * 服务中心注册服务
     * @param service 服务提供者
     * @param inetSocketAddress 服务提供者地址
     */
    void registerService(Object service, InetSocketAddress inetSocketAddress);

    /**
     * 服务中心查找服务
     * @param serviceName 服务名
     * @return
     */
    InetSocketAddress lookupService(String serviceName);
}
