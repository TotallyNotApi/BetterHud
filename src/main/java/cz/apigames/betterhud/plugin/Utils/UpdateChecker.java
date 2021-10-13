package cz.apigames.betterhud.plugin.Utils;

import cz.apigames.betterhud.BetterHud;
import cz.apigames.betterhud.api.Utils.MessageUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdateChecker implements Listener {

    private static String api = "https://api.spigotmc.org/legacy/update.php?resource=";
    private static String id = "84180";

    private static boolean newUpdate;
    private static String latestVersion;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(event.getPlayer().hasPermission("betterhud.update.notify")) {
            if(newUpdate) {
                event.getPlayer().sendMessage(MessageUtils.colorize(" &eBetterHud &8» &7New version available! Your version: &e"+BetterHud.getVersion()+" &7| Latest version: &6"+latestVersion));
                event.getPlayer().sendMessage(MessageUtils.colorize(" &eDownload here &8» &6https://spigotmc.org/resource/84180"));
            }
        }
    }

    public static void checkUpdate() {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(api + id).openConnection();
            connection.setRequestMethod("GET");
            latestVersion = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();

            if(!BetterHud.getVersion().equalsIgnoreCase(latestVersion))
                newUpdate = true;
                return;

        } catch (IOException e) {
            BetterHud.error("Failed to check for the latest version!", e);
        }
        newUpdate = false;
    }

}
