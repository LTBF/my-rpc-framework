package github.ltbf.transport;

import github.ltbf.dto.RpcRequest;

/**
 * @author shkstart
 * @create 2020-10-02 10:49
 */
public interface ClientTransport {

    /**
     * 向服务端发送请求
     * @param rpcRequest  请求
     * @return    响应
     */
    Object sendRpcRequest(RpcRequest rpcRequest);
}
