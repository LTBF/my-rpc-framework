package github.ltbf.transport.netty.client;

import github.ltbf.dto.RpcRequest;
import github.ltbf.dto.RpcResponse;
import github.ltbf.registry.ServiceDiscovery;
import github.ltbf.serialize.Serializer;
import github.ltbf.serialize.kryo.KryoSerializer;
import github.ltbf.transport.ClientTransport;
import github.ltbf.transport.netty.codec.NettyKryoDecoder;
import github.ltbf.transport.netty.codec.NettyKryoEncoder;
import github.ltbf.util.checker.RpcMessageChecker;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.callback.CallbackHandler;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author shkstart
 * @create 2020-10-02 13:14
 */
@AllArgsConstructor
public class NettyClientTransport implements ClientTransport {

    private static final Logger logger = LoggerFactory.getLogger(NettyClientTransport.class);

    private ServiceDiscovery serviceDiscovery;

    /**
     * 发送消息
     * */
    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {

        // 原子引用
        AtomicReference<Object> result = new AtomicReference<>();

        try{
            //服务发现
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
            // 连接到服务端，并返回通道
            Channel channel = ChannelProvider.get(inetSocketAddress);
            if(null != channel){
                channel.writeAndFlush(rpcRequest).addListener(future -> {
                    if(future.isSuccess()){
                       logger.info("客户端发送请求成功：%s", rpcRequest.toString());
                   }
                   else{
                       logger.info("客户端发送请求失败:" + future.cause());
                   }
                });

                // 异步监听通道关闭
                channel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse" + rpcRequest.getRequestId());
                RpcResponse rpcResponse = channel.attr(key).get();

                // 校验RpcRequest和RpcResponse
                RpcMessageChecker.check(rpcResponse, rpcRequest);
                result.set(rpcResponse.getData());
            }

        }
        catch (InterruptedException e){
            logger.error("occur exception on:" + e);
        }
        return result.get();
    }
}
