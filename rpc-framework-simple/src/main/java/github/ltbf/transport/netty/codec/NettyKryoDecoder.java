package github.ltbf.transport.netty.codec;

import github.ltbf.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author shkstart
 * @create 2020-10-02 15:24
 */
@AllArgsConstructor
@Slf4j
public class NettyKryoDecoder extends ByteToMessageDecoder {

    private Serializer serializer;
    private Class<?> genericClass;

    /**
     * Netty传输数据时，用于存储消息长度所需的字节数，存放在ByteBuf的头部
     * */
    private static final int BODY_LENGTH = 4;


    /**
     * ByteBuf --> Object
     */
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        // 1.byteBuf中的数据长度必须要大于BODY_LENGHT
        if(byteBuf.readableBytes() >= BODY_LENGTH){
            // 2.标记readIndex的位置
            byteBuf.markReaderIndex();
            // 3.读取消息长度
            // 消息长度时encoder时，自己写入的，也就是byteBuf的头部四字节
            int dataLength = byteBuf.readInt();
            // 4.不合理的情况, 直接返回
            if(dataLength < 0 || byteBuf.readableBytes() < 0){
                return;
            }
            // 5.可读消息长度小于应该的长度，说明是不完整的消息, 重置readIndex,下次继续读
            if(byteBuf.readableBytes() < dataLength){
                byteBuf.resetReaderIndex();
                return;
            }

            // 6.数据可以正确读取，数据进行反序列化
            byte[] bytes = new byte[dataLength];
            byteBuf.readBytes(bytes);
            Object obj = serializer.deserialize(bytes, genericClass);
            list.add(obj );    // ????
        }

    }
}
