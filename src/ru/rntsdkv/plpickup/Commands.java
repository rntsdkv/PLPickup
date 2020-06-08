package ru.rntsdkv.plpickup;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import net.md_5.bungee.api.ChatColor;

public class Commands implements CommandExecutor{
	
	private Main plugin;
	
	public Commands(Main main) {
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
	 *     uiid: <uiid UIID>
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command can use only players!");
			return true;
		}
		Player player = (Player) sender;
		if (args.length == 0) {
			return false;
		}
		if (args[0] == "set") {
			if (!(player.hasPermission("plpickup.set"))) {
				player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Вы не имеете доступа к этой команде!");
				return true;
			}
			if (args[1] == null || args[2] == null) {
				player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Недостаточно аргументов!");
				return false;
			}
			if (plugin.getConfig().getConfigurationSection("pickups." + args[2].toString()) != null) {
				player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Пикап с таким именем уже существует, выберите другое!");
				return true;
			}
			World world = player.getWorld();
			Double x = player.getLocation().getX();
			Double y = player.getLocation().getY();
			Double z = player.getLocation().getZ();
			plugin.getConfig().set("pickups." + args[2].toString() + ".world", world);
			plugin.getConfig().set("pickups." + args[2].toString() + ".x", x);
			plugin.getConfig().set("pickups." + args[2].toString() + ".y", y);
			plugin.getConfig().set("pickups." + args[2].toString() + ".z", z);
			plugin.getConfig().set("pickups." + args[2].toString() + ".id", args[1].toString());
			ArmorStand armorstand = (ArmorStand) player.getWorld().spawnEntity(new Location(world, x, y, z), EntityType.ARMOR_STAND);
			armorstand.setVisible(false);
			armorstand.setHelmet(new ItemStack(Material.DIAMOND_BLOCK, 1));
			UUID uiid = armorstand.getUniqueId();
			plugin.getConfig().set("pickups." + args[2].toString() + ".uiid", uiid.toString());
			new BukkitRunnable() {
				@Override
	            public void run() {
					if (armorstand != null) {
						armorstand.setHeadPose(new EulerAngle(armorstand.getHeadPose().getX(), armorstand.getHeadPose().getY() + 0.1, armorstand.getHeadPose().getZ()));
					}
	            }
	        }.runTaskTimer(this.plugin, 0, 1);
	        plugin.saveConfig();
			player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Вы установили парящий блок " + args[2] + "!");
		} else if (args[0] == "delete") {
			if (!(player.hasPermission("plpickup.delete"))) {
				player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Вы не имеете доступа к этой команде!");
				return true;
			}
			if (args[1] == null) {
				player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Необходимо указать название пикапа!");
				return false;
			}
			if (plugin.getConfig().getConfigurationSection("pickups." + args[1].toString()) == null) {
				player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Пикапа с таким именем не существует!");
				return true;
			}
			World world = Bukkit.getWorld(plugin.getConfig().getString("pickups." + args[1] + ".world"));
			Double x = Double.parseDouble(plugin.getConfig().getString("pickups." + args[1] + ".x"));
			Double y = Double.parseDouble(plugin.getConfig().getString("pickups." + args[1] + ".y"));
			Double z = Double.parseDouble(plugin.getConfig().getString("pickups." + args[1] + ".z"));
			UUID uiid = UUID.fromString(plugin.getConfig().getString("pickups." + args[1] + ".uiid"));
			Entity armorstand = Bukkit.getEntity(uiid);
			armorstand.remove();
			plugin.getConfig().set("pickups." + args[1], null);
			plugin.saveConfig();
			player.sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD + "Пикап с именем " + args[1] + " был удалён!");
		} else if (args[0] == "list") {
			if (!(player.hasPermission("plpickup.list"))) {
				player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Вы не имеете доступа к этой команде!");
			}
			if (plugin.getConfig().getConfigurationSection("pickups") == null) {
				player.sendMessage(ChatColor.BOLD + "" + ChatColor.AQUA + "Пикапов пока нет, приходите позже...");
				return true;
			}
			String message = "Информация о пикапах: \n";
			for (String pickup : plugin.getConfig().getConfigurationSection("pickups").getKeys(false)) {
				message += "\nНазвание:" + pickup + " | Мир: " + plugin.getConfig().getString("pickups." + pickup + ".world") + " | X: " + plugin.getConfig().getString("pickups." + pickup + ".x") + " | Y: " + plugin.getConfig().getString("pickups." + pickup + ".y") + " | Z: " + plugin.getConfig().getString("pickups." + pickup + ".z") + " | ID: " + plugin.getConfig().getString("pickups." + pickup + ".id");
			}
			player.sendMessage(ChatColor.BOLD + "" + ChatColor.AQUA + message);
		} else if (args[0] == "tp") {
			if (!(player.hasPermission("plpickup.tp"))) {
				player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Вы не имеете доступа к этой команде!");
			}
			if (plugin.getConfig().getConfigurationSection("pickups." + args[1].toString()) == null) {
				player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Пикапа с таким именем не существует!");
				return true;
			}
			World world = Bukkit.getWorld(plugin.getConfig().getString("pickups." + args[1] + ".world"));
			Double x = Double.parseDouble(plugin.getConfig().getString("pickups." + args[1] + ".x"));
			Double y = Double.parseDouble(plugin.getConfig().getString("pickups." + args[1] + ".y"));
			Double z = Double.parseDouble(plugin.getConfig().getString("pickups." + args[1] + ".z"));
			player.teleport(new Location(world, x, y, z));
			player.sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD + "Вы телепортировались к пикапу!");
		}
		return true;
	}

}
