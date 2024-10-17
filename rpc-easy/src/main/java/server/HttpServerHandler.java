package server;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import model.RpcRequest;
import model.RpcResponse;
import registry.LocalRegistry;
import serializer.JdkSerializer;
import serializer.Serializer;

import java.io.IOException;
import java.lang.reflect.Method;

public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {
        //    反序列化请求为对象，并从请求对象中获取参数。
        final Serializer serializer = new JdkSerializer();
        System.out.println("Received request: " + request.method() + " " + request.uri());
        request.bodyHandler(body->{
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            RpcResponse rpcResponse = new RpcResponse();
            // 如果请求为 null，直接返回
            if (rpcRequest == null) {
                rpcResponse.setMessage("rpcRequest is null");
                doResponse(request, rpcResponse, serializer);
                return;
            }

            String serviceName = rpcRequest.getServiceName();
            //    根据服务名称从本地注册器中获取到对应的服务实现类。
            Class<?> aClass = LocalRegistry.get(serviceName);
            try {
                //    根据服务名称从本地注册器中获取到对应的服务实现类。
                Method method = aClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                //    通过反射机制调用方法，得到返回结果。
                Object result = method.invoke(aClass.newInstance(),rpcRequest.getArgs());
                //    对返回结果进行封装和序列化，并写入到响应中。
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            doResponse(request, rpcResponse, serializer);

        });
    }

    void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse httpServerResponse = request.response()
                .putHeader("content-type", "application/json");
        try {
            // 序列化
            byte[] serialized = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serialized));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
