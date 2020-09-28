package github.lfbf;

import github.ltbf.RPCClientProxy;
import github.ltbf.dao.User;
import github.ltbf.service.IUserService;

/**
 * @author shkstart
 * @create 2020-09-28 15:44
 */
public class Client {

    public static void main(String[] args) {
        RPCClientProxy rpcClientProxy = new RPCClientProxy("127.0.0.1", 8888);
        IUserService userService = rpcClientProxy.getProxy(IUserService.class);
        User user = userService.findById(111);
        System.out.println(user);
    }
}
