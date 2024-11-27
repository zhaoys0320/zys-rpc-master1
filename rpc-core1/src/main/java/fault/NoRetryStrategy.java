package fault;

import lombok.extern.slf4j.Slf4j;
import model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 不重试 - 重试策略
 *
*
 */
@Slf4j
public class NoRetryStrategy implements RetryStrategy {

    /**
     * 重试
     *
     * @param callable
     * @return
     * @throws Exception
     */
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        return callable.call();
    }

}