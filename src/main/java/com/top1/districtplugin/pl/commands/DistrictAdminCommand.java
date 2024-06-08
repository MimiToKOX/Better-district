package com.top1.districtplugin.pl.commands;

import com.top1.districtplugin.utility.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class DistrictAdminCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final ScoreboardManager scoreboardManager;

    public DistrictAdminCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.scoreboardManager = Bukkit.getScoreboardManager();

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("betterdistrict.districtadmin")) {
            MessageUtil.sendError(sender, "Nie masz permisji do tej komendy! &8&o(betterdistrict.districtadmin)");
            return true;
        }

        if (args.length < 1) {
            MessageUtil.sendError(sender, "Poprawne użycie: /districtadmin <player/district/help>");
            return true;
        }

        if (args[0].equalsIgnoreCase("help")) {
            HelpCommand(sender, args);
            return true;
        } else if (args[0].equalsIgnoreCase("player")) {
            handlePlayerCommand(sender, args);
        } else if (args[0].equalsIgnoreCase("district")) {
            handleDistrictCommand(sender, args);
        } else {
            MessageUtil.sendError(sender,"Nie poprawna komenda! Użyj: &4/districtadmin help &cżeby dostać więcej informacji!");
        }

        return true;
    }

    private void handlePlayerCommand(CommandSender sender, String[] args) {
        if (args.length < 4) {
            MessageUtil.sendError(sender, "Poprawne użycie: /districtadmin player <nick> <add/remove> <district>");
            return;
        }

        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            MessageUtil.sendError(sender, "Taki gracz nie istnieje!");
            return;
        }

        String action = args[2];
        String districtName = args[3];
        Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
        Team team = scoreboard.getTeam(districtName);

        if (team == null) {
            MessageUtil.sendError(sender, "Taki Dystrykt nie istnieje!");
            return;
        }

        if (action.equalsIgnoreCase("add")) {
            team.addEntry(player.getName());
            MessageUtil.sendSucces(player, "Zostałeś dodany do dystryktu " + districtName);
            MessageUtil.sendSucces(sender, "Gracz" + player.getName() + " został pomyślnie dodany do dystryktu " + districtName);
        } else if (action.equalsIgnoreCase("remove")) {
            team.removeEntry(player.getName());
            MessageUtil.sendSucces(player, "Zostałeś usunięty z dystryktu " + districtName);
            MessageUtil.sendSucces(sender, "Gracz " + player.getName() + " został pomyślnie usunięty z dystryktu " + districtName);
        } else {
            MessageUtil.sendError(sender, "Nie poprawna akcja! użyj add/remove");
        }
    }

    private void handleDistrictCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtil.sendError(sender, "Poprawne użycie: /districtadmin district <create/remove/edit>");
            return;
        }

        String action = args[1];
        Scoreboard scoreboard = scoreboardManager.getMainScoreboard();

        if (action.equalsIgnoreCase("create")) {
            int districtNumber = scoreboard.getTeams().size() + 1;
            Team newTeam = scoreboard.registerNewTeam("D" + districtNumber);
            newTeam.setPrefix(ChatColor.BOLD + "D" + districtNumber + " " + ChatColor.WHITE);
            MessageUtil.sendSucces(sender, "Dystrykt D" + districtNumber + " Został stworzony!");
        } else if (action.equalsIgnoreCase("remove")) {
            if (args.length < 3) {
                MessageUtil.sendError(sender, "Poprawne użycie: /districtadmin district remove <district>");
                return;
            }

            String districtName = args[2];
            Team team = scoreboard.getTeam(districtName);
            if (team == null) {
                MessageUtil.sendError(sender, "Taki Dystrykt nie istnieje!");
                return;
            }

            team.unregister();
            MessageUtil.sendSucces(sender, "Dystykt " + districtName + " został pomyślnie usunienty");
        } else if (action.equalsIgnoreCase("edit")) {
            if (args.length < 5) {
                MessageUtil.sendError(sender, "Poprawne użycie: /districtadmin district edit <district> <prefix/color> <value>");
                return;
            }

            String districtName = args[2];
            Team team = scoreboard.getTeam(districtName);
            if (team == null) {
                MessageUtil.sendError(sender, "Taki Dystrykt nie istnieje!");
                return;
            }

            String editType = args[3];
            String newValue = args[4];

            if (editType.equalsIgnoreCase("prefix")) {
                team.setPrefix(ChatColor.translateAlternateColorCodes('&', newValue));
                MessageUtil.sendSucces(sender,"Prefix dystryktu został pomyślnie zmieniony na: " + districtName);
            } else if (editType.equalsIgnoreCase("color")) {
                ChatColor color = ChatColor.valueOf(newValue.toUpperCase());
                team.setColor(color);
                MessageUtil.sendSucces(sender,"Kolor dystryktu został pomyślnie zmieniony dla dystryktu: " + districtName);
            } else {
                MessageUtil.sendError(sender,"Niepoprawna akcja! użyj: prefix/color");
            }
        } else {
            MessageUtil.sendError(sender,"Niepoprawna akcja! użyj: create/remove/edit.");
        }
    }


    private void HelpCommand(CommandSender sender, String[] args) {
        MessageUtil.sendMessage(sender,"Jajo");
    }
}