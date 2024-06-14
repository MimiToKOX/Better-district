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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DistrictCommand implements CommandExecutor {

    private final Main plugin;
    private final ScoreboardManager scoreboardManager;
    private final Map<UUID, String> pendingInvites = new HashMap<>();

    public DistrictCommand(Main plugin) {
        this.plugin = plugin;
        this.scoreboardManager = Bukkit.getScoreboardManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length < 1) {
            MessageUtil.sendError(sender, "Correct usage: /district <invite/accept/leave/removeplayer/chat>");
            return true;
        }

        if (args[0].equalsIgnoreCase("invite")) {
            handleInviteCommand(sender, args);
        } else if (args[0].equalsIgnoreCase("accept")) {
            handleAcceptCommand(sender, args);
        } else if (args[0].equalsIgnoreCase("leave")) {
            handleLeaveCommand(sender);
        } else if (args[0].equalsIgnoreCase("removeplayer")) {
            handleRemovePlayerCommand(sender, args);
        } else if (args[0].equalsIgnoreCase("chat")) {
            handleChatCommand(sender, args);
        } else {
            MessageUtil.sendError(sender, "Incorrect command! Use: /district <invite/accept/leave/removeplayer/chat>");
        }

        return true;
    }

    private void handleInviteCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtil.sendError(sender, "Only players can use this command.");
            return;
        }

        Player leader = (Player) sender;
        if (args.length < 2) {
            MessageUtil.sendError(sender, "Correct usage: /district invite <player>");
            return;
        }

        String districtName = getPlayerDistrict(leader);
        if (districtName == null) {
            MessageUtil.sendError(sender, "You are not the leader of any district.");
            return;
        }

        if (!isPlayerLeader(leader, districtName)) {
            MessageUtil.sendError(sender, "You are not the district leader  " + districtName);
            return;
        }

        Player invitee = Bukkit.getPlayer(args[1]);
        if (invitee == null) {
            MessageUtil.sendError(sender, "You are not the district leader ");
            return;
        }

        pendingInvites.put(invitee.getUniqueId(), districtName);
        MessageUtil.sendSuccess(sender, "Player invited " + invitee.getName() + " to the district " + districtName);
        MessageUtil.sendSuccess(invitee, "You have been invited to the district " + districtName + ". Use /district accept to join.");
    }

    private void handleAcceptCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtil.sendError(sender, "Only players can use this command.");
            return;
        }

        Player player = (Player) sender;
        String districtName = pendingInvites.remove(player.getUniqueId());
        if (districtName == null) {
            MessageUtil.sendError(sender, "You have no invitations to the district.");
            return;
        }

        Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
        Team team = scoreboard.getTeam(districtName);
        if (team == null) {
            MessageUtil.sendError(sender, "District " + districtName + " does not exist.");
            return;
        }

        team.addEntry(player.getName());
        MessageUtil.sendSuccess(player, "You have joined the district " + districtName);
    }

    private void handleLeaveCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            MessageUtil.sendError(sender, "Only players can use this command.");
            return;
        }

        Player player = (Player) sender;
        String districtName = getPlayerDistrict(player);
        if (districtName == null) {
            MessageUtil.sendError(sender, "You are not a member of any district.");
            return;
        }

        Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
        Team team = scoreboard.getTeam(districtName);
        if (team == null) {
            MessageUtil.sendError(sender, "District " + districtName + " does not exist.");
            return;
        }

        if (isPlayerLeader(player, districtName)) {
            plugin.getPlayerConfig().set("leaders." + districtName, null);
            plugin.savePlayerConfig();
        }

        team.removeEntry(player.getName());
        MessageUtil.sendSuccess(player, "You have left the district " + districtName);
    }


    private void handleRemovePlayerCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtil.sendError(sender, "Only players can use this command.");
            return;
        }

        Player leader = (Player) sender;
        if (args.length < 2) {
            MessageUtil.sendError(sender, "Correct usage: /district removeplayer <player>");
            return;
        }

        String districtName = getPlayerDistrict(leader);
        if (districtName == null) {
            MessageUtil.sendError(sender, "You are not the leader of any district.");
            return;
        }

        if (!isPlayerLeader(leader, districtName)) {
            MessageUtil.sendError(sender, "You are not the district leader " + districtName);
            return;
        }

        Player targetPlayer = Bukkit.getPlayer(args[1]);
        if (targetPlayer == null) {
            MessageUtil.sendError(sender, "Such a player does not exist!");
            return;
        }

        Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
        Team team = scoreboard.getTeam(districtName);
        if (team == null) {
            MessageUtil.sendError(sender, "District " + districtName + " does not exist.");
            return;
        }

        team.removeEntry(targetPlayer.getName());
        MessageUtil.sendSuccess(sender, "Player " + targetPlayer.getName() + " was removed from the district " + districtName);
        MessageUtil.sendSuccess(targetPlayer, "You have been removed from the district " + districtName);
    }

    private void handleChatCommand(CommandSender sender, String[] args) {

        Player player = (Player) sender;
        String districtName = getPlayerDistrict(player);
        if (districtName == null) {
            MessageUtil.sendError(sender, "You are not a member of any district.");
            return;
        }

        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }

        Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
        Team team = scoreboard.getTeam(districtName);
        if (team == null) {
            MessageUtil.sendError(sender, "District " + districtName + " does not exist.");
            return;
        }

        for (String entry : team.getEntries()) {
            Player member = Bukkit.getPlayer(entry);
            if (member != null) {
                member.sendMessage(ChatColor.LIGHT_PURPLE + "[" + districtName + "] " + ChatColor.WHITE + player.getName() + ChatColor.GRAY + " --> " + message.toString().trim());
            }
        }
    }

    private boolean isPlayerLeader(Player player, String districtName) {
        String leaderUUID = plugin.getPlayerConfig().getString("leaders." + districtName);
        return leaderUUID != null && leaderUUID.equals(player.getUniqueId().toString());
    }

    private String getPlayerDistrict(Player player) {
        Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
        for (Team team : scoreboard.getTeams()) {
            if (team.hasEntry(player.getName())) {
                return team.getName();
            }
        }
        return null;
    }
}
