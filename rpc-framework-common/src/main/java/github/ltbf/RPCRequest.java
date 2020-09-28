package github.ltbf;

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
public class RPCRequest implements Serializable {
    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;

}
