package github.ltbf.service.impl;

import github.ltbf.dao.Product;
import github.ltbf.service.IProductService;

import java.lang.reflect.Proxy;

/**
 * @author shkstart
 * @create 2020-09-28 11:39
 */
public class ProductServiceImpl implements IProductService {
    public Product selectNumById(Integer id) {

        Product product = Product.builder().id(111).name("手机").num(1000).build();

        return product;
    }
}
