package net.techcable.combattag.events;

import net.techcable.combattag.CombatPlayer;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.*;

@Getter
@RequiredArgsConstructor
public class CombatTagEvent extends Event implements Cancellable {
	private final CombatPlayer player;
	private final LivingEntity cause;
	@Getter(AccessLevel.NONE)
	private final TagCause reason;
	
	public boolean isBecauseOfAttack() {
		return reason == TagCause.ATTACK;
	}
	
	public boolean isBecauseOfDefend() {
		return reason == TagCause.DEFEND;
	}
	
	@Getter
	private static final HandlerList handlerList = new HandlerList();
	@Setter
	private boolean cancelled;
	@Override
	public HandlerList getHandlers() {
		return getHandlerList();
	}
	
	public static enum TagCause {
		ATTACK,
		DEFEND,
	}
}
