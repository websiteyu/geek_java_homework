package com.mine.grpc;

import com.mine.grpc.controller.impl.GreeterImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */
public class GRPCServer
{
    private Server server;
    public GRPCServer(int port) throws IOException {
        server = ServerBuilder.forPort(port).addService(new GreeterImpl()).build().start();
        System.out.println("grpc server start! port is "+port);

        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            // 使用标准错误输出，因为日志记录器有可能在JVM关闭时被重置
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            try {
                GRPCServer.this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("*** server shut down");
        }));
    }

    private void stop() throws InterruptedException {
        if(server!=null){
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * 在主线程上等待终止，因为grpc库使用守护进程。
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * 启动服务Main方法
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        final GRPCServer server = new GRPCServer(50001);
        server.blockUntilShutdown();
    }
}
