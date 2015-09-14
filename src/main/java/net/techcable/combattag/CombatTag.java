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

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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
            settings.load(new File(getDataFolder(), "config.cdl"), CombatTag.class.getResource("/config.cdl"));
            messages.load(new File(getDataFolder(), "messages.cdl"), CombatTag.class.getResource("/messages.cdl"));
        } catch (IOException | InvalidConfigurationException ex) {
            Utils.severe("Unable to load config", ex);
            Utils.severe("Shutting down");
            setEnabled(false);
            return;
        }
        registerListener(new PlayerListener(this));
        registerListener(new ConfigListener());
        registerListener(new SafeLogoutListener(this));
        registerListener(new UIListener());
        if (Reflection.getClass("be.maximvdw.featherboard.api.PlaceholderAPI") != null) {
            MaximvdwHelper.registerPlaceholders();
        }
        initMetrics();
        if (!NPCLib.isSupported()) {
            severe("This version of minecraft isn't supported");
            severe("Please install citizens or update CombatTag if you want to use npcs");
        } else {
            this.npcManager = new NPCManager(this);
            registerListener(new NPCListener(this));
            registerListener(new SafeLogoutListener(this));
        }
        if (settings.getPunishment() == Punishment.INSTAKILL) {
            registerListener(new InstakillListener());
        }
    }

    @Override
    protected void shutdown() {
        try {
            settings.save(new File(getDataFolder(), "config.cdl"), CombatTag.class.getResource("/config.cdl"));
            messages.save(new File(getDataFolder(), "messages.cdl"), CombatTag.class.getResource("/messages.cdl"));
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

    @Override
    public CombatPlayer getPlayer(Player player) {
        if (isNPC(player)) throw new IllegalArgumentException("Can't get a combat player for an npc");
        return super.getPlayer(player);
    }

    @Override
    public CombatPlayer getPlayer(UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);
        if (player == null) return super.getPlayer(playerId); // Handle null players with superclass
        return getPlayer(player);
    }

    @Getter
    private NPCManager npcManager;

    public boolean isNPC(LivingEntity e) {
        return npcManager != null && e instanceof Player && npcManager.isNPC(((Player) e));
    }
}
