package com.trc202.libs.techcable.intercept;

import com.trc202.libs.techcable.Reflection;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DynamicMinecraftHandler extends DynamicChannelDuplexHandler {
    private final Player player;
    @Override
    public void write(Object ctx, Object packet, Object promise) throws Exception {
        AsyncPacketSendEvent event = new AsyncPacketSendEvent(this.player, packet);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        super.write(ctx, packet, promise);
    }

    @Override
    public void channelRead(Object ctx, Object packet) throws Exception {
        AsyncPacketReceiveEvent event = new AsyncPacketReceiveEvent(this.player, packet);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        super.channelRead(ctx, packet);
    }

    private static final Class<?> inboundHandler = Reflection.getUtilClass("io.netty.channel.ChannelInboundHandler");
    private static final Class<?> outboundHandler = Reflection.getUtilClass("io.netty.channel.ChannelOutboundHandler");
    private static final Class<?> proxyClass = Proxy.getProxyClass(DynamicMinecraftHandler.class.getClassLoader(), new Class<?>[] {inboundHandler, outboundHandler});;
    private static final Constructor proxyConstructor = Reflection.makeConstructor(proxyClass, InvocationHandler.class);
    public static Object create(Player player) {
        DynamicMinecraftHandler handler = new DynamicMinecraftHandler(player);
        return Reflection.callConstructor(proxyConstructor, handler);
    }
}
