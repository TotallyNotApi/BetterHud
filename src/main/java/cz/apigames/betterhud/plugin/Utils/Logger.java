package cz.apigames.betterhud.plugin.Utils;

import cz.apigames.betterhud.BetterHud;
import cz.apigames.betterhud.api.Utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.*;

public class Logger {

    public static void createNewLogs() {

        try {

            File errorFile = new File(BetterHud.getPlugin().getDataFolder()+"/logs", "errors.txt");
            if(!errorFile.exists()) {
                errorFile.getParentFile().mkdirs();
                errorFile.createNewFile();
            }

            if(BetterHud.isDebugEnabled()) {
                File debugFile = new File(BetterHud.getPlugin().getDataFolder()+"/logs", "debug.txt");
                if(debugFile.exists()) {
                    debugFile.delete();
                }
                debugFile.getParentFile().mkdirs();
                debugFile.createNewFile();
            }

        } catch (IOException e) {
            BetterHud.sendErrorToConsole("&cAn error occurred while creating log files!");
            e.printStackTrace();
        }

    }

    public static void createReportFile() {

        try {

            File dirPath = new File(BetterHud.getPlugin().getDataFolder()+"/logs");
            if(!dirPath.exists())
                dirPath.mkdirs();

            File reportFile = new File(BetterHud.getPlugin().getDataFolder()+"/logs", "report-log.txt");
            if(reportFile.exists()) {
                reportFile.delete();
            }

            if(reportFile.createNewFile()) {

                StringBuilder plugins = new StringBuilder();

                for(Plugin plugin : Bukkit.getPluginManager().getPlugins()) {

                    plugins.append(plugin.getName()+" ["+plugin.getDescription().getVersion()+"], ");

                }

                PrintStream fileStream = new PrintStream(reportFile);

                String host = BetterHud.isIASelfHosted() ? "Self-host" : "External-host";

                fileStream.println("--------------[Information]--------------");
                fileStream.println(" ");
                fileStream.println("Machine:");
                fileStream.println(" OS: "+System.getProperty("os.name"));
                fileStream.println(" OS version: "+System.getProperty("os.version"));
                fileStream.println(" Java version: "+System.getProperty("java.version"));
                fileStream.println(" Java VM Version: "+System.getProperty("java.vm.version"));
                fileStream.println(" ");
                fileStream.println("Other:");
                fileStream.println(" Server version: "+Bukkit.getVersion());
                fileStream.println(" Resource pack host: "+host);
                fileStream.println(" ");
                fileStream.println("Plugins: "+ plugins.toString());

                fileStream.close();
            }

        } catch (IOException e) {
            BetterHud.sendErrorToConsole("&cFailed to create report log!");
            e.printStackTrace();
        }


    }

    public static void writeErrorMessage(String str) {

        str = MessageUtils.getRawMessage(str);

        try {

            File errorFile = new File(BetterHud.getPlugin().getDataFolder()+"/logs", "errors.txt");
            if(!errorFile.exists()) {
                errorFile.getParentFile().mkdirs();
                errorFile.createNewFile();
            }

            PrintStream fileStream = new PrintStream(new FileOutputStream(errorFile, true));

            if(str.contains("\n")) {

                for(String line : str.split("\n")) {

                    fileStream.println(line);

                }

            } else {
                fileStream.println(str);
            }

            fileStream.close();
        } catch (IOException e) {
            BetterHud.sendErrorToConsole("&cCan't write to the error file!");
            e.printStackTrace();
        }

    }

    public static void writeDebugMessage(String newLine) {

        try {

            File debugFile = new File(BetterHud.getPlugin().getDataFolder()+"/logs", "debug.txt");
            if(!debugFile.exists()) {
                debugFile.createNewFile();
            }

            PrintStream fileStream = new PrintStream(new FileOutputStream(debugFile, true));

            fileStream.println(newLine);
            fileStream.close();

        } catch (IOException e) {
            BetterHud.sendErrorToConsole("&cFailed to write to debug file!");
            e.printStackTrace();
        }

    }

    public static void writeException(Exception exception) {

        try {

            File errorFile = new File(BetterHud.getPlugin().getDataFolder()+"/logs", "errors.txt");
            if(!errorFile.exists()) {
                errorFile.getParentFile().mkdirs();
                errorFile.createNewFile();
            }

            PrintStream fileStream = new PrintStream(new FileOutputStream(errorFile, true));

            exception.printStackTrace(fileStream);

            fileStream.close();
        } catch (IOException e) {
            BetterHud.sendErrorToConsole("&cCan't write to the error file!");
            e.printStackTrace();
        }

    }

}
