package ru.prisonlife.plpickup.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import ru.prisonlife.plpickup.Main;
import ru.prisonlife.plpickup.PickupLoader;
import ru.prisonlife.plpickup.PickupObject;

import java.util.UUID;

import static ru.prisonlife.plpickup.Main.colorize;
import static ru.prisonlife.plpickup.Main.getPickupLoader;

public class PickupDelete implements CommandExecutor {

    private Main plugin;

    public PickupDelete(Main main) {
        this.plugin = main;
        plugin.getCommand("pickup delete").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(colorize(plugin.getConfig().getString("messages.wrongSender")));
        }

        Player player = (Player) commandSender;
        PickupLoader pickupLoader = getPickupLoader();

        if (strings.length == 0) {
            player.sendMessage(colorize(plugin.getConfig().getString("messages.notEnoughName")));
            return false;
        }

        String pickupName = strings[1];

        if (!pickupLoader.exists(pickupName)) {
            player.sendMessage(colorize(plugin.getConfig().getString("messages.pickupNameNotFound")));
            return true;
        }

        PickupObject pickupObject = pickupLoader.fromConfig(pickupName);
        UUID pickupUuid = pickupObject.getUuid();

        if (pickupUuid == null) {
            player.sendMessage(colorize(plugin.getConfig().getString("messages.errorUuid")));
            return true;
        }

        Entity armorStand = Bukkit.getEntity(pickupUuid);
        armorStand.remove();
        pickupLoader.removePickupObject(pickupName);
        player.sendMessage(colorize(plugin.getConfig().getString("messages.pickupDelete")));
        return true;
    }
}
