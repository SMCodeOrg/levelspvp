package io.smcode.levelspvp;

import io.smcode.levelspvp.commands.LevelsCommand;
import io.smcode.levelspvp.config.Messages;
import io.smcode.levelspvp.game.*;
import io.smcode.levelspvp.listeners.GameListener;
import io.smcode.levelspvp.setup.PointsManager;
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
    @Getter
    private PointsManager pointsManager;

    @Override
    public void onLoad() {
        ConfigurationSerialization.registerClass(GameLocations.class, "GameLocations");
        ConfigurationSerialization.registerClass(GameSettings.class, "GameSettings");
        ConfigurationSerialization.registerClass(Game.class, "Game");

        saveResource("messages.yml", false);
    }

    @Override
    public void onEnable() {
        plugin = this;
        this.messages = new Messages(new File(getDataFolder(), "messages.yml"));
        this.manager = new GameManager(this);
        this.pointsManager = new PointsManager(messages);
        manager.loadGames();

        getCommand("levelspvp").setExecutor(new LevelsCommand(manager, messages));
        getServer().getPluginManager().registerEvents(new GameListener(messages, manager), this);
        getServer().getPluginManager().registerEvents(getPointsManager(), this);
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
