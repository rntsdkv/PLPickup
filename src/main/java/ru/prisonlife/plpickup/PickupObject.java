package ru.prisonlife.plpickup;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.UUID;

/**
 * @author prot_cn
 * @project PLPickup
 */

public class PickupObject {

    private String name;
    private World world;
    private Double x;
    private Double y;
    private Double z;
    private Material id;
    private UUID uuid;

    public PickupObject(String name, World world, Double x, Double y, Double z, Material id, UUID uuid) {
        this.name = name;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = id;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getZ() {
        return z;
    }

    public void setZ(Double z) {
        this.z = z;
    }

    public Material getId() {
        return id;
    }

    public void setId(Material id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Location getLocation() {
        return new Location(world, x, y, z);
    }
}
