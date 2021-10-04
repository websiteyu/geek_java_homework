package com.mine.grpc;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;

public class GRPCClient {
    private final GreeterGrpc.GreeterBlockingStub greeterBlockingStub;

    public GRPCClient(Channel channel){
        greeterBlockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    /** 向服务端发送请求 */
    public void greet(String name) {
        System.out.println("Will try to greet " + name + " ...");
        DemoProto.HelloRequest request = DemoProto.HelloRequest.newBuilder().setName(name).build();
        DemoProto.HelloReply response;
        try {
            response = greeterBlockingStub.sayHello(request);
        } catch (StatusRuntimeException e) {
            System.out.println("WARNING, RPC failed: Status=" + e.getStatus());
            return;
        }
        System.out.println("Greeting: " + response.getMessage());
    }

    public static void main(String[] args) throws InterruptedException {
        String user = "wmy";
        // 访问本机在 50001 端口上运行的服务
        String target = "localhost:50001";
        // 命令行用法帮助，允许将用户名和目标服务器作为命令行参数传入。
        if (args.length > 0) {
            if ("--help".equals(args[0])) {
                System.err.println("Usage: [name [target]]");
                System.err.println("");
                System.err.println("  name    The name you wish to be greeted by. Defaults to " + user);
                System.err.println("  target  The server to connect to. Defaults to " + target);
                System.exit(1);
            }
            user = args[0];
        }
        if (args.length > 1) {
            target = args[1];
        }

        // 创建到服务器的通信通道，通道是线程安全的和可重用的。
        // 通常在应用程序开始时创建通道，并重用直到应用程序关闭。
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        try {
            GRPCClient client = new GRPCClient(channel);
            client.greet(user);
        } finally {
            // ManagedChannel使用像线程和TCP连接这样的资源。
            // 为了防止泄漏这些资源，通道应该在不再使用时关闭。
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

}
