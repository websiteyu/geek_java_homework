package com.mine.gateway.outbound.okhttp;

import com.mine.gateway.filter.HttpRequestFilter;
import com.mine.gateway.filter.HttpResponseFilter;
import com.mine.gateway.filter.impl.HeaderHttpResponseFilter;
import com.mine.gateway.outbound.OutboundHandler;
import com.mine.gateway.router.HttpEndpointRouter;
import com.mine.gateway.router.impl.SortedHttpEndpointRouter;
import com.mine.gateway.threadpool.NamedThreadFactory;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class OkhttpOutboundHandler implements OutboundHandler {
    private List<String> backendUrls;
    private ExecutorService proxyService;

    HttpResponseFilter filter = new HeaderHttpResponseFilter();
    HttpEndpointRouter router = new SortedHttpEndpointRouter();

    public OkhttpOutboundHandler(List<String> backends){
        this.backendUrls = backends.stream().map(this::formatUrl).collect(Collectors.toList());

        int cores = Runtime.getRuntime().availableProcessors();
        long keepAliveTime = 1000;
        int queueSize = 2048;
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();//.DiscardPolicy();
        proxyService = new ThreadPoolExecutor(cores, cores,
                keepAliveTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(queueSize),
                new NamedThreadFactory("proxyService"), handler);




    }



    @Override
    public void handle(FullHttpRequest fullRequest, ChannelHandlerContext ctx, HttpRequestFilter filter) {
        String backendUrl = router.route(this.backendUrls);
        final String url = backendUrl + fullRequest.uri();
        filter.filter(fullRequest, ctx);
        proxyService.submit(()->fetchGet(fullRequest, ctx, url));
    }

    private void fetchGet(final FullHttpRequest inbound, final ChannelHandlerContext ctx, final String url) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("mao",inbound.headers().get("mao"))
                .build();
//        ResponseBody body = null;
        byte[] body = null;
        int contentLength = -1;
        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            body = responseBody.bytes();
            contentLength = Math.toIntExact(responseBody.contentLength());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(body!=null)  {
            handleResponse(inbound, ctx, body, contentLength);
        }
    }

    private void handleResponse(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, byte[] body, int contentLength) {
        FullHttpResponse response = null;
        try {

//            byte[] body = endpointResponse.bytes();

            response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(body));

            response.headers().set("Content-Type", "application/json");
            response.headers().setInt("Content-Length", Math.toIntExact(contentLength));
            filter.filter(response);


        } catch (Exception e) {
            e.printStackTrace();
            response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
            exceptionCaught(ctx, e);
        } finally {
            if (fullRequest != null) {
                if (!HttpUtil.isKeepAlive(fullRequest)) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    //response.headers().set(CONNECTION, KEEP_ALIVE);
                    ctx.write(response);
                }
            }
            ctx.flush();
            //ctx.close();
        }

    }

    private String formatUrl(String backend) {
        return backend.endsWith("/")?backend.substring(0,backend.length()-1):backend;
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
