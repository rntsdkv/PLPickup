package ru.prisonlife.plpickup;

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
import org.bukkit.util.EulerAngle;
import org.bukkit.entity.ArmorStand;
import ru.prisonlife.plpickup.command.PickupManage;

public class Main extends JavaPlugin {

	private static PickupLoader pickupLoader;

	public static PickupLoader getPickupLoader() {
		return pickupLoader;
	}

	public static ArmorStand createPickupAsArmorStand(World world, Location location) {
		ArmorStand armorstand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
		armorstand.setVisible(false);
		armorstand.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_BLOCK, 1));
		return armorstand;
	}

	public static Runnable getPickupRotationTask(ArmorStand armorStand) {
		return () -> {
			if (armorStand != null) {
				EulerAngle headPose = armorStand.getHeadPose();
				armorStand.setHeadPose(new EulerAngle(headPose.getX(), headPose.getY() + 0.1, headPose.getZ()));
			}
		};
	}

	public void onEnable() {
		copyConfigFile();
		initPickupLoader();
		initRotationPickups();
		getLogger().info("PLPickup plugin is enable!");
		new PickupManage(this);
	}

	public void onDisable() {
		clearPickupArmorStands();
		getLogger().info("PLPickup plugin is disable!");
	}

	private void copyConfigFile() {
		File config = new File(getDataFolder() + File.separator + "config.yml");
		if (!config.exists()) {
			getLogger().info("PLPickup | Default Config copying...");
			getConfig().options().copyDefaults(true);
			saveDefaultConfig();
		}
	}

	private void initPickupLoader() {
		pickupLoader = PickupLoader.create(this);
	}

	private void initRotationPickups() {
		for (PickupObject pickup : pickupLoader.getPickups()) {
			ArmorStand pickupAsArmorStand = createPickupAsArmorStand(pickup.getWorld(), pickup.getLocation());
			// get rotating process and start it
			Runnable rotationTask = getPickupRotationTask(pickupAsArmorStand);
			Bukkit.getScheduler().runTaskTimer(this, rotationTask, 0, 1);
		}
	}

	private void clearPickupArmorStands() {
		for (PickupObject pickup : pickupLoader.getPickups()) {
			ArmorStand armorStand = (ArmorStand) Bukkit.getEntity(pickup.getUuid());
			armorStand.remove();
		}
	}
}
