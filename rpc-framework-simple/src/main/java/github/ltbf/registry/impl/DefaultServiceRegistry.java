package github.ltbf.registry.impl;

import github.ltbf.enumeration.RpcErrorMessageEnum;
import github.ltbf.exception.RpcExcepion;
import github.ltbf.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shkstart
 * @create 2020-09-30 15:23
 */
public class DefaultServiceRegistry implements ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(DefaultServiceRegistry.class);

    // 注册的服务保存在类上
    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    /**
     * TODO 修改为扫描实现注册
     * 将service实现的所有接口都注册进去
     * */

    @Override
    public synchronized <T> void register(T service) {
        // 返回由Java语言规范定义的基础类的规范名称
        String serviceName = service.getClass().getCanonicalName();

        // 服务已注册，直接返回
        if(registeredService.contains(serviceName)){
            return;
        }

        // 获取服务类实现的所有接口
        Class<?>[] interfaces = service.getClass().getInterfaces();
        // 服务未实现任何接口
        if(interfaces.length == 0){
            throw new RpcExcepion(RpcErrorMessageEnum.SERVICE_NOT_IMMPLEMENT_ANY_INTERFACE);
        }
        // 注册服务
        for (Class<?> anInterface : interfaces) {
            serviceMap.put(anInterface.getCanonicalName(), service);
        }

        // 记录服务注册
        registeredService.add(serviceName);
        logger.info("Add service:{} and interfaces:{}", serviceName, service.getClass().getInterfaces());
    }

    /**
     * 根据接口名，获取服务
     * */
    @Override
    public synchronized Object getService(String interfaceName) {

        Object service = serviceMap.get(interfaceName);
        // 未注册服务
        if(null == service){
            throw new RpcExcepion(RpcErrorMessageEnum.SERVICE_NOT_FOUND);
        }

        return service;
    }
}
