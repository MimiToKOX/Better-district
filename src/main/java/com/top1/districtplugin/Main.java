package com.top1.districtplugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.top1.districtplugin.eng.EnglishPlugin;
import com.top1.districtplugin.pl.PolishPlugin;

public class Main extends JavaPlugin {

    private LanguagePlugin delegate;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        String language = config.getString("language", "eng").toLowerCase();
        getLogger().info("Language setting: " + language);

        try {
            if ("pl".equals(language)) {
                getLogger().info("Loading Polish version of the plugin.");
                delegate = new PolishPlugin(this);
            } else {
                getLogger().info("Loading English version of the plugin.");
                delegate = new EnglishPlugin(this);
            }
            delegate.onEnable();
        } catch (Exception e) {
            getLogger().severe("Failed to load the plugin based on the language setting.");
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        if (delegate != null) {
            delegate.onDisable();
        }
    }
}
