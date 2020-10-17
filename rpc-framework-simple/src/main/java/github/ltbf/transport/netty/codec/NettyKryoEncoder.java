package github.ltbf.transport.netty.codec;

import github.ltbf.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shkstart
 * @create 2020-10-02 15:24
 */
@AllArgsConstructor
@Slf4j
public class NettyKryoEncoder extends MessageToByteEncoder {

    private Serializer serializer;
    private Class<?> genericClass;


    /**
     * Object --> ByteBuf
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {

        if(genericClass.isInstance(o)){
            // 1.对象转换为byte[]
            byte[] bytes = serializer.serialize(o);
            // 2.读取消息长度
            int dataLength = bytes.length;
            // 3.写入消息长度
            byteBuf.writeInt(dataLength);
            // 4.写入消息
            byteBuf.writeBytes(bytes);
        }


    }
}
