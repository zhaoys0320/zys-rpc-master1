package org.zys.excempleconsumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import model.RpcRequest;
import model.RpcResponse;
import model.User;
import serializer.JdkSerializer;
import serializer.Serializer;
import service.UserService;

import java.io.IOException;

public class UserServiceProxy implements UserService {
    @Override
    public User getUser(User user) {
        Serializer serializer = new JdkSerializer();


        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();

        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                    .body(bodyBytes)
                    .execute()) {
                result = httpResponse.bodyBytes();
            }
            RpcResponse deserialize = serializer.deserialize(result, RpcResponse.class);
            return (User)deserialize.getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
