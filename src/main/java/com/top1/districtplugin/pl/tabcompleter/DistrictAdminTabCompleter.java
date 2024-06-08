package com.top1.districtplugin.pl.tabcompleter;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DistrictAdminTabCompleter implements TabCompleter {

    private final ScoreboardManager scoreboardManager;

    public DistrictAdminTabCompleter(ScoreboardManager scoreboardManager) {
        this.scoreboardManager = scoreboardManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions.addAll(Arrays.asList("player", "district", "help"));
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("player")) {
                Bukkit.getOnlinePlayers().forEach(player -> suggestions.add(player.getName()));
            } else if (args[0].equalsIgnoreCase("district")) {
                suggestions.addAll(Arrays.asList("create", "remove", "edit"));
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("player")) {
                suggestions.addAll(Arrays.asList("add", "remove"));
            } else if (args[0].equalsIgnoreCase("district")) {
                scoreboardManager.getMainScoreboard().getTeams().forEach(team -> suggestions.add(team.getName()));
            }
        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("district") && args[1].equalsIgnoreCase("edit")) {
                suggestions.addAll(Arrays.asList("prefix", "color"));
            }
        }

        return suggestions.stream().filter(s -> s.startsWith(args[args.length - 1])).collect(Collectors.toList());
    }
}
