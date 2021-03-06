package github.ltbf.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author shkstart
 * @create 2020-09-28 11:23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable {

    private Integer id;
    private String name;
    private Integer num;
}
