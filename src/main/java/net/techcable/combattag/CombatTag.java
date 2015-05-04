package net.techcable.combattag;

import com.trc202.settings.Settings;
import com.trc202.settings.SettingsHelper;
import com.trc202.settings.SettingsLoader;
import lombok.Getter;
import net.techcable.combattag.listeners.ConfigListener;
import net.techcable.combattag.listeners.InstakillListener;
import net.techcable.combattag.listeners.PlayerListener;
import net.techcable.combattag.listeners.SafeLogoutListener;
import net.techcable.combattag.npc.NPCListener;
import net.techcable.combattag.npc.NPCManager;
import net.techcable.npclib.NPCLib;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class CombatTag extends com.trc202.CombatTag.CombatTag {//Extend the old class for backwards api compatibility
    private final SettingsHelper settingsHelper = new SettingsHelper(new File(getDataFolder(), "settings.prop"), "CombatTag");
    @Getter
    private Settings settings;

    @Override
    protected void startup() {
        settings = new SettingsLoader().loadSettings(settingsHelper, getDescription().getVersion());
        getCommand("ct").setExecutor(new CombatTagExecutor(this));
        registerListener(new PlayerListener(this));
        registerListener(new ConfigListener());
        MaximvdwHelper.registerPlaceholders();
        initMetrics();
        if (!getSettings().isInstaKill()) {
            if (!NPCLib.isSupported() ){
                severe("NPCs are enabled but this version of minecraft isn't supported");
                severe("Please install citizens or update CombatTag if you want to use npcs");
                setEnabled(false);
                return;
            } else {
                this.npcManager = new NPCManager();
                NPCListener npcListener = new NPCListener();
                registerListener(npcListener);
                registerListener(new SafeLogoutListener());
            }
        } else {
            registerListener(new InstakillListener());
        }
    }

    @Override
    protected void shutdown() {

    }

    @Override
    public CombatPlayer createPlayer(UUID id) {
        return new CombatPlayer(id, this);
    }

    private Metrics metrics;
    public boolean initMetrics() {
        try {
            if (metrics == null) {
                metrics = new Metrics(this);
            }
            Metrics.Graph punishment = metrics.createGraph("Punishment used on Combat Tag");
            punishment.addPlotter(new Metrics.Plotter("Instakill") {
                @Override
                public int getValue() {
                    if (getSettings().isInstaKill()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
            punishment.addPlotter(new Metrics.Plotter("NPC") {
                @Override
                public int getValue() {
                    if (!getSettings().isInstaKill()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
            metrics.start();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public static CombatTag getInstance() {
        return JavaPlugin.getPlugin(CombatTag.class);
    }

    @Getter
    private NPCManager npcManager;
    public boolean isNPC(LivingEntity e) {
        if (npcManager != null) {
            return npcManager.isNpc(e);
        } else { //Instakill
            return false;
        }
    }
}
