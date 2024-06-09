package com.top1.districtplugin.utility;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtil {

    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void sendMessage(Player player, String message) {
        player.sendMessage(colorize(message));
    }

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(colorize(message));
    }

    public static void sendError(CommandSender sender, String message) {
        sender.sendMessage(colorize(ChatColor.DARK_RED + "☺ " + ChatColor.RED + message));
    }

    public static void sendSuccess(CommandSender sender, String message) {
        sender.sendMessage(colorize(ChatColor.DARK_GREEN + "☺ " + ChatColor.GREEN + message));
    }

    public static String fix(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
