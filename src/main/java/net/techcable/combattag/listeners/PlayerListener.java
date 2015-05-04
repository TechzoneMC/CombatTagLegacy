package net.techcable.combattag.listeners;

import com.trc202.CombatTagApi.CombatTagApi;
import lombok.RequiredArgsConstructor;
import net.techcable.combattag.CombatPlayer;
import net.techcable.combattag.CombatTag;
import net.techcable.combattag.events.CombatLogEvent;
import net.techcable.combattag.events.CombatTagEvent;
import net.techcable.combattag.npc.NPCManager;
import net.techcable.npclib.NPCLib;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.ref.PhantomReference;

@RequiredArgsConstructor
public class PlayerListener implements Listener {
    private final CombatTag plugin;
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        LivingEntity attacker = getRootDamager(event.getDamager());
        if (attacker == null || !(event.getEntity() instanceof LivingEntity)) return;
        LivingEntity defender = (LivingEntity) event.getEntity();
        if (CombatTagApi.getInstance().isNPC(defender));
        if (attacker.equals(defender)) return;
        if (attacker instanceof Player && !CombatPlayer.getPlayer((Player) attacker).isTagged()) {
            CombatPlayer attackPlayer = CombatPlayer.getPlayer((Player) attacker);
            if (attackPlayer.isTagged()) {
                //Update time and return to avoid event spam
                attackPlayer.tag();
            } else if (plugin.getSettings().mobTag() || (defender instanceof HumanEntity)) {
                CombatTagEvent tagEvent = new CombatTagEvent(attackPlayer, defender, CombatTagEvent.TagCause.ATTACK);
                Bukkit.getPluginManager().callEvent(tagEvent);
                if (!tagEvent.isCancelled()) {
                    attackPlayer.tag();
                }
            }
        }
        if (defender instanceof Player && !CombatTag.getInstance().isNPC(defender) && !CombatPlayer.getPlayer((Player) defender).isTagged()) {
            CombatPlayer defendPlayer = CombatPlayer.getPlayer((Player) defender);
            if (defendPlayer.isTagged()) {
                //Update time and return to avoid event spam
                defendPlayer.tag();
            } else if (plugin.getSettings().mobTag() || (defender instanceof HumanEntity)) {

                CombatTagEvent tagEvent = new CombatTagEvent(defendPlayer, defender, CombatTagEvent.TagCause.DEFEND);
                Bukkit.getPluginManager().callEvent(tagEvent);
                if (!tagEvent.isCancelled()) {
                    defendPlayer.tag();
                }
            }
        }
    }

    public String getQuitMessage() {
        if (CombatTag.getInstance().getSettings().isInstaKill()) {
            return (ChatColor.RED + "You have logged out in combat and have been killed");
        } else {
            return (ChatColor.RED + "You have logged out in combat and an npc has taken your place");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuitMonitor(PlayerQuitEvent event) {
        CombatPlayer player = CombatPlayer.getPlayer(event.getPlayer());
        if (player.isTagged()) {
            onLogout(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKickMonitor(PlayerKickEvent event) {
        if (CombatTag.getInstance().getSettings().dropTagOnKick() && !event.getReason().contains("Hacking")) return;
        CombatPlayer player = CombatPlayer.getPlayer(event.getPlayer());
        if (player.isTagged()) {
            onLogout(player);
        }
    }


    private void onLogout(CombatPlayer player) {
        CombatLogEvent event = new CombatLogEvent(player);
        Bukkit.getPluginManager().callEvent(event);
    }

    public static LivingEntity getRootDamager(Entity e) {
        if (e instanceof Projectile) {
            if (!(((Projectile) e).getShooter() instanceof LivingEntity)) return null;
            return getRootDamager((Entity) ((Projectile) e).getShooter());
        } else if (e instanceof LivingEntity) {
            return (LivingEntity) e;
        }
        return null;
    }
}
