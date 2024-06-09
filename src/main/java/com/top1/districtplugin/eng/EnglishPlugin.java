package com.top1.districtplugin.eng;

import com.top1.districtplugin.utility.LanguagePlugin;
import org.bukkit.plugin.java.JavaPlugin;

public class EnglishPlugin implements LanguagePlugin {

    private final JavaPlugin plugin;

    public EnglishPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEnable() {
        plugin.getLogger().info("English version of the plugin enabled!");
    }

    @Override
    public void onDisable() {
    }
}
