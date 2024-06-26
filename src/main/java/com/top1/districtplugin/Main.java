package com.top1.districtplugin;

import com.top1.districtplugin.pl.commands.DistrictAdminCommand;
import com.top1.districtplugin.pl.commands.DistrictCommand;
import com.top1.districtplugin.pl.tabcompleter.DistrictAdminTabCompleter;
import com.top1.districtplugin.pl.tabcompleter.DistrictTabCompleter;
import com.top1.districtplugin.utility.LanguagePlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.top1.districtplugin.eng.EnglishPlugin;
import com.top1.districtplugin.pl.PolishPlugin;

import java.io.File;
import java.io.IOException;

public class Main extends JavaPlugin {

    private LanguagePlugin delegate;
    private File playerFile;
    private FileConfiguration playerConfig;

    @Override
    public void onEnable() {

        // Load configuration
        saveDefaultConfig();
        loadPlayerConfig();
        FileConfiguration config = getConfig();
        String language = config.getString("language", "eng").toLowerCase();

        // Language messenger
        getLogger().info("Language setting: " + language);

        try {
            // Load PL Language
            if ("pl".equals(language)) {
                getLogger().info("Loading Polish version of the plugin.");
                delegate = new PolishPlugin(this);

                // Load PL Command
                getCommand("districtadmin").setExecutor(new DistrictAdminCommand(this));
                getCommand("districtadmin").setTabCompleter(new DistrictAdminTabCompleter(Bukkit.getScoreboardManager()));
                getCommand("district").setExecutor(new DistrictCommand(this));
                getCommand("district").setTabCompleter(new DistrictTabCompleter(Bukkit.getScoreboardManager()));
            } else {
                // Load ENG Language
                getLogger().info("Loading English version of the plugin.");
                delegate = new EnglishPlugin(this);

                // Load ENG Command
                getCommand("districtadmin").setExecutor(new com.top1.districtplugin.eng.commands.DistrictAdminCommand(this));
                getCommand("districtadmin").setTabCompleter(new com.top1.districtplugin.eng.tabcompleter.DistrictAdminTabCompleter(Bukkit.getScoreboardManager()));
                getCommand("district").setExecutor(new com.top1.districtplugin.eng.commands.DistrictCommand(this));
                getCommand("district").setTabCompleter(new com.top1.districtplugin.eng.tabcompleter.DistrictTabCompleter(Bukkit.getScoreboardManager()));

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
        savePlayerConfig();
    }

    public FileConfiguration getPlayerConfig() {
        return this.playerConfig;
    }

    public void savePlayerConfig() {
        try {
            this.playerConfig.save(this.playerFile);
        } catch (IOException e) {
            getLogger().severe("Could not save player.yml!");
            e.printStackTrace();
        }
    }


    private void loadPlayerConfig() {
        playerFile = new File(getDataFolder(), "player.yml");
        if (!playerFile.exists()) {
            playerFile.getParentFile().mkdirs();
            saveResource("player.yml", false);
        }
        playerConfig = YamlConfiguration.loadConfiguration(playerFile);
    }


}
