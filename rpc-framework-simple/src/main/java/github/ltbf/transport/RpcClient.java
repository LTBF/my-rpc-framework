package github.ltbf.transport;

import github.ltbf.dto.RpcRequest;

/**
 * @author shkstart
 * @create 2020-10-02 10:49
 */
public interface RpcClient {

    /**
     * 向服务端发送请求
     * @param rpcRequest
     * @param host
     * @param port
     * @return
     */
    Object sendRpcRequest(RpcRequest rpcRequest);
}
