package github.ltbf.service;

import github.ltbf.dao.Product;

/**
 * @author shkstart
 * @create 2020-09-28 11:36
 */
public interface IProductService {

    public Product selectNumById(Integer id);
}
