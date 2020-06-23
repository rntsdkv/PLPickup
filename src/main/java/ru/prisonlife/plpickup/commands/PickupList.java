package ru.prisonlife.plpickup.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ru.prisonlife.plpickup.Main;
import ru.prisonlife.plpickup.PickupLoader;
import ru.prisonlife.plpickup.PickupObject;

import java.util.ArrayList;
import java.util.List;

import static ru.prisonlife.plpickup.Main.colorize;
import static ru.prisonlife.plpickup.Main.getPickupLoader;

public class PickupList implements CommandExecutor {

    private Main plugin;

    public PickupList(Main main) {
        this.plugin = main;
        plugin.getCommand("pickuplist").setExecutor(this);
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

        if (config.getConfigurationSection("pickups") == null) {
            player.sendMessage(colorize(config.getString("messages.pickupsNotFound")));
            return true;
        }

        String pickupInfo = getPickupListText(pickupLoader.getPickups(), strings);
        player.sendMessage(pickupInfo);
        return true;
    }

    private String getPickupListText(List<PickupObject> list, String[] strings) {
        StringBuilder text = new StringBuilder();

        ArrayList<String> pickupNames = new ArrayList<>();

        for (PickupObject pickupObject : list) {
            pickupNames.add(pickupObject.getName());
        }

        text.append(colorize("&l&3Список созданных пикапов:\n&b"));
        text.append(String.join(", ", pickupNames));
        return text.toString();
    }
}
