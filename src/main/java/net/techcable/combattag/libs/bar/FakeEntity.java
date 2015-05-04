package net.techcable.combattag.libs.bar;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;

import static net.techcable.techutils.Reflection.*;

/**
* Created by Nicholas Schlabach on 4/16/2015.
*/
@RequiredArgsConstructor
public abstract class FakeEntity {
    static {
        //Create fake Entity ID
        Field entityCountField = makeField(getNmsClass("Entity"), "entityCount");
        int entityCount = getField(entityCountField, null);
        fakeEntityId = entityCount++;
        setField(entityCountField, null, entityCount);
    }
    @Getter
    private boolean spawned = false;

    /**
     * Spawn the fake entity at the specified location
     *
     * @param location location to spawn this entity
     */
    public void spawn(Location location) {
        if (isSpawned()) throw new IllegalStateException("Already spawned");
        if (!location.getWorld().equals(player.getWorld())) throw new IllegalArgumentException("Must be in the same world as the player");
        this.location = location;
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(FakeEntity.fakeEntityId, getMobTypeId(), location, velocity, watcher);
        packet.sendPacket(player);
        spawned = true;
    }

    public void despawn() {
        if (!isSpawned()) throw new IllegalStateException("Can only despawn a spawned entity");
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(FakeEntity.fakeEntityId);
        packet.sendPacket(player);
        spawned = false;
    }

    public void update() {
        if (!isSpawned()) return;
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(FakeEntity.fakeEntityId, watcher);
        packet.sendPacket(player);
    }

    public void move(Location location) {//Thread safe with lock
        if (!location.getWorld().equals(player.getWorld())) throw new IllegalArgumentException("Must be in the same world as the player");
        this.location = location;
        boolean onGround = location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY() - 1, location.getBlockZ()).getType().isSolid(); //Worst case scenario i see an inconsistent state
        PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(FakeEntity.fakeEntityId, location, onGround);
        packet.sendPacket(player);
    }

    protected static final int fakeEntityId; //The entity id of all fake entities
    protected final Player player;
    protected final WrappedDataWatcher watcher = new WrappedDataWatcher();
    private final Vector velocity = new Vector(0, 0, 0);
    private Location location;

    public void setHealth(float health) {
        if (health < 0.1F) { //Don't let it die
            health = 0.1F;
        } else if (health > getMaxHealth()) {
            health = getMaxHealth();
        }
        watcher.setFloat(6, health);
    }

    public void setCustomName(String name) {
        if (BossBar.getProtocolVersion(player) > 5) {
            watcher.setString(2, name);
        } else {
            watcher.setString(10, name);
        }
    }

    public void setInvisible(boolean invisible) {
        set0Flag(5, invisible);
    }

    private void set0Flag(int flagIndex, boolean value) {
        byte b = watcher.hasIndex(0) ? watcher.getByte(0) : 0;

        if (value) {
            watcher.setByte(0, (byte) (b | 1 << flagIndex));
        } else {
            watcher.setByte(0, (byte) (b & ~(1 << flagIndex)));
        }
    }

    protected abstract float getMaxHealth();
    protected abstract byte getMobTypeId();
}
