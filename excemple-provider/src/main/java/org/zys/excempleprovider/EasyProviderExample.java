package org.zys.excempleprovider;


import config.RegistryConfig;
import config.RpcApplication;
import config.RpcConfig;
import model.ServiceMetaInfo;
import org.zys.rpccore1.RpcCore1Application;
import protocol.VertxTcpServer;
import registry.EtcdRegistry;
import registry.LocalRegistry;
import registry.Registry;
import rpceasy.RpcEasyApplication;
import server.HttpServer;
import server.VertexHttpServer;
import service.UserService;

/**
 * 简易服务提供者示例
 */
public class EasyProviderExample {

    public static void main(String[] args) {

        RpcApplication.init();
        // 提供服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        String serviceName = UserService.class.getName();


        // 注册服务到注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = new EtcdRegistry();
        registry.init(registryConfig);
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //启动web
//        HttpServer httpServer = new VertexHttpServer();
//        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(8088);
    }
}