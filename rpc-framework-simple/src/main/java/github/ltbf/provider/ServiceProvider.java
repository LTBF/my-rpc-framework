package github.ltbf.provider;

/**
 * @author shkstart
 * @create 2020-10-08 18:35
 * 服务提供者接口，维护本机所提供的服务
 * 服务提供者指的是一个实现了指定接口的service类
 */
public interface ServiceProvider {

    /**
     * 保存服务提供者
     */
    <T> void addServiceProvider(T service);

    /**
     * 获取服务
     */
    Object getServiceProvider(String serviceName);

}
