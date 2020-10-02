package github.ltbf.serialize;

/**
 * @author shkstart
 * @create 2020-10-02 10:16
 */
public interface Serializer {

    /**
     *
     * 对象序列化为字节数组
     * */
    byte[] serialize(Object object);

    /**
     * 字节数组反序列化为对象
     * */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
