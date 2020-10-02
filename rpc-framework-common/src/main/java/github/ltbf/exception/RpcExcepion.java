package github.ltbf.exception;

import github.ltbf.dto.RpcResponse;
import github.ltbf.enumeration.RpcErrorMessageEnum;

/**
 * @author shkstart
 * @create 2020-10-02 19:31
 */
public class RpcExcepion extends RuntimeException {

    public RpcExcepion(RpcErrorMessageEnum rpcErrorMessageEnum, String message){
        super(rpcErrorMessageEnum.getMessage() + ":" + message);
    }

    public RpcExcepion(String message, Throwable throwable){
        super(message, throwable);
    }

    public RpcExcepion(RpcErrorMessageEnum rpcErrorMessageEnum){
        super(rpcErrorMessageEnum.getMessage());
    }

}
