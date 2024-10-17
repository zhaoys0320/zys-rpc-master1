package registry;



import com.google.common.primitives.Bytes;
import model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册中心服务本地缓存
 */
public class RegistryServiceCacheMilt {

    /**
     * 服务缓存
     */
//    List<ServiceMetaInfo> serviceCache;

    Map<String, List<ServiceMetaInfo>> serviceCache = new ConcurrentHashMap<>();

    /**
     * 写缓存
     *
     * @param newServiceCache
     * @return
     */
    void writeCache(String ServiceKey,List<ServiceMetaInfo> newServiceCache) {
        this.serviceCache.put(ServiceKey,newServiceCache);
    }

    /**
     * 读缓存
     *
     * @return
     */
    List<ServiceMetaInfo> readCache(String ServiceKey) {
        return serviceCache.get(ServiceKey);
    }

    /**
     * 清空缓存
     */
    void clearCache(String ServiceKey) {
        this.serviceCache.remove(ServiceKey);
    }
}