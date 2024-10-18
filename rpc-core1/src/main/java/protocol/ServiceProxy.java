package protocol;

import balancer.LoadBalancer;
import balancer.LoadBalancerFactory;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import config.RegistryConfig;
import config.RpcApplication;
import config.RpcConfig;
import constant.RpcConstant;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;
import model.RpcRequest;
import model.RpcResponse;
import model.ServiceMetaInfo;
import registry.EtcdRegistry;
import registry.Registry;
import serializer.JdkSerializer;
import serializer.Serializer;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * 服务代理（JDK 动态代理）
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @learn <a href="https://codefather.cn">编程宝典</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public class ServiceProxy implements InvocationHandler {


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //指定序列化器
        final Serializer serializer = new JdkSerializer();

        //构造请求

        String serviceName =  method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try{
            //将请求序列化
            byte[] serialize = serializer.serialize(rpcRequest);
            // 从注册中心获取服务提供者请求地址
            RpcConfig rpcConfig = new RpcConfig();
            RegistryConfig registryConfig = new RegistryConfig();
            EtcdRegistry registry = new EtcdRegistry();
            registry.init(registryConfig);
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if(serviceMetaInfoList.isEmpty()){
                throw new RuntimeException("暂无服务地址");
            }
            LoadBalancer balancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("methodName", rpcRequest.getMethodName());
            ServiceMetaInfo selectedServiceMetaInfo  = balancer.select(requestParams, serviceMetaInfoList);

            //发送TCP请求
            Vertx vertx = Vertx.vertx();
            NetClient netClient = vertx.createNetClient();
            //TCP为异步 CompletableFuture转换为同步
            CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
            netClient.connect(serviceMetaInfo.getServicePort(),selectedServiceMetaInfo.getServiceAddress(),result ->{
                if(result.succeeded()){
                    System.out.println("Connected to TCP server");
                    io.vertx.core.net.NetSocket socket = result.result();
                    ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                    ProtocolMessage.Header header = new ProtocolMessage.Header();
                    header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                    header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                    header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                    header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                    header.setRequestId(IdUtil.getSnowflakeNextId());
                    protocolMessage.setHeader(header);
                    protocolMessage.setBody(rpcRequest);
                    // 编码请求
                    try {
                        Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
                        socket.write(encodeBuffer);
                    } catch (IOException e) {
                        throw new RuntimeException("协议消息编码错误");
                    }
                    socket.handler(buffer -> {
                        try {
                            ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                            responseFuture.complete(rpcResponseProtocolMessage.getBody());
                        } catch (IOException e) {
                            throw new RuntimeException("协议消息解码错误");
                        }

                    });
                }else {
                    System.err.println("Failed to connect to TCP server");
                }
            } );
            RpcResponse rpcResponse = responseFuture.get();
            return rpcResponse.getData();
        }catch (Exception e){
        }
        return null;
    }
}