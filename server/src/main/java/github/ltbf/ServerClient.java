package github.ltbf;

import github.ltbf.service.IUserService;
import github.ltbf.service.impl.UserServiceImpl;
import org.slf4j.Logger;


/**
 * @author shkstart
 * @create 2020-09-28 11:51
 */
public class ServerClient {

    public static void main(String[] args) {

        RPCServer rpcServer = new RPCServer();
        IUserService userService = new UserServiceImpl();
        rpcServer.register(userService, 8888);

    }
}