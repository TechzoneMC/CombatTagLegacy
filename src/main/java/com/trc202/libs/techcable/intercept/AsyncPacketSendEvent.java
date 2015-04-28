package com.trc202.libs.techcable.intercept;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class AsyncPacketSendEvent extends Event implements Cancellable {
    private final Player player;
    private final Object packet;
    public AsyncPacketSendEvent(Player player, Object packet) {
        super(true);
        this.player = player;
        this.packet = packet;
    }
    @Setter
    private boolean cancelled;
    private final HandlerList handlerList = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
