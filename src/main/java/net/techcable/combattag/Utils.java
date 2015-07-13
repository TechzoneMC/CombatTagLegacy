package net.techcable.combattag;

import lombok.*;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {

    public static String getName(Entity of) {
        if (of instanceof Projectile) {
            if (((Projectile) of).getShooter() instanceof Entity) {
                return getName((Entity) ((Projectile) of).getShooter());
            } else {
                return "unknown";
            }
        } else if (of instanceof Tameable) {
            return getName((Entity) ((Tameable) of).getOwner());
        } else if (of instanceof HumanEntity) {
            return of.getName();
        } else if (of.getCustomName() != null) {
            return of.getCustomName();
        } else {
            return of.getType().getName();
        }
    }

    public static void warning(String msg, Throwable t) {
        Bukkit.getLogger().log(Level.WARNING, "[CombatTag] " + msg, t);
    }

    public static void severe(String msg, Throwable t) {
        Bukkit.getLogger().log(Level.SEVERE, "[CombatTag] " + msg, t);
    }

    public static void severe(String msg) {
        Bukkit.getLogger().log(Level.SEVERE, "[CombatTag] " + msg);
    }


    public static void info(String msg) {
        Bukkit.getLogger().log(Level.INFO, "[CombatTag] " + msg);
    }

    public static void debug(String msg) {
        if (CombatTag.getInstance().getSettings().isDebugMode()) {
            info(msg);
        }
    }
}
