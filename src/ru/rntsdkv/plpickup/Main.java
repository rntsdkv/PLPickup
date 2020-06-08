package ru.rntsdkv.plpickup;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.entity.ArmorStand;

public class Main extends JavaPlugin {
	
	public void onEnable() {
		File config = new File(getDataFolder() + File.separator + "config.yml");
		if (!config.exists()) {
			getLogger().info("PLPickup | Default Config copying...");
			getConfig().options().copyDefaults(true);
			saveDefaultConfig();
		}
		if (getConfig().getConfigurationSection("pickups") != null) {
			for (String pickup : getConfig().getConfigurationSection("pickups").getKeys(false)) {
				World world = Bukkit.getWorld(getConfig().getString("pickups." + pickup + ".world"));
				Double x = Double.parseDouble(getConfig().getString("pickups." + pickup + ".x"));
				Double y = Double.parseDouble(getConfig().getString("pickups." + pickup + ".y"));
				Double z = Double.parseDouble(getConfig().getString("pickups." + pickup + ".z"));
				ArmorStand armorstand = (ArmorStand) Bukkit.getWorld(world.getName()).spawnEntity(new Location(world, x, y, z), EntityType.ARMOR_STAND);
				armorstand.setVisible(false);
				armorstand.setHelmet(new ItemStack(Material.DIAMOND_BLOCK, 1));
				new BukkitRunnable() {
					@Override
		            public void run() {
						if (armorstand != null) {
							armorstand.setHeadPose(new EulerAngle(armorstand.getHeadPose().getX(), armorstand.getHeadPose().getY() + 0.1, armorstand.getHeadPose().getZ()));
						}
		            }
		        }.runTaskTimer(this, 0, 1);
			}
		}
		getLogger().info("PLPickup plugin is enable!");
		new Commands(this);
	}
	
	public void onDisable() {
		if (getConfig().getConfigurationSection("pickups") != null) {
			for (String pickup : getConfig().getConfigurationSection("pickups").getKeys(false)) {
				UUID uiid = UUID.fromString(getConfig().getString("pickups." + pickup + ".uiid"));
				Entity armorstand = Bukkit.getEntity(uiid);
				armorstand.remove();
			}
		}
		getLogger().info("PLPickup plugin is disable!");
	}
}
