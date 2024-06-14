package com.top1.districtplugin.eng.commands;

import com.top1.districtplugin.Main;
import com.top1.districtplugin.utility.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class DistrictAdminCommand implements CommandExecutor {

    private final Main plugin;
    private final ScoreboardManager scoreboardManager;

    public DistrictAdminCommand(Main plugin) {
        this.plugin = plugin;
        this.scoreboardManager = Bukkit.getScoreboardManager();
    }

    private boolean isPlayerLeader(Player player, String districtName) {
        String leaderUUID = plugin.getPlayerConfig().getString("leaders." + districtName);
        return leaderUUID != null && leaderUUID.equals(player.getUniqueId().toString());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("betterdistrict.districtadmin")) {
            MessageUtil.sendError(sender, "You have no permissions to this command! &8&o(betterdistrict.districtadmin)");
            return true;
        }

        if (args.length < 1) {
            MessageUtil.sendError(sender, "Correct usage: /districtadmin <player/district/help>");
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
            MessageUtil.sendError(sender, "Not a valid command! Use: &4/districtadmin help &cto get more information!");
        }

        return true;
    }

    private void handlePlayerCommand(CommandSender sender, String[] args) {
        if (args.length < 4) {
            MessageUtil.sendError(sender, "Correct usage: /districtadmin player <username> <add/remove/leader> <district>");
            return;
        }

        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            MessageUtil.sendError(sender, "Such a player does not exist!");
            return;
        }

        String action = args[2];
        String districtName = args[3];
        Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
        Team team = scoreboard.getTeam(districtName);

        if (team == null) {
            MessageUtil.sendError(sender, "Such a District does not exist!");
            return;
        }

        if (action.equalsIgnoreCase("add")) {
            team.addEntry(player.getName());
            MessageUtil.sendSuccess(player, "You have been added to the district " + districtName);
            MessageUtil.sendSuccess(sender, "Player " + player.getName() + " was successfully added to the district " + districtName);
        } else if (action.equalsIgnoreCase("remove")) {
            team.removeEntry(player.getName());
            MessageUtil.sendSuccess(player, "You have been removed from the district " + districtName);
            MessageUtil.sendSuccess(sender, "Player " + player.getName() + " was successfully removed from the district " + districtName);
        } else if (action.equalsIgnoreCase("leader")) {
            plugin.getPlayerConfig().set("leaders." + districtName, player.getUniqueId().toString());
            plugin.savePlayerConfig();
            MessageUtil.sendSuccess(sender, "Player " + player.getName() + " was successfully set up as district leader " + districtName);
        } else {
            MessageUtil.sendError(sender, "Incorrect action! use: add/remove/leader");
        }

        if (action.equalsIgnoreCase("remove") && isPlayerLeader(player, districtName)) {
            plugin.getPlayerConfig().set("leaders." + districtName, null);
            plugin.savePlayerConfig();
        }
    }


    private void handleDistrictCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtil.sendError(sender, "Correct usage: /districtadmin district <create/remove/edit>");
            return;
        }

        String action = args[1];
        Scoreboard scoreboard = scoreboardManager.getMainScoreboard();

        if (action.equalsIgnoreCase("create")) {
            int districtNumber = scoreboard.getTeams().size() + 1;
            Team newTeam = scoreboard.registerNewTeam("D" + districtNumber);
            newTeam.setPrefix(ChatColor.BOLD + "D" + districtNumber + " " + ChatColor.WHITE);
            MessageUtil.sendSuccess(sender, "District D" + districtNumber + " was created!");
        } else if (action.equalsIgnoreCase("remove")) {
            if (args.length < 3) {
                MessageUtil.sendError(sender, "Correct usage: /districtadmin district remove <district>");
                return;
            }

            String districtName = args[2];
            Team team = scoreboard.getTeam(districtName);
            if (team == null) {
                MessageUtil.sendError(sender, "Such a District does not exist!");
                return;
            }

            team.unregister();
            plugin.getPlayerConfig().set("leaders." + districtName, null);
            plugin.savePlayerConfig();
            MessageUtil.sendSuccess(sender, "District " + districtName + " was successfully removed");
        } else if (action.equalsIgnoreCase("edit")) {
            if (args.length < 5) {
                MessageUtil.sendError(sender, "Correct usage: /districtadmin district edit <district> <prefix/color> <value>");
                return;
            }

            String districtName = args[2];
            Team team = scoreboard.getTeam(districtName);
            if (team == null) {
                MessageUtil.sendError(sender, "Such a District does not exist!");
                return;
            }

            String editType = args[3];
            String newValue = args[4];

            if (editType.equalsIgnoreCase("prefix")) {
                team.setPrefix(ChatColor.translateAlternateColorCodes('&', newValue + " "));
                MessageUtil.sendSuccess(sender, "The district prefix was successfully changed to: " + districtName);
            } else if (editType.equalsIgnoreCase("color")) {
                ChatColor color = ChatColor.valueOf(newValue.toUpperCase());
                team.setColor(color);
                MessageUtil.sendSuccess(sender, "District colour has been successfully changed for the district: " + districtName);
            } else {
                MessageUtil.sendError(sender, "Incorrect action! use: prefix/color");
            }
        } else {
            MessageUtil.sendError(sender, "Incorrect action! use: create/remove/edit.");
        }
    }

    private void HelpCommand(CommandSender sender, String[] args) {
        MessageUtil.sendMessage(sender, "&5---------- &dBetter district &5----------");
        MessageUtil.sendMessage(sender,"&fCommands for players:");
        MessageUtil.sendMessage(sender, "&8- &d/district invite <player> &7- &fIf you are the district founder, invite players!");
        MessageUtil.sendMessage(sender, "&8- &d/district leave &7- &fLeave the district");
        MessageUtil.sendMessage(sender, "&8- &d/district accept <district number> &7- &fAccept a request to join the district");
        MessageUtil.sendMessage(sender, "&8- &d/district removeplayer <player> &7- &fRemove a player from the district");
        MessageUtil.sendMessage(sender, "");
        MessageUtil.sendMessage(sender, "&fCommands for admins:");
        MessageUtil.sendMessage(sender, "&f                   Player");
        MessageUtil.sendMessage(sender, "&8- &d/districtadmin player <nickname> add <district> &7- &fAdd a player to the district");
        MessageUtil.sendMessage(sender, "&8- &d/districtadmin player <nickname> remove <district> &7- &fRemove a player from the district");
        MessageUtil.sendMessage(sender, "&8- &d/districtadmin player <nickname> leader <district> &7- &fSet the district leader");
        MessageUtil.sendMessage(sender, "&f                    District");
        MessageUtil.sendMessage(sender, "&8- &d/districtadmin district create &7- &fCreate a new district");
        MessageUtil.sendMessage(sender, "&8- &d/districtadmin district remove <district> &7- &fRemove a district");
        MessageUtil.sendMessage(sender, "&8- &d/districtadmin district edit <district> color <color in English> &7- &fSet the district color");
        MessageUtil.sendMessage(sender, "&8- &d/districtadmin district edit <district> prefix <district prefix, & can be used> &7- &fSet a new district prefix");
        MessageUtil.sendMessage(sender, "&5---------- &dBetter district &5----------");
    }
}
