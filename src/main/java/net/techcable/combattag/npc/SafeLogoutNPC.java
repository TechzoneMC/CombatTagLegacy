package net.techcable.combattag.npc;

import net.techcable.npclib.HumanNPC;

import java.util.UUID;

public class SafeLogoutNPC extends CombatNPC {
    public SafeLogoutNPC(NPCManager manager, UUID playerId, HumanNPC npc) {
        super(manager, playerId, npc);
    }

    //
    // Instance Methods
    //

    @Override
    public NPCType getType() {
        return NPCType.SAFELOGOUT_NPC;
    }

    //
    // Static Methods for NPCType
    //
    public static final long SAFE_LOGOUT_MASK = 0x5362EA11D66F5146L;

}
