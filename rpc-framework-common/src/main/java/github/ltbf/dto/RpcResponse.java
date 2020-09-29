package github.ltbf.dto;

import github.ltbf.enumeration.RpcResponseCode;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author shkstart
 * @create 2020-09-29 14:53
 */
@Data
@AllArgsConstructor
public class RpcResponse<T> implements Serializable {

    private static final long serialVersionUID = -6489912619852961642L;

    /**
     * 响应码
     * */
    private Integer code;

    /**
     * 响应消息
     * */
    private String message;

    /**
     * 响应数据
     * */
    private T data;

    public static <T> RpcResponse<T> success(T data){
        return new RpcResponse<T>(RpcResponseCode.SUCCESS.getCode(),
                RpcResponseCode.SUCCESS.getMessage(), data);
    }

    public static <T> RpcResponse<T> fail(RpcResponseCode rpcResponseCode){
        return new RpcResponse<T>(rpcResponseCode.getCode(),
                rpcResponseCode.getMessage(), null);
    }

}
