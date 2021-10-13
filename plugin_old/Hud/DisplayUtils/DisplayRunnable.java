package cz.apigames.betterhud.plugin_old.Hud.DisplayUtils;

import cz.apigames.betterhud.BetterHud;
import cz.apigames.betterhud.plugin_old.Hud.DisplayUtils.Displays.ActionBar;
import cz.apigames.betterhud.plugin_old.Hud.DisplayUtils.Displays.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class DisplayRunnable implements Runnable {

    private static int taskID;

    public static void initialize(int period) {

        period = period == 0 ? 1 : period;

        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(BetterHud.getPlugin(), new DisplayRunnable(), 0, period*20);
        taskID = task.getTaskId();
    }

    public static void cancelTask() {
        Bukkit.getScheduler().cancelTask(taskID);
    }

    @Override
    public void run() {

        for(ActionBar actionBar : ActionBar.getAll()) {
            actionBar.update();
        }

        for(BossBar bossBar : BossBar.getAll()) {
            bossBar.update();
        }

    }
}
