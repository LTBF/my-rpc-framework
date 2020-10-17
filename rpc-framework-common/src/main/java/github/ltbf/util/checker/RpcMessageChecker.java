package github.ltbf.util.checker;

import github.ltbf.dto.RpcRequest;
import github.ltbf.dto.RpcResponse;
import github.ltbf.enumeration.RpcErrorMessageEnum;
import github.ltbf.enumeration.RpcResponseCode;
import github.ltbf.exception.RpcExcepion;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shkstart
 * @create 2020-10-04 13:24
 */
@Slf4j
public class RpcMessageChecker {

    private static final String INTERFACE_NAME = "interfaceName";

    public static void check(RpcResponse rpcResponse, RpcRequest rpcRequest){

        if(null == rpcResponse){
            log.error("调用服务失败，serviceName:{}", rpcRequest.getInterfaceName());
            throw new RpcExcepion(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if(!rpcResponse.getRequestId().equals(rpcRequest.getRequestId())){
            log.error("调用服务失败，serviceName:{}", rpcRequest.getInterfaceName());
            throw new RpcExcepion(RpcErrorMessageEnum.REQUEST_NOT_MATCH_RESPONSE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if(null == rpcResponse.getCode() || !rpcResponse.getCode().equals(RpcResponseCode.SUCCESS.getCode())){
            log.error("调用服务失败，serviceName:{}", rpcRequest.getInterfaceName());
            throw new RpcExcepion(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }


    }
}
