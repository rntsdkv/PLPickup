package ru.prisonlife.plpickup.command;

import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import ru.prisonlife.plpickup.Main;
import ru.prisonlife.plpickup.PickupLoader;
import ru.prisonlife.plpickup.PickupObject;

import static java.lang.String.*;
import static ru.prisonlife.plpickup.Main.*;

public class PickupManage implements CommandExecutor{
	
	private final Main plugin;
	
	public PickupManage(Main main) {
		this.plugin = main;
		plugin.getCommand("pickup").setExecutor(this);
		plugin.getConfig();
	}
	
	/**
	 * CONFIG:
	 * 
	 * pickups:
	 *   <name STRING>:
	 *     world: <world WORLD>
	 *     x: <x DOUBLE>
	 *     y: <y DOUBLE>
	 *     z: <z DOUBLE>   
	 *     id: <id STRING>
	 *     uuid: <uiid UIID>
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player) || args.length < 1) {
			sender.sendMessage("This command can use only players!");
			return true;
		}

		Player player = (Player) sender;
		String commandType = args[0];
		PickupLoader pickupLoader = getPickupLoader();

		if (commandType.equals("set")) {

			if (args.length != 3) {
				return false;
			}

			String pickupId = args[1];
			String pickupName = args[2];

			if (!isValidMaterial(pickupId)) {
				player.sendMessage(colorize("&l&cНеправильный id!"));
				return true;
			}
			else if (pickupLoader.exists(pickupName)) {
				player.sendMessage(colorize("&l&cТакой пикап уже существует!"));
				return true;
			}

			World world = player.getWorld();
			Location playerLocation = player.getLocation();

			Double x = playerLocation.getX();
			Double y = playerLocation.getY();
			Double z = playerLocation.getZ();

			Material armorStandMaterial = Material.valueOf(pickupId);

			ArmorStand armorStand = createPickupAsArmorStand(player.getWorld(), playerLocation, armorStandMaterial);
			PickupObject pickupObject = new PickupObject(pickupName, world, x, y, z, armorStandMaterial, armorStand.getUniqueId());
			pickupLoader.savePickupObject(pickupObject);

			// get rotating process and start it
			Runnable rotationTask = getPickupRotationTask(armorStand);
			Bukkit.getScheduler().runTaskTimer(this.plugin, rotationTask, 0, 1);

			player.sendMessage(colorize(format("&l&6Создан пикап %s!", pickupName)));

		} else if (commandType.equals("delete")) {

			if (args.length != 2) {
				return false;
			}

			String pickupName = args[1];

			if (!pickupLoader.exists(pickupName)) {
				player.sendMessage(colorize("&l&cТакого пикапа не существует!"));
				return true;
			}

			PickupObject pickupObject = pickupLoader.fromConfig(pickupName);
			UUID pickupUuid = pickupObject.getUuid();

			if (pickupUuid == null) {
				player.sendMessage(colorize("&l&cОшибка!"));
				return true;
			}

			Entity armorStand = Bukkit.getEntity(pickupUuid);
			armorStand.remove();
			pickupLoader.removePickupObject(pickupName);
			player.sendMessage(format("&l&6Удален пикап %s", pickupName));

		} else if (args[0].equals("list")) {

			String pickupInfo = getPickupListText(pickupLoader.getPickups());
			player.sendMessage(pickupInfo);

		} else if (commandType.equals("tp")) {

			String pickupName = args[1];

			if (!pickupLoader.exists(pickupName)) {
				player.sendMessage(colorize("&l&cТакого пикапа не существует!"));
				return true;
			}

			PickupObject pickupObject = pickupLoader.fromConfig(pickupName);
			player.teleport(pickupObject.getLocation());
			player.sendMessage(colorize("&l&6Вы были телепортированы к пикаапу"));
		}

		return true;
	}


	private String getPickupListText(List<PickupObject> list) {
		StringBuilder text = new StringBuilder();

		for (PickupObject pickup : list) {
			text.append(pickup.getName())
					.append(" | Мир: ")
					.append(pickup.getWorld().getName())
					.append(" | X: ").append(pickup.getX())
					.append(" | Y: ").append(pickup.getY())
					.append(" | Z: ").append(pickup.getZ())
					.append(" | ID: ").append(pickup.getId().name());
		}

		return text.toString();
	}

	private boolean isValidMaterial(String materialId) {
		List<String> materials = new ArrayList<>();
		for (Material material : Material.values()) {
			materials.add(material.toString());
		}
		return materials.contains(materialId);
	}

	private String colorize(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}
}
