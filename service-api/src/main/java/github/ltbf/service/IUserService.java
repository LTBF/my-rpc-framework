package github.ltbf.service;

import github.ltbf.dao.User;

/**
 * @author shkstart
 * @create 2020-09-28 11:36
 */
public interface IUserService {

    public User findById(Integer id);

}
