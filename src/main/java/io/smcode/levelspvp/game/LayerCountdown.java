package io.smcode.levelspvp.game;

import io.smcode.levelspvp.config.Message;
import io.smcode.levelspvp.config.Messages;
import lombok.Data;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.WorldBorder;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Data
public class LayerCountdown {
    private final JavaPlugin plugin;
    private final int startTime;
    private final Game game;
    private final Messages messages;

    private Consumer<Game> onFinish;

    private int secondsLeft;
    private BukkitTask task;

    public LayerCountdown(JavaPlugin plugin, int startTime, Game game, Messages messages) {
        this.plugin = plugin;
        this.startTime = startTime;
        this.game = game;
        this.secondsLeft = startTime;
        this.messages = messages;
    }

    public void onFinish(Consumer<Game> onFinish) {
        this.onFinish = onFinish;
    }

    public void start() {
        if (isRunning())
            return;

        this.secondsLeft = startTime;

        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                final int minutes = secondsLeft / 60;
                final int seconds = secondsLeft % 60;

                if (secondsLeft == 60) {
                    game.getPlayers().stream()
                            .map(GamePlayer::getPlayer)
                            .forEach(player -> {
                                final WorldBorder border;

                                if ((border = player.getWorldBorder()) == null)
                                    return;

                                border.setSize(2, TimeUnit.MINUTES.toSeconds(1));
                            });
                }

                if ((secondsLeft % 30 == 0 || secondsLeft <= 5) && secondsLeft > 0) {
                    game.broadcast(messages.getMessage(Message.NEXT_LAYER_OPEN, Placeholder.unparsed("seconds", String.valueOf(secondsLeft))));
                }

                game.getPlayers().stream()
                        .map(GamePlayer::getPlayer)
                        .forEach(player -> player.sendActionBar(Component.text((minutes != 0 ? minutes + "m " : "") + seconds + "s", NamedTextColor.GREEN)));

                if (secondsLeft <= 0) {
                    if (onFinish != null)
                        onFinish.accept(game);

                    cancel();
                }

                secondsLeft--;
            }
        }.runTaskTimerAsynchronously(plugin, 0, 20);
    }

    public boolean isRunning() {
        return this.task != null;
    }

    public LayerCountdown getHandle() {
        return this;
    }
}
