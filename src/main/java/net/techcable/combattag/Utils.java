package net.techcable.combattag;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.projectiles.ProjectileSource;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {

    public static String getName(Entity of) {
        if (of instanceof Projectile) {
            if (((Projectile) of).getShooter() instanceof Entity) {
                return getName((Entity)((Projectile) of).getShooter());
            } else {
                return "unknown";
            }
        } else if (of instanceof Tameable) {
            return getName((Entity) ((Tameable) of).getOwner());
        } else if (of instanceof HumanEntity || of.getCustomName() != null) {
            return of.getCustomName() != null ? of.getCustomName() : of.getName();
        } else {
            return of.getType().getName();
        }
    }

}
