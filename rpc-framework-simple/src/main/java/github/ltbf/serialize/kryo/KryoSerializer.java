package github.ltbf.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import github.ltbf.dto.RpcRequest;
import github.ltbf.dto.RpcResponse;
import github.ltbf.serialize.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author shkstart
 * @create 2020-10-02 10:20
 * Kryo实现序列化
 */
public class KryoSerializer implements Serializer {

    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);

    /**
     * Kryo是非线程安全的，所以每个线程必须都要有自己的Kryo对象
     * 下列代码，当每个线程访问时，ThreadLocal都会为线程创建一份变量
     */
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        kryo.setReferences(true);//默认值为true,是否关闭注册行为,关闭之后可能存在序列化问题，一般推荐设置为 true
        kryo.setRegistrationRequired(false);//默认值为false,是否关闭循环引用，可以提高性能，但是一般不推荐设置为 true

        return kryo;
    });


    @Override
    public byte[] serialize(Object object) {

        try(ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            Output output = new Output(byteOutputStream);) {

            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, object);

            kryoThreadLocal.remove();   // 删除
            return output.toBytes();
        }
        catch(Exception e){
            logger.error("occur exception when serialize:" + e);
            // 序列化失败异常
            throw new RuntimeException("序列化失败");
        }

    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {

        try(ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
            Input input = new Input(byteInputStream);){

            Kryo kryo = kryoThreadLocal.get();
            T t = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return t;
        }
        catch(Exception e){
            logger.error("occur exception when deserialize:" + e);
            // 序列化失败异常
            throw new RuntimeException("序列化失败");
        }
    }
}
