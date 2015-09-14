package net.techcable.combattag.config;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

import net.techcable.combattag.CombatPlayer;
import net.techcable.techutils.config.AnnotationConfig;
import net.techcable.techutils.config.Setting;

@Getter
public class CombatTagConfig extends AnnotationConfig {
    @Setting("safeLogoutEnabled")
    private boolean safeLogoutEnabled = false;

    @Setting("safeLogoutTime")
    private int safeLogoutTime = 15;

    @Setting("safeLogoutDisplayMode")
    private DisplayMode safeLogoutDisplayMode = DisplayMode.ACTION_BAR;

    @Setting(value = "tagDuration")
    private int tagDuration = 10;

    @Setting("debugMode")
    private boolean debugMode = false;

    @Setting("punishment")
    private Punishment punishment = Punishment.NPC;

    @Setting("disallowedCommands")
    private List<String> disallowedCommands = new ArrayList<>();

    @Setting("disallowedWorlds")
    private List<String> disallowedWorlds = new ArrayList<>();

    @Setting("dropTagOnKick")
    private boolean dropTagOnKick = false;

    @Setting("mobTag")
    private boolean mobTag = false;

    @Setting("playerTag")
    private boolean playerTag = true;

    @Setting("tagDisplayMode")
    private DisplayMode tagDisplayMode = DisplayMode.BOSS_BAR;

    @Setting("onlyTagDamager")
    private boolean onlyTagDamager = false;

    //
    // Npcs
    //

    @Setting("npc.name")
    @Getter(AccessLevel.NONE)
    private String npcName = "{player}";

    public String getNpcName(CombatPlayer player, int npcId) {
        return npcName.replace("{player}", player.getName()).replace("{number}", Integer.toString(npcId));
    }

    @Setting("npc.despawnTime")
    private int npcDespawnTime = -1;

    @Setting("npc.dieAfterTimeRunsOut")
    private boolean npcDiesAfterTimeRunsOut = false;

    @Setting("npc.showInTab")
    private boolean showNpcInTab = false;

    //
    // Blocks
    //

    @Setting("blocks.canEditBlocks")
    private boolean blockEditWhileTagged = true;

    @Setting("blocks.teleport")
    private boolean blockTeleport = false;

    @Setting("blocks.enderPearl")
    private boolean blockEnderPearl = false;

    @Setting("blocks.creativeTagging")
    private boolean blockCreativeTagging = true;

    @Setting("blocks.fly")
    private boolean blockFly = true;

}
