package ru.prisonlife.plpickup.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ru.prisonlife.plpickup.Main;
import ru.prisonlife.plpickup.PickupLoader;
import ru.prisonlife.plpickup.PickupObject;

import static ru.prisonlife.plpickup.Main.colorize;
import static ru.prisonlife.plpickup.Main.getPickupLoader;

public class PickupInfo implements CommandExecutor {

    private Main plugin;

    public PickupInfo(Main main) {
        this.plugin = main;
        plugin.getCommand("pickupinfo").setExecutor(this);
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

        if (strings.length != 1) {
            player.sendMessage(colorize(config.getString("messages.notEnoughName")));
            return false;
        }

        String pickupName = strings[0];

        if (!pickupLoader.exists(pickupName)) {
            player.sendMessage(colorize(config.getString("messages.pickupNameNotFound")));
            return true;
        }

        PickupObject pickupObject = pickupLoader.fromConfig(pickupName);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(colorize("&l&3Информация о пикапе " + pickupName + ":\n"));
        stringBuilder.append(colorize("&l&3Мир: &b" + pickupObject.getWorld().getName() + "\n"));
        Location pickupLocation = pickupObject.getLocation();
        stringBuilder.append(colorize("&l&3Координаты: &b" + pickupLocation.getBlockX() + " | " + pickupLocation.getBlockY() + " | " + pickupLocation.getBlockZ() + "\n"));
        stringBuilder.append(colorize("&l&3Блок: &b" + pickupObject.getId() + "\n"));
        stringBuilder.append(colorize("&l&3Текст: &b" + pickupObject.getText()));

        player.sendMessage(stringBuilder.toString());
        return true;
    }
}
