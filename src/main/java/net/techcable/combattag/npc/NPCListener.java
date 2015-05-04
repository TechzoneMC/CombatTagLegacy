package net.techcable.combattag.npc;

import net.techcable.combattag.CombatPlayer;
import net.techcable.combattag.CombatTag;
import net.techcable.combattag.events.CombatLogEvent;
import net.techcable.combattag.libs.NMSUtils;
import net.techcable.techutils.inventory.InventoryUtils;
import net.techcable.techutils.inventory.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

public class NPCListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCombatLog(CombatLogEvent handler) {
        NPCManager npcManager = CombatTag.getInstance().getNpcManager();
        CombatNPC npc = npcManager.spawnNpc(handler.getLogger());
        InventoryUtils.copy(InventoryUtils.getData(handler.getLogger().getEntity()), InventoryUtils.getData((Player)npc.getNpc().getEntity()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        NPCManager npcManager = CombatTag.getInstance().getNpcManager();
        CombatNPC npc = npcManager.getNpc(CombatPlayer.getPlayer(event.getPlayer()));
        if (npc == null) return;
        PlayerData npcData = InventoryUtils.getData(npc.getEntity());
        PlayerData playerData = InventoryUtils.getData(event.getPlayer());
        InventoryUtils.copy(npcData, playerData);
        npcManager.despawnNpc(npc);
        NMSUtils.setInvulnerableTicks(event.getPlayer(), 0);
        CombatPlayer.getPlayer(event.getPlayer()).tag(); // Update tag
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        NPCManager npcManager = CombatTag.getInstance().getNpcManager();
        CombatNPC npc = npcManager.getNpc(CombatPlayer.getPlayer(event.getEntity()));
        PlayerData npcData = InventoryUtils.getData(npc.getEntity());
        PlayerData playerData = InventoryUtils.getData(event.getEntity());
        InventoryUtils.copy(npcData, playerData);
        npcManager.despawnNpc(npc);
    }
}
