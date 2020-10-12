package github.ltbf.registry;

import java.net.InetSocketAddress;

/**
 * 服务发现接口
 * @author shkstart
 * @create 2020-10-12 16:38
 */
public interface ServiceDiscovery {
    /**
     * 根据服务名查找服务
     * @param serviceName 服务名称
     * @return 服务地址
     */
    InetSocketAddress lookupService(String serviceName);

}
