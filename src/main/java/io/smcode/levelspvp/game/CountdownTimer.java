package io.smcode.levelspvp.game;

import io.smcode.levelspvp.events.AsyncCountDownTickEvent;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

@Data
public class CountdownTimer {
    private final JavaPlugin plugin;
    private final int startTime;
    private final Game game;

    private int seconds;
    private BukkitTask task;

    public CountdownTimer(JavaPlugin plugin, int startTime, Game game) {
        this.plugin = plugin;
        this.startTime = startTime;
        this.game = game;
        this.seconds = startTime;
    }

    public void start() {
        if (isRunning())
            return;

        this.seconds = startTime;

        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getServer().getPluginManager().callEvent(new AsyncCountDownTickEvent(getHandle()));

                if (seconds <= 0)
                    cancel();

                seconds--;
            }
        }.runTaskTimerAsynchronously(plugin, 0, 20);
    }

    public boolean isRunning() {
        return this.task != null;
    }

    public CountdownTimer getHandle() {
        return this;
    }
}
