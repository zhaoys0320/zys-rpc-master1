什么是RPC？
全称：remote procedure call 远程过程调用

作用：像调用本地方法一样调用远程方法，而不需要了解数据的传输处理过程、底层网络协议等细节，可以快速开发分布式系统

定义：分布式系统中跨网络进行通信的技术，RPC是一种技术机通信协议

优势：性能好，采用高效的网络协议和序列化机制；额外功能，例如负载均衡、服务发现、容错机制等；支持动态扩展，可以自定义负载均衡器、自定义序列化协议

为什么需要RPC
简化调用，服务者不需要每个方法都写一个http请求
