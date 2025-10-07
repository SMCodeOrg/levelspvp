package io.smcode.levelspvp.commands.arguments;

import io.smcode.levelspvp.LevelsPlugin;
import io.smcode.levelspvp.commands.ArgumentExecutor;
import io.smcode.levelspvp.config.Message;
import io.smcode.levelspvp.config.Messages;
import io.smcode.levelspvp.game.GameManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class ReloadArgument implements ArgumentExecutor {
    private final GameManager manager;
    private final Messages messages;

    @Override
    public void execute(Player player, String[] args) {
        LevelsPlugin.getInstance().reloadConfig();
        manager.loadGames();
        player.sendMessage(messages.getMessage(Message.CONFIG_RELOAD));
    }

    @Override
    public String getUsage() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reload games and messages config.";
    }
}
