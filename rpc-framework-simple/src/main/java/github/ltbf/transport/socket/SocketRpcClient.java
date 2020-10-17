package github.ltbf.transport.socket;

import github.ltbf.dto.RpcRequest;
import github.ltbf.dto.RpcResponse;
import github.ltbf.enumeration.RpcResponseCode;
import github.ltbf.transport.ClientTransport;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author shkstart
 * @create 2020-09-29 14:38
 */
@AllArgsConstructor
@Slf4j
public class SocketRpcClient implements ClientTransport {

    private String host;
    private int port;

    public Object sendRpcRequest(RpcRequest rpcRequest){

        try(Socket socket = new Socket(host, port)){
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(rpcRequest);
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            RpcResponse rpcResponse = (RpcResponse)ois.readObject();
            if(rpcResponse == null){
                log.error("调用服务失败,serviceName:" + rpcRequest.getInterfaceName());
                // throw excepiton
            }
            if(rpcResponse.getCode() == null || !rpcResponse.getCode().equals(RpcResponseCode.SUCCESS.getCode())){
                log.error("调用服务失败,serviceName:" + rpcRequest.getInterfaceName());
                // throw excepiton
            }

            return rpcResponse.getData();
        }
        catch (IOException | ClassNotFoundException e){
            // throw excepiton
        }

        return null;
    }
}
