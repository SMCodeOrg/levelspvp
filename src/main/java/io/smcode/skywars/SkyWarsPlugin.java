package io.smcode.skywars;

import io.smcode.skywars.commands.SkyWarsCommand;
import io.smcode.skywars.config.Messages;
import io.smcode.skywars.game.*;
import io.smcode.skywars.listeners.GameListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class SkyWarsPlugin extends JavaPlugin {
    private static SkyWarsPlugin plugin;
    private GameManager manager;
    @Getter
    private Messages messages;

    @Override
    public void onLoad() {
        ConfigurationSerialization.registerClass(GameLocations.class);
        ConfigurationSerialization.registerClass(GameSettings.class);
        ConfigurationSerialization.registerClass(Game.class);

        saveResource("messages.yml", false);

        Bukkit.getPluginManager().disablePlugin(plugin);
    }

    @Override
    public void onEnable() {
        plugin = this;
        this.messages = new Messages(new File(getDataFolder(), "messages.yml"));
        this.manager = new GameManager(this);
        manager.loadGames();

        getCommand("skywars").setExecutor(new SkyWarsCommand(manager, messages));
        getServer().getPluginManager().registerEvents(new GameListener(messages, manager), this);
    }

    @Override
    public void onDisable() {
        if (manager != null) {
            manager.saveGames();
        }
    }

    public static SkyWarsPlugin getInstance() {
        return plugin;
    }
}
