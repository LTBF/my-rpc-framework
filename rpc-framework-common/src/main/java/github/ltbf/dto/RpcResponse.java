package github.ltbf.dto;

import github.ltbf.enumeration.RpcResponseCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author shkstart
 * @create 2020-09-29 14:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse<T> implements Serializable {

    private static final long serialVersionUID = -6489912619852961642L;

    private String requestId;

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

    public static <T> RpcResponse<T> success(T data, String requestId){
        return new RpcResponse<T>(requestId, RpcResponseCode.SUCCESS.getCode(),
                RpcResponseCode.SUCCESS.getMessage(), data);
    }

    public static <T> RpcResponse<T> fail(RpcResponseCode rpcResponseCode, String requestId){
        return new RpcResponse<T>(requestId, rpcResponseCode.getCode(),
                rpcResponseCode.getMessage(), null);
    }

}
