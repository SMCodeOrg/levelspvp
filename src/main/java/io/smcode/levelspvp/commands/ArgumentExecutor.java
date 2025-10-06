package io.smcode.levelspvp.commands;

import org.bukkit.entity.Player;

public interface ArgumentExecutor {
    void execute(Player player, String[] args);

    String getUsage();

    String getDescription();
}
