package com.mine.grpc.controller.impl;

import com.mine.grpc.DemoProto;
import com.mine.grpc.GreeterGrpc;
import io.grpc.stub.StreamObserver;

public class GreeterImpl extends GreeterGrpc.GreeterImplBase {
    @Override
    public void sayHello(DemoProto.HelloRequest request, StreamObserver<DemoProto.HelloReply> responseObserver) {
        DemoProto.HelloReply reply = DemoProto.HelloReply.newBuilder().setMessage("Hello " + request.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void sayHelloAgain(DemoProto.HelloRequest request, StreamObserver<DemoProto.HelloReply> responseObserver) {
        DemoProto.HelloReply reply = DemoProto.HelloReply.newBuilder().setMessage("Hello again" + request.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
