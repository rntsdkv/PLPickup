package ru.prisonlife.plpickup.commands;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import ru.prisonlife.plpickup.PickupLoader;
import ru.prisonlife.plpickup.Main;
import ru.prisonlife.plpickup.PickupObject;

import java.util.ArrayList;
import java.util.List;

import static ru.prisonlife.plpickup.Main.*;

public class PickupSet implements CommandExecutor {

    private Main plugin;

    public PickupSet(Main main) {
        this.plugin = main;
        plugin.getCommand("pickupset").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        FileConfiguration config = plugin.getConfig();

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(colorize(config.getString("messages.wrongSender")));
            return true;
        }

        Player player = (Player) commandSender;
        PickupLoader pickupLoader = getPickupLoader();

        if (strings.length < 3) {
            player.sendMessage(colorize(config.getString("messages.notEnoughArguments")));
            return false;
        }

        String pickupId = strings[0];
        String pickupName = strings[1];

        if (!isValidMaterial(pickupId)) {
            player.sendMessage(colorize(config.getString("messages.errorBlockID")));
            return true;
        }
        if (pickupLoader.exists(pickupName)) {
            player.sendMessage(colorize(config.getString("messages.pickupNameAlreadyUsed")));
            return true;
        }

        List<String> words = new ArrayList<>();
        for (int i = 2; i < strings.length; i++) {
            words.add(strings[i]);
        }

        String text = String.join(" ", words);

        World world = player.getWorld();
        Location playerLocation = player.getLocation();

        Double x = playerLocation.getX();
        Double y = playerLocation.getY() - 0.5;
        Double z = playerLocation.getZ();

        Location pickupLocation = new Location(world, x, y, z);
        Material armorStandMaterial = Material.valueOf(pickupId);

        ArmorStand armorStand = createPickupAsArmorStand(player.getWorld(), pickupLocation, text, armorStandMaterial);
        PickupObject pickupObject = new PickupObject(pickupName, world, x, y, z, armorStandMaterial, text, armorStand.getUniqueId());
        pickupLoader.savePickupObject(pickupObject);

        // get rotating process and start it
        Runnable rotationTask = getPickupRotationTask(armorStand);
        Bukkit.getScheduler().runTaskTimer(this.plugin, rotationTask, 0, 1);

        player.sendMessage(colorize(config.getString("messages.pickupSet")));
        return true;
    }

    private boolean isValidMaterial(String materialId) {
        List<String> materials = new ArrayList<>();
        for (Material material : Material.values()) {
            materials.add(material.toString());
        }
        return materials.contains(materialId);
    }

}
