package github.ltbf.provider.impl;

import github.ltbf.enumeration.RpcErrorMessageEnum;
import github.ltbf.exception.RpcExcepion;
import github.ltbf.provider.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shkstart
 * @create 2020-10-08 18:42
 * 服务提供者的实现类，负责维护本机所提供的服务，包括服务添加，以及查找
 */
public class ServiceProviderImpl implements ServiceProvider {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderImpl.class);

    // 一个Map对象，key是接口名, value是其对应的service[服务]，线程安全
    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    // 一个Set对象，存放本机service提供者, 线程安全
    private static final Set<String> serviceSet = ConcurrentHashMap.newKeySet();


    /**
     * 添加一个服务
     * @param service 服务的提供者[]
     * @param <T>
     */
    @Override
    public <T> void addServiceProvider(T service) {
        // 服务提供者的名称
        String serviceProviderName = service.getClass().getCanonicalName();
        // 已经有该服务提供者，无需添加
        if(serviceSet.contains(serviceProviderName)){
            return;
        }

        // 获取该服务提供者实现的所有接口，即服务名
        Class<?>[] interfaces = service.getClass().getInterfaces();
        // 未实现任何接口，该服务提供者不能添加
        if(interfaces.length == 0){
            throw new RpcExcepion(RpcErrorMessageEnum.SERVICE_NOT_IMMPLEMENT_ANY_INTERFACE);
        }
        // 为每个接口，指定服务提供者
        for (Class<?> anInterface : interfaces) {
            serviceMap.put(anInterface.getCanonicalName(), service);
        }

        // 将该服务提供者放入Set记录
        serviceSet.add(serviceProviderName);

        logger.info("添加服务提供者{}成功,其实现接口：{}", serviceProviderName, interfaces);

    }

    /**
     * 根据接口名，获取服务提供者
     * @param serviceName 服务名，即所需服务提供者所实现的接口的名字
     * @return
     */
    @Override
    public Object getServiceProvider(String serviceName) {

        Object service = serviceMap.get(serviceName);
        if(service == null){
            throw new RpcExcepion(RpcErrorMessageEnum.SERVICE_NOT_FOUND);
        }

        return service;

    }
}
