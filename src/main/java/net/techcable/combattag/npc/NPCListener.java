package net.techcable.combattag.npc;

import lombok.RequiredArgsConstructor;
import net.techcable.combattag.CombatPlayer;
import net.techcable.combattag.CombatTag;
import net.techcable.combattag.config.Punishment;
import net.techcable.combattag.events.CombatLogEvent;
import net.techcable.combattag.libs.NMSUtils;
import net.techcable.techutils.Reflection;
import net.techcable.techutils.inventory.InventoryUtils;
import net.techcable.techutils.inventory.PlayerData;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.lang.reflect.Field;
import java.util.UUID;

@RequiredArgsConstructor
public class NPCListener implements Listener {
    private final CombatTag plugin;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCombatLog(CombatLogEvent handler) {
        if (plugin.getSettings().getPunishment() != Punishment.NPC) return;
        NPCManager npcManager = plugin.getNpcManager();
        CombatNPC npc = npcManager.spawnNpc(handler.getLogger(), NPCType.COMBAT_LOG_NPC);
        PlayerData playerData = InventoryUtils.getData(handler.getLogger().getEntity());
        npc.copyFrom(playerData);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        NPCManager npcManager = CombatTag.getInstance().getNpcManager();
        CombatNPC npc = npcManager.asNPC(event.getPlayer());
        if (npc == null) return;
        PlayerData playerData = InventoryUtils.getData(event.getPlayer());
        npc.copyTo(playerData);
        npc.despawn();
        NMSUtils.setInvulnerableTicks(event.getPlayer(), 0);
        CombatPlayer.getPlayer(event.getPlayer()).tag(); // Update tag
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        NPCManager npcManager = CombatTag.getInstance().getNpcManager();
        CombatNPC npc = npcManager.asNPC(event.getEntity());
        if (npc == null) return;
        if (npc.getType().isCopyDataToPlayerOnDeath()) {
            PlayerData playerData = InventoryUtils.getData(npc.getPlayerId());
            npc.copyTo(playerData);
        }
        npc.despawn();
    }
}
