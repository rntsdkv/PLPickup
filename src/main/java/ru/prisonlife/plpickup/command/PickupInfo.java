package ru.prisonlife.plpickup.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
        plugin.getCommand("pickup info").setExecutor(this);
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

        String pickupName = strings[0];

        if (!pickupLoader.exists(pickupName)) {
            player.sendMessage(colorize(plugin.getConfig().getString("messages.pickupNameNotFound")));
            return true;
        }

        PickupObject pickupObject = pickupLoader.fromConfig(pickupName);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(colorize("&l&3Информация о пикапе " + pickupName + ":\n"));
        stringBuilder.append(colorize("&l&3Мир: &b" + pickupObject.getWorld().getName()));
        stringBuilder.append(colorize("&l&3Координаты: &b" + pickupObject.getX() + " | " + pickupObject.getY() + " | " + pickupObject.getZ()));
        stringBuilder.append(colorize("&l&3Блок: &b" + pickupObject.getId()));
        stringBuilder.append(colorize("&l&3Текст: &b" + pickupObject.getText()));

        player.sendMessage(stringBuilder.toString());
        return true;
    }
}
