package com.top1.districtplugin.pl;

import com.top1.districtplugin.LanguagePlugin;
import com.top1.districtplugin.pl.commands.DistrictAdminCommand;
import com.top1.districtplugin.pl.tabcompleter.DistrictAdminTabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public class PolishPlugin implements LanguagePlugin {

    private final JavaPlugin plugin;

    public PolishPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEnable() {
        plugin.getLogger().info("Polish version of the plugin enabled!");

    }

    @Override
    public void onDisable() {
    }
}
