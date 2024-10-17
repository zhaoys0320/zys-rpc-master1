package registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalRegistry{
    private static final Map<String, Class<?>> registermap = new ConcurrentHashMap<>();

    public static void register(String name,Class<?> implClass){
        registermap.put(name,implClass);
    }
    public static Class<?> get(String serviceName) {
        return registermap.get(serviceName);
    }
    public static void remove(String name){
        registermap.remove(name);
    }
}
