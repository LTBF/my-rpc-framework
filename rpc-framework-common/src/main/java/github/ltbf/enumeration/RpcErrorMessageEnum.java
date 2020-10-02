package github.ltbf.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author shkstart
 * @create 2020-10-02 19:34
 * Rpc框架服务调用错误
 *
 */

@AllArgsConstructor
@Getter
public enum RpcErrorMessageEnum {
    SERVICE_INVOCATION_FAILURE("服务调用失败"),
    SERVICE_NOT_FOUND("没有找到指定服务"),
    SERVICE_NOT_IMMPLEMENT_ANY_INTERFACE("服务未实现任何接口");
    private String message;
}
