package fault;

import spi.SpiLoader;

/**
 * 负载均衡器工厂（工厂模式，用于获取负载均衡器对象）
 *
 */
public class RetryStrategyFactory {

    static {
        SpiLoader.load(RetryStrategy.class);
    }

    /**
     * 默认负载均衡器
     */
    private static final RetryStrategy  DEFAULT_LOAD_BALANCER = new NoRetryStrategy();

    /**
     * 获取实例
     *
     * @param key
     * @return
     *
     */
    public static RetryStrategy  getInstance(String key) {
        return SpiLoader.getInstance(RetryStrategy.class, key);
    }

}