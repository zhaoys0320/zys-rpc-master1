package org.zys.excempleconsumer;

import config.RpcConfig;
import model.User;
import proxy.ServiceProxyFactory;
import service.UserService;
import utils.ConfigUtils;

public class EasyConsumerExample {
    public static void main(String[] args) {
//        // 静态代理
//        UserService userService = new UserServiceProxy();
//
//        ...
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc);
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("yupi");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
    }
}
