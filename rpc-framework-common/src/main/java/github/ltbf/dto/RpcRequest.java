package github.ltbf.dto;

import lombok.Builder;
import lombok.Data;

import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author shkstart
 * @create 2020-09-28 13:36
 */
@Data
@Builder
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 5937933279954325740L;
    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;

}
