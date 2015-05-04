package net.techcable.combattag.libs.bar;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

import com.trc202.CombatTag.CombatTag;
import net.techcable.techutils.Reflection;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import static net.techcable.techutils.Reflection.*;

public class BossBar extends BukkitRunnable {
    private BossBar(Plugin plugin, Player p) {
        this.plugin = plugin; //Plugin to register tasks under
        this.player = p;
        int version = getProtocolVersion(p);
        if (version > 5) {
            this.entity = new FakeWither(p);
        } else {
            this.entity = new FakeDragon(p);
        }
        runTaskTimer(plugin, 0, 3); //Every 3 ticks
    }

    private static final Map<Player, BossBar> bars = new WeakHashMap<Player, BossBar>();
    private final Player player;
    private final Plugin plugin;
    private final net.techcable.combattag.libs.bar.FakeEntity entity;
    public static BossBar getBossBar(Player player) {
        //!!Change this to get your plugin's instance!!
        Plugin plugin = JavaPlugin.getPlugin(CombatTag.class);
        if (!bars.containsKey(player)) {
            bars.put(player, new BossBar(plugin, player));
        }
        return bars.get(player);
    }

    //Public Instance Methods

    /**
     * Set the message on the boss bar
     *
     * @param message the message for the player to see on the boss bar
     */
    public void setMessage(String message) {
        entity.setCustomName(message.length() <= 64 ? message : message.substring(0, 63));
        if (isShown()) entity.update();
        trySpawn();
    }

    /**
     * Set how full the boss bar should be
     *
     * @param percent how full the boss bar should be
     */
    public void setPercentage(int percent) {
        percent = Math.max(Math.min(100, percent), 0); //Checking for the errors of others
        entity.setHealth(percent / 100F * entity.getMaxHealth());
        if (isShown()) entity.update();
        trySpawn();
    }

    /**
     * Stop showing the boss bar to this player
     *
     */
    public void stopShowing() {
        if (entity.isSpawned()) {
            entity.despawn();
        }
    }

    /**
     * Return if the boss bar is currently being shown
     * @return
     */
    public boolean isShown() {
        return entity.isSpawned();
    }

    //Internal
    private void trySpawn() {
        if (entity.isSpawned()) return;
        entity.setInvisible(true);
        entity.setHealth(entity.getMaxHealth());
        entity.spawn(calculateLoc());
    }

    @Override
    public void run() {
        if (!entity.isSpawned()) return;
        entity.move(calculateLoc());
    }

    private Location calculateLoc() {
        if (getProtocolVersion(player) == 47) {
            return player.getLocation().add(player.getEyeLocation().getDirection().multiply(20));
        } else {
            return player.getLocation().add(0, -300, 0);
        }
    }

    //Utils
    public static int toFixedPoint(double d) {
        return (int) Math.floor(d * 32.0D);
    }
    public static byte toByteAngle(double d) {
        return (byte) ((int) (d * 256.0F / 360.0F));
    }
    private static Field playerConnectionField = makeField(getNmsClass("EntityPlayer"), "playerConnection");
    private static Field networkManagerField = makeField(getNmsClass("PlayerConnection"), "networkManager");
    private static Method getVersionMethod = makeMethod(getNmsClass("NetworkManager"), "getVersion");
    public static int getProtocolVersion(Player player) {
        Object handle = getHandle(player);
        Object connection = getField(playerConnectionField, handle);
        Object networkManager = getField(networkManagerField, connection);
        if (getVersionMethod == null) { //Go to server's default version 
            if (Reflection.getVersion().startsWith("v1_8")) {
                return 47;
            } else if (Reflection.getVersion().startsWith("v1_7")) {
                return 5;
            } else throw new RuntimeException("Unsupported server version " + Reflection.getVersion());
        }
        int version = callMethod(getVersionMethod, networkManager);
        return version;
    }
    public static int[] doVelocityMagic(double velocityX, double velocityY, double velocityZ) {
        double d0 = 3.9D;
        if (velocityX < -d0) {
            velocityX = -d0;
        }
        if (velocityY < -d0) {
            velocityY = -d0;
        }
        if (velocityZ < -d0) {
            velocityZ = -d0;
        }
        if (velocityX > d0) {
            velocityX = d0;
        }
        if (velocityY > d0) {
            velocityY = d0;
        }
        if (velocityZ > d0) {
            velocityZ = d0;
        }
        return new int[] {(int)(velocityX * 8000.0D), (int)(velocityY * 8000.0D), (int)(velocityZ * 8000.0D)}; //I wish methods could return multiple values
    }
}