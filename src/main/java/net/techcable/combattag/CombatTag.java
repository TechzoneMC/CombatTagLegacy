package net.techcable.combattag;

import lombok.*;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import net.techcable.combattag.config.CombatTagConfig;
import net.techcable.combattag.config.CombatTagMessages;
import net.techcable.combattag.config.Punishment;
import net.techcable.combattag.listeners.ConfigListener;
import net.techcable.combattag.listeners.InstakillListener;
import net.techcable.combattag.listeners.PlayerListener;
import net.techcable.combattag.listeners.SafeLogoutListener;
import net.techcable.combattag.listeners.UIListener;
import net.techcable.combattag.npc.NPCListener;
import net.techcable.combattag.npc.NPCManager;
import net.techcable.npclib.NPCLib;
import net.techcable.techutils.Reflection;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

public class CombatTag extends com.trc202.CombatTag.CombatTag {//Extend the old class for backwards api compatibility
    @Getter
    private CombatTagConfig settings;
    @Getter
    private CombatTagMessages messages;

    @Override
    protected void startup() {
        getCommand("ct").setExecutor(new CombatTagExecutor(this));
        getCommand("logout").setExecutor(new SafeLogoutExecutor(this));
        settings = new CombatTagConfig();
        messages = new CombatTagMessages();
        try {
            settings.load(new File(getDataFolder(), "config.yml"), CombatTag.class.getResource("/config.yml"));
            messages.load(new File(getDataFolder(), "messages.yml"), CombatTag.class.getResource("/messages.yml"));
        } catch (IOException | InvalidConfigurationException ex) {
            Utils.severe("Unable to load config", ex);
            Utils.severe("Shutting down");
            setEnabled(false);
            return;
        }
        registerListener(new PlayerListener(this));
        registerListener(new ConfigListener());
        registerListener(new SafeLogoutListener());
        registerListener(new UIListener());
        if (Reflection.getClass("be.maximvdw.featherboard.api.PlaceholderAPI") != null) {
            MaximvdwHelper.registerPlaceholders();
        }
        initMetrics();
        if (settings.getPunishment().equals(Punishment.NPC)) {
            if (!NPCLib.isSupported()) {
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
        try {
            settings.save(new File(getDataFolder(), "config.yml"), CombatTag.class.getResource("/config.yml"));
            messages.save(new File(getDataFolder(), "messages.yml"), CombatTag.class.getResource("/messages.yml"));
        } catch (IOException | InvalidConfigurationException ex) {
            Utils.warning("Unable to save config", ex);
        }
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
                    if (getSettings().getPunishment().equals(Punishment.INSTAKILL)) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
            punishment.addPlotter(new Metrics.Plotter("NPC") {

                @Override
                public int getValue() {
                    if (getSettings().getPunishment().equals(Punishment.NPC)) {
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
