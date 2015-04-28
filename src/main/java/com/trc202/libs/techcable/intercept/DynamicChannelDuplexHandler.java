package com.trc202.libs.techcable.intercept;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.SocketAddress;

import static com.trc202.libs.techcable.Reflection.*;

public class DynamicChannelDuplexHandler implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //Hardcoded for speed (don't want a method lookup each time)
        Object ctx = args[0]; //All methods take a context
        switch (method.getName()) {
            case "bind" :
                bind(ctx, (SocketAddress) args[1], args[2]);
                return null;
            case "connect" :
                connect(ctx, (SocketAddress) args[1], (SocketAddress) args[2], args[3]);
                return null;
            case "disconnect" :
                disconnect(ctx, args[1]);
                return null;
            case "close" :
                close(ctx, args[1]);
                return null;
            case "deregister" :
                deregister(ctx, args[1]);
                return null;
            case "read" :
                read(ctx);
                return null;
            case "write" :
                write(ctx, args[1], args[2]);
                return null;
            case "flush" :
                flush(ctx);
                return null;
            case "channelRegistered" :
                channelRegistered(ctx);
                return null;
            case "channelUnregistered" :
                channelUnregistered(ctx);
                return null;
            case "channelActive" :
                channelActive(ctx);
                return null;
            case "channelInactive" :
                channelInactive(ctx);
                return null;
            case "channelRead" :
                channelRead(ctx, args[1]);
                return null;
            case "channelReadComplete" :
                channelReadComplete(ctx);
                return null;
            case "userEventTriggered" :
                userEventTriggered(ctx, args[1]);
                return null;
            case "channelWritabilityChanged" :
                channelWritabilityChanged(ctx);
                return null;
            case "exceptionCaught" :
                exceptionCaught(ctx, (Throwable) args[1]);
                return null;
            default :
                throw new IllegalArgumentException("Unknown method " + method.getName());
        }
    }
    //Classes
    private static final Class<?> channelPromise = getUtilClass("io.netty.channel.ChannelPromise");
    private static final Class<?> channelHandlerContext = getUtilClass("io.netty.channel.ChannelHandlerContext");
    
    private static final Method bindMethod = makeMethod(channelHandlerContext, "bind", SocketAddress.class, channelPromise);
    public void bind(Object ctx, SocketAddress localAddress, Object future) throws Exception {
        callMethod(bindMethod, ctx, localAddress, future);
    }
    private static final Method connectMethod = makeMethod(channelHandlerContext, "connect", SocketAddress.class, SocketAddress.class, channelPromise);
    public void connect(Object ctx, SocketAddress remoteAddress, SocketAddress localAddress, Object future) throws Exception {
        callMethod(connectMethod, ctx, remoteAddress, localAddress, future);
    }
    private static final Method disconnectMethod = makeMethod(channelHandlerContext, "disconnect", channelPromise);
    public void disconnect(Object ctx, Object future) throws Exception {
        callMethod(disconnectMethod, ctx, future);
    }
    private static final Method closeMethod = makeMethod(channelHandlerContext, "close", channelPromise);
    public void close(Object ctx, Object future) throws Exception {
        callMethod(closeMethod, ctx, future);
    }
    private static final Method deregisterMethod = makeMethod(channelHandlerContext, "deregister", channelPromise);
    public void deregister(Object ctx, Object future) throws Exception {
        callMethod(deregisterMethod, ctx, future);
    }
    private static final Method readMethod = makeMethod(channelHandlerContext, "read");
    public void read(Object ctx) throws Exception {
        callMethod(readMethod, ctx);
    }
    private static final Method writeMethod = makeMethod(channelHandlerContext, "write", Object.class, channelPromise);
    public void write(Object ctx, Object msg, Object promise) throws Exception {
        callMethod(writeMethod, ctx, msg, promise);
    }
    private static final Method flushMethod = makeMethod(channelHandlerContext, "flush");
    public void flush(Object ctx) throws Exception {
        callMethod(flushMethod, ctx);
    }
    private static final Method fireChannelRegisteredMethod = makeMethod(channelHandlerContext, "fireChannelRegistered");
    public void channelRegistered(Object ctx) throws Exception {
        callMethod(fireChannelRegisteredMethod, ctx);
    }
    private static final Method fireChannelUnregisteredMethod = makeMethod(channelHandlerContext, "fireChannelUnregistered");
    public void channelUnregistered(Object ctx) throws Exception {
        callMethod(fireChannelUnregisteredMethod, ctx);
    }
    private static final Method fireChannelActiveMethod = makeMethod(channelHandlerContext, "fireChannelActive");
    public void channelActive(Object ctx) throws Exception {
        callMethod(fireChannelActiveMethod, ctx);
    }
    private static final Method fireChannelInactiveMethod = makeMethod(channelHandlerContext, "fireChannelInactive");
    public void channelInactive(Object ctx) throws Exception {
        callMethod(fireChannelInactiveMethod, ctx);
    }
    private static final Method fireChannelReadMethod = makeMethod(channelHandlerContext, "fireChannelRead", Object.class);
    public void channelRead(Object ctx, Object msg) throws Exception {
        callMethod(fireChannelReadMethod, ctx, msg);
    }
    private static final Method fireChannelReadCompleteMethod = makeMethod(channelHandlerContext, "fireChannelReadComplete");
    public void channelReadComplete(Object ctx) throws Exception {
        callMethod(fireChannelReadCompleteMethod, ctx);
    }
    private static final Method fireUserEventTriggeredMethod = makeMethod(channelHandlerContext, "fireUserEventTriggered", Object.class);
    public void userEventTriggered(Object ctx, Object evt) throws Exception {
        callMethod(fireUserEventTriggeredMethod, ctx, evt);
    }
    private static final Method fireChannelWritabilityChanged = makeMethod(channelHandlerContext, "fireChannelWritabilityChanged");
    public void channelWritabilityChanged(Object ctx) throws Exception {
        callMethod(fireChannelWritabilityChanged, ctx);
    }
    private static final Method fireExceptionCaughtMethod = makeMethod(channelHandlerContext, "fireExceptionCaught");
    public void exceptionCaught(Object ctx, Throwable cause) throws Exception {
        callMethod(fireExceptionCaughtMethod, ctx, cause);
    }
}
