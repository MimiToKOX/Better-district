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
            MessageUtil.sendError(sender, "Nie poprawna komenda! Użyj: &4/districtadmin help &cżeby dostać więcej informacji!");
        }

        return true;
    }

    private void handlePlayerCommand(CommandSender sender, String[] args) {
        if (args.length < 4) {
            MessageUtil.sendError(sender, "Poprawne użycie: /districtadmin player <nick> <add/remove/leader> <district>");
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
            MessageUtil.sendSuccess(player, "Zostałeś dodany do dystryktu " + districtName);
            MessageUtil.sendSuccess(sender, "Gracz " + player.getName() + " został pomyślnie dodany do dystryktu " + districtName);
        } else if (action.equalsIgnoreCase("remove")) {
            team.removeEntry(player.getName());
            MessageUtil.sendSuccess(player, "Zostałeś usunięty z dystryktu " + districtName);
            MessageUtil.sendSuccess(sender, "Gracz " + player.getName() + " został pomyślnie usunięty z dystryktu " + districtName);
        } else if (action.equalsIgnoreCase("leader")) {
            plugin.getPlayerConfig().set("leaders." + districtName, player.getUniqueId().toString());
            plugin.savePlayerConfig();
            MessageUtil.sendSuccess(sender, "Gracz " + player.getName() + " został pomyślnie ustawiony jako lider dystryktu " + districtName);
        } else {
            MessageUtil.sendError(sender, "Niepoprawna akcja! użyj add/remove/leader");
        }

        if (action.equalsIgnoreCase("remove") && isPlayerLeader(player, districtName)) {
            plugin.getPlayerConfig().set("leaders." + districtName, null);
            plugin.savePlayerConfig();
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
            MessageUtil.sendSuccess(sender, "Dystrykt D" + districtNumber + " został stworzony!");
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
            plugin.getPlayerConfig().set("leaders." + districtName, null);
            plugin.savePlayerConfig();
            MessageUtil.sendSuccess(sender, "Dystrykt " + districtName + " został pomyślnie usunięty");
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
                team.setPrefix(ChatColor.translateAlternateColorCodes('&', newValue + " "));
                MessageUtil.sendSuccess(sender, "Prefix dystryktu został pomyślnie zmieniony na: " + districtName);
            } else if (editType.equalsIgnoreCase("color")) {
                ChatColor color = ChatColor.valueOf(newValue.toUpperCase());
                team.setColor(color);
                MessageUtil.sendSuccess(sender, "Kolor dystryktu został pomyślnie zmieniony dla dystryktu: " + districtName);
            } else {
                MessageUtil.sendError(sender, "Niepoprawna akcja! użyj: prefix/color");
            }
        } else {
            MessageUtil.sendError(sender, "Niepoprawna akcja! użyj: create/remove/edit.");
        }
    }

    private void HelpCommand(CommandSender sender, String[] args) {
        MessageUtil.sendMessage(sender, "&5---------- &dBetter district &5----------");
        MessageUtil.sendMessage(sender,"&fKomendy dla gracza:");
        MessageUtil.sendMessage(sender, "&8- &d/district invite <player> &7- &fJeżeli jesteś założycielem dystryktu zapraszaj graczy!");
        MessageUtil.sendMessage(sender, "&8- &d/district leave &7- &fOpuszcza dystrykt");
        MessageUtil.sendMessage(sender, "&8- &d/district accept <numer dystryktu> &7- &fAkcpetuje prozbe o dołączenie do dystryktu");
        MessageUtil.sendMessage(sender, "&8- &d/district removeplayer <player> &7- &fUsuwa gracza z dystryktu");
        MessageUtil.sendMessage(sender, "");
        MessageUtil.sendMessage(sender, "&fKomendy dla admina:");
        MessageUtil.sendMessage(sender, "&f                   Player");
        MessageUtil.sendMessage(sender, "&8- &d/districtadmin player <nick> add <district> &7- &fDodaje gracza do dystryktu");
        MessageUtil.sendMessage(sender, "&8- &d/districtadmin player <nick> remove <district> &7- &fUsuwa gracza z dystryktu");
        MessageUtil.sendMessage(sender, "&8- &d/districtadmin player <nick> lider <district> &7- &fUstawia lidera dystryktu");
        MessageUtil.sendMessage(sender, "&f                    District");
        MessageUtil.sendMessage(sender, "&8- &d/districtadmin district create &7- &fTworzy nowy dystrykt");
        MessageUtil.sendMessage(sender, "&8- &d/districtadmin district remove <dystrykt> &7- &fUsuwa dystrykt");
        MessageUtil.sendMessage(sender, "&8- &d/districtadmin district edit <dystrykt> color <kolor po angliesku> &7- &fUstawia kolor dystryktu");
        MessageUtil.sendMessage(sender, "&8- &d/districtadmin district edit <dystrykt> prefix <prefix dystryktu, można &> &7- &fUstawia nowy prefix dystryktu");
        MessageUtil.sendMessage(sender, "&5---------- &dBetter district &5----------");
    }
}
