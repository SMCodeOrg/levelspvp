package io.smcode.levelspvp;

import io.smcode.levelspvp.commands.LevelsCommand;
import io.smcode.levelspvp.config.Messages;
import io.smcode.levelspvp.game.*;
import io.smcode.levelspvp.listeners.GameListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class LevelsPlugin extends JavaPlugin {
    private static LevelsPlugin plugin;
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

        getCommand("levelspvp").setExecutor(new LevelsCommand(manager, messages));
        getServer().getPluginManager().registerEvents(new GameListener(messages, manager), this);
    }

    @Override
    public void onDisable() {
        if (manager != null) {
            manager.saveGames();
        }
    }

    public static LevelsPlugin getInstance() {
        return plugin;
    }
}
