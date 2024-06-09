package com.top1.districtplugin.pl.commands;

import com.top1.districtplugin.utility.MessageUtil;
import com.top1.districtplugin.Main;
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
            MessageUtil.sendError(sender, "Poprawne użycie: /district <invite/accept/leave/removeplayer/chat>");
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
            MessageUtil.sendError(sender, "Niepoprawna komenda! Użyj: /district <invite/accept/leave/removeplayer/chat>");
        }

        return true;
    }

    private void handleInviteCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtil.sendError(sender, "Tylko gracze mogą używać tej komendy.");
            return;
        }

        Player leader = (Player) sender;
        if (args.length < 2) {
            MessageUtil.sendError(sender, "Poprawne użycie: /district invite <player>");
            return;
        }

        String districtName = getPlayerDistrict(leader);
        if (districtName == null) {
            MessageUtil.sendError(sender, "Nie jesteś liderem żadnego dystryktu.");
            return;
        }

        if (!isPlayerLeader(leader, districtName)) {
            MessageUtil.sendError(sender, "Nie jesteś liderem dystryktu " + districtName);
            return;
        }

        Player invitee = Bukkit.getPlayer(args[1]);
        if (invitee == null) {
            MessageUtil.sendError(sender, "Taki gracz nie istnieje!");
            return;
        }

        pendingInvites.put(invitee.getUniqueId(), districtName);
        MessageUtil.sendSuccess(sender, "Zaproszono gracza " + invitee.getName() + " do dystryktu " + districtName);
        MessageUtil.sendSuccess(invitee, "Zostałeś zaproszony do dystryktu " + districtName + ". Użyj /district accept, aby dołączyć.");
    }

    private void handleAcceptCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtil.sendError(sender, "Tylko gracze mogą używać tej komendy.");
            return;
        }

        Player player = (Player) sender;
        String districtName = pendingInvites.remove(player.getUniqueId());
        if (districtName == null) {
            MessageUtil.sendError(sender, "Nie masz żadnych zaproszeń do dystryktu.");
            return;
        }

        Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
        Team team = scoreboard.getTeam(districtName);
        if (team == null) {
            MessageUtil.sendError(sender, "Dystrykt " + districtName + " nie istnieje.");
            return;
        }

        team.addEntry(player.getName());
        MessageUtil.sendSuccess(player, "Dołączyłeś do dystryktu " + districtName);
    }

    private void handleLeaveCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            MessageUtil.sendError(sender, "Tylko gracze mogą używać tej komendy.");
            return;
        }

        Player player = (Player) sender;
        String districtName = getPlayerDistrict(player);
        if (districtName == null) {
            MessageUtil.sendError(sender, "Nie jesteś członkiem żadnego dystryktu.");
            return;
        }

        Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
        Team team = scoreboard.getTeam(districtName);
        if (team == null) {
            MessageUtil.sendError(sender, "Dystrykt " + districtName + " nie istnieje.");
            return;
        }

        if (isPlayerLeader(player, districtName)) {
            plugin.getPlayerConfig().set("leaders." + districtName, null);
            plugin.savePlayerConfig();
        }

        team.removeEntry(player.getName());
        MessageUtil.sendSuccess(player, "Opuściłeś dystrykt " + districtName);
    }


    private void handleRemovePlayerCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtil.sendError(sender, "Tylko gracze mogą używać tej komendy.");
            return;
        }

        Player leader = (Player) sender;
        if (args.length < 2) {
            MessageUtil.sendError(sender, "Poprawne użycie: /district removeplayer <player>");
            return;
        }

        String districtName = getPlayerDistrict(leader);
        if (districtName == null) {
            MessageUtil.sendError(sender, "Nie jesteś liderem żadnego dystryktu.");
            return;
        }

        if (!isPlayerLeader(leader, districtName)) {
            MessageUtil.sendError(sender, "Nie jesteś liderem dystryktu " + districtName);
            return;
        }

        Player targetPlayer = Bukkit.getPlayer(args[1]);
        if (targetPlayer == null) {
            MessageUtil.sendError(sender, "Taki gracz nie istnieje!");
            return;
        }

        Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
        Team team = scoreboard.getTeam(districtName);
        if (team == null) {
            MessageUtil.sendError(sender, "Dystrykt " + districtName + " nie istnieje.");
            return;
        }

        team.removeEntry(targetPlayer.getName());
        MessageUtil.sendSuccess(sender, "Gracz " + targetPlayer.getName() + " został usunięty z dystryktu " + districtName);
        MessageUtil.sendSuccess(targetPlayer, "Zostałeś usunięty z dystryktu " + districtName);
    }

    private void handleChatCommand(CommandSender sender, String[] args) {

        Player player = (Player) sender;
        String districtName = getPlayerDistrict(player);
        if (districtName == null) {
            MessageUtil.sendError(sender, "Nie jesteś członkiem żadnego dystryktu.");
            return;
        }

        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }

        Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
        Team team = scoreboard.getTeam(districtName);
        if (team == null) {
            MessageUtil.sendError(sender, "Dystrykt " + districtName + " nie istnieje.");
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
