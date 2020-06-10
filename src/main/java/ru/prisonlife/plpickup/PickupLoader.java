package ru.prisonlife.plpickup;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ru.prisonlife.plpickup.PickupPather.*;

/**
 * @author prot_cn
 * @project PLPickup
 */

public class PickupLoader {

    public static PickupLoader create(Plugin plugin) {
        return new PickupLoader(plugin);
    }

    private final Plugin plugin;
    private final FileConfiguration config;

    private PickupLoader(Plugin plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
    }

    public PickupObject fromConfig(String pickupName) {
        ConfigurationSection section = config.getConfigurationSection(getPickupConfigPath(pickupName));
        if (section == null) return null;

        String world = section.getString("world");
        Double x = section.getDouble("x");
        Double y = section.getDouble("y");
        Double z = section.getDouble("z");
        String id = section.getString("id");
        String uuid = section.getString("uuid");

        return new PickupObject(pickupName, Bukkit.getWorld(world), x, y, z, Material.valueOf(id), UUID.fromString(uuid));
    }

    public void savePickupObject(PickupObject pickupObject) {
        String pickupName = pickupObject.getName();

        config.set(getWorldConfigPath(pickupName), pickupObject.getWorld().getName());
        config.set(getXCoordinateConfigPath(pickupName), pickupObject.getX());
        config.set(getYCoordinateConfigPath(pickupName), pickupObject.getY());
        config.set(getZCoordinateConfigPath(pickupName), pickupObject.getZ());
        config.set(getIdConfigPath(pickupName), pickupObject.getId().toString());
        config.set(getUuidConfigPath(pickupName), pickupObject.getUuid().toString());
        plugin.saveConfig();
    }

    public void removePickupObject(String pickupName) {
        config.set(getPickupConfigPath(pickupName), null);
    }

    public List<PickupObject> getPickups() {
        List<PickupObject> list = new ArrayList<>();
        ConfigurationSection section = config.getConfigurationSection("pickups");

        if (!Optional.ofNullable(section).isPresent()) return list;
        else for (String pickup : section.getKeys(false)) {
            list.add(fromConfig(pickup));
        }

        return list;
    }

    public boolean exists(String pickupName) {
        return config.getConfigurationSection(getPickupConfigPath(pickupName)) != null;
    }
}
