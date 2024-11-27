package org.zys.excempleprovider;//package com.yupi.example.provider;


import model.User;
import service.UserService;

/**
 * 用户服务实现类
 *
 *
 */
public class UserServiceImpl implements UserService {

    public User getUser(User user) {
        user.setName("xixixi");
        System.out.println("用户名：" + user.getName());
        return user;
    }
}