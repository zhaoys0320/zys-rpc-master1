package server;
import io.vertx.core.Vertx;
public class VertexHttpServer implements HttpServer{

    @Override
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();
        //监听端口，输出结果
        HttpServerHandler httpServerHandler = new HttpServerHandler();
        server.requestHandler(httpServerHandler);
        server.listen(port,result -> {
            if (result.succeeded()) {
                System.out.println("Server is now listening on port " + port);
            } else {
                System.err.println("Failed to start server: " + result.cause());
            }
        });
    }
}
