package github.ltbf.service.impl;

import github.ltbf.dao.User;
import github.ltbf.service.IUserService;

/**
 * @author shkstart
 * @create 2020-09-28 11:39
 */
public class UserServiceImpl implements IUserService {
    public User findById(Integer id) {

        System.out.println(id);
        User user = User.builder().id(id).age(11).name("张三").build();

        return user;
    }
}
