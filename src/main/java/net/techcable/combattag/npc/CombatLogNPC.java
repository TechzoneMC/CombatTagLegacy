package net.techcable.combattag.npc;

import java.util.UUID;

import net.techcable.npclib.HumanNPC;

public class CombatLogNPC extends CombatNPC {

    public CombatLogNPC(NPCManager manager, UUID playerId, HumanNPC npc) {
        super(manager, playerId, npc);
    }

    @Override
    public NPCType getType() {
        return NPCType.COMBAT_LOG_NPC;
    }

    //
    // Static Methods for NPCType
    //
    public static final long ID_MASK = 0x3C2284545E105E6EL;


}
