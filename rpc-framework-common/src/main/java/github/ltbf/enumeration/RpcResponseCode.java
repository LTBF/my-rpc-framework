package github.ltbf.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author shkstart
 * @create 2020-09-29 12:27
 */
@AllArgsConstructor
@Getter
@ToString
public enum RpcResponseCode {

    SUCCESS(200, "调用方法成功"),
    FAIL(500,"调用方法失败"),
    NOT_FOUND_METHOD(501,"未找到指定方法"),
    NOT_FOUND_CLASS(502,"未找到指定类");


    private final int code;
    private final String message;



}
