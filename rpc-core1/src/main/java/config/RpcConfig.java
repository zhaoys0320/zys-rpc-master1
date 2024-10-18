package config;

import balancer.LoadBalancerKeys;
import fault.RetryStrategyKeys;
import lombok.Data;

/**
 * RPC 框架配置
 */
@Data
public class RpcConfig {

    /**
     * 名称
     */
    private String name = "yu-rpc";

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 服务器主机名
     */
    private String serverHost = "localhost";
    
    /**
     * 服务器端口号
     */
    private Integer serverPort = 8080;

    private RegistryConfig registryConfig = new RegistryConfig();

    /**
     * 负载均衡器
     */
    private String loadBalancer = LoadBalancerKeys.CONSISTENT_HASH;

    /**
     * 重试策略
     */
    private String RetryStrategy = RetryStrategyKeys.FIXED_INTERVAL;

}