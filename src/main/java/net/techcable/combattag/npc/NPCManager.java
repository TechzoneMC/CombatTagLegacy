package net.techcable.combattag.npc;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import net.techcable.combattag.CombatPlayer;
import net.techcable.combattag.CombatTag;
import net.techcable.npclib.NPC;
import net.techcable.npclib.NPCLib;
import net.techcable.npclib.NPCRegistry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NPCManager {

    public boolean isNpc(Entity e) {
        return getRegistry().isNPC(e);
    }

    public void despawnNpc(CombatNPC npc) {

    }

    private final Map<UUID, CombatNPC> npcs = new HashMap<>();
    public CombatNPC spawnNpc(CombatPlayer forWhom) {
        if (getNpc(forWhom) != null) return getNpc(forWhom);
        NPC npc = getRegistry().createNPC(EntityType.PLAYER, forWhom.getId(), forWhom.getName());
        npc.setSkin(forWhom.getId());
        npc.setProtected(false);
        npc.spawn(forWhom.getEntity().getLocation());
        CombatNPC combatNPC = new CombatNPC(forWhom.getId(), npc);
        npcs.put(combatNPC.getPlayerId(), combatNPC);
        return combatNPC;
    }

    public CombatNPC getNpc(CombatPlayer player) {
        return npcs.get(player.getId());
    }

    public Collection<CombatNPC> listNpcs() {
        return Collections2.transform(getRegistry().listNpcs(), new Function<NPC, CombatNPC>() {
            public CombatNPC apply(NPC input) {
                return npcs.get(input.getUUID());
            }
        });
    }

    private NPCRegistry getRegistry() {
        return NPCLib.getNPCRegistry("CombatTag", CombatTag.getInstance());
    }

    public boolean isNPC(Entity entity) {
        return getRegistry().isNPC(entity);
    }
}
