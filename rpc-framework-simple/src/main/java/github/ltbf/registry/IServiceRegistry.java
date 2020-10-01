package github.ltbf.registry;

/**
 * @author shkstart
 * @create 2020-09-30 15:22
 */
public interface IServiceRegistry {

    <T> void register(T service);
    Object getService(String interfaceName);

}
