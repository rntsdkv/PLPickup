package ru.prisonlife.plpickup.command;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
        plugin.getCommand("pickup set").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(colorize(plugin.getConfig().getString("messages.wrongSender")));
        }

        Player player = (Player) commandSender;
        PickupLoader pickupLoader = getPickupLoader();

        if (strings.length < 2) {
            player.sendMessage(colorize(plugin.getConfig().getString("messages.notEnoughArguments")));
            return false;
        }

        String pickupId = strings[1];
        String pickupName = strings[2];

        if (!isValidMaterial(pickupId)) {
            player.sendMessage(colorize(plugin.getConfig().getString("messages.errorBlockID")));
            return true;
        }
        if (pickupLoader.exists(pickupName)) {
            player.sendMessage(colorize(plugin.getConfig().getString("messages.pickupNameAlreadyUsed")));
            return true;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strings.length && i != 0 && i != 1; i++) {
            stringBuilder.append(strings[i]);
        }
        String text = stringBuilder.toString();

        World world = player.getWorld();
        Location playerLocation = player.getLocation();

        Double x = playerLocation.getX();
        Double y = playerLocation.getY() - 0.5;
        Double z = playerLocation.getZ();

        Material armorStandMaterial = Material.valueOf(pickupId);

        ArmorStand armorStand = createPickupAsArmorStand(player.getWorld(), playerLocation, text, armorStandMaterial);
        PickupObject pickupObject = new PickupObject(pickupName, world, x, y, z, armorStandMaterial, text, armorStand.getUniqueId());
        pickupLoader.savePickupObject(pickupObject);

        // get rotating process and start it
        Runnable rotationTask = getPickupRotationTask(armorStand);
        Bukkit.getScheduler().runTaskTimer(this.plugin, rotationTask, 0, 1);

        player.sendMessage(colorize(plugin.getConfig().getString("messages.pickupSet")));
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
