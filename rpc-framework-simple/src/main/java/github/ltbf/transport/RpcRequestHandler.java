package github.ltbf.transport;

import github.ltbf.dto.RpcRequest;
import github.ltbf.dto.RpcResponse;
import github.ltbf.enumeration.RpcResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.Response;
import java.awt.geom.QuadCurve2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author shkstart
 * @create 2020-10-02 20:36
 */
public class RpcRequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RpcRequestHandler.class);

    public Object handle(RpcRequest rpcRequest, Object service){

        if(service == null){
            return RpcResponse.fail(RpcResponseCode.NOT_FOUND_CLASS, rpcRequest.getRequestId());
        }

        Object result = null;
        try{
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(),
                    rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            result = RpcResponse.success(result, rpcRequest.getRequestId());
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
            logger.error("occur exception on:" + e);
            result = RpcResponse.fail(RpcResponseCode.FAIL, rpcRequest.getRequestId());
        }

        return result;
    }
}
