package org.zys.excempleprovider;//package com.yupi.example.provider;


import model.User;
import service.UserService;

/**
 * 用户服务实现类
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @learn <a href="https://codefather.cn">编程宝典</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public class UserServiceImpl implements UserService {

    public User getUser(User user) {
        user.setName("xixixi");
        System.out.println("用户名：" + user.getName());
        return user;
    }
}