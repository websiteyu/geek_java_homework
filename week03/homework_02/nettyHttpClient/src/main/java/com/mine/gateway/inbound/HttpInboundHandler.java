package com.mine.gateway.inbound;

import com.mine.gateway.filter.HttpRequestFilter;
import com.mine.gateway.filter.impl.HeaderHttpRequestFilter;
import com.mine.gateway.outbound.OutboundHandler;
import com.mine.gateway.outbound.okhttp.OkhttpOutboundHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;

import java.util.List;


public class HttpInboundHandler extends ChannelInboundHandlerAdapter {
    private final List<String> proxyServer;
    private HttpRequestFilter filter = new HeaderHttpRequestFilter();
    private OutboundHandler handler;


    public HttpInboundHandler(List<String> proxyServer){
        this.proxyServer = proxyServer;
        this.handler = new OkhttpOutboundHandler(proxyServer);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)  {
        try{
            FullHttpRequest fullRequest = (FullHttpRequest) msg;

            handler.handle(fullRequest, ctx, filter);

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }

    }
}
