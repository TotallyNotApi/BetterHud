package cz.apigames.betterhud.plugin_old.Utils;

import cz.apigames.betterhud.BetterHud;
import cz.apigames.betterhud.plugin_old.Configs.ConfigManager;
import cz.apigames.betterhud.plugin_old.Hud.Hud;
import cz.apigames.betterhud.plugin_old.Hud.HudPart;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileUtils {

    public static void exportTextures(String targetDirName) {

        BetterHud.debug("Exporting textures");

        final File jarFile = new File(BetterHud.class.getProtectionDomain().getCodeSource().getLocation().getPath());

        //OTEVRE TO JAKO ZIP INPUT STREAM

        Path targetDir = Paths.get("plugins/"+targetDirName);

        if(!Files.exists(targetDir)) {
            BetterHud.sendErrorToConsole("&cCan't find target directory: &4"+targetDir.toAbsolutePath());
        }

        try {
            final JarFile jar = new JarFile(jarFile);
            final Enumeration<JarEntry> entries = jar.entries();
            BetterHud.debug("Opening jar file");
            while(entries.hasMoreElements()) {

                final String path = entries.nextElement().getName();

                if (path.startsWith("data" + "/")) {

                    if(path.endsWith(".png") || path.endsWith(".jpg")) {

                        Path targetFile = Paths.get(targetDir.toString(), path);
                        targetFile.toFile().getParentFile().mkdirs();
                        Files.deleteIfExists(targetFile);

                        Files.createFile(targetFile);

                        InputStream inputStream = BetterHud.getPlugin().getResource(path);
                        byte[] buffer = new byte[inputStream.available()];
                        inputStream.read(buffer);

                        OutputStream outStream = new FileOutputStream(targetFile.toFile());
                        outStream.write(buffer);
                        inputStream.close();
                        outStream.close();
                    }

                }

            }
            jar.close();
            BetterHud.debug("Closing jar file");

        } catch (IOException | UnsupportedOperationException e) {
            BetterHud.error("&cFailed to export textures! See 'logs/errors.txt' for more info", e);
            BetterHud.sendMessageToConsole("&cYou can import the textures manually, download the textures here: &ehttps://www.mediafire.com/file/ni6oe31q57xkhr8/textures.zip/file");
        }
    }

    public static void zipTextures() {

        BetterHud.debug("Zipping textures");

        try {

            Path sourceFile = Paths.get("plugins/BetterHud/data/resource_pack/assets");
            Path destinationFile = Paths.get("plugins/BetterHud/textures.zip");

            if(Files.notExists(sourceFile)) {
                BetterHud.sendErrorToConsole("&cCan't find exported textures in plugin's folder!");
            }

            Files.deleteIfExists(destinationFile);

            BetterHud.debug("Creating destination file");
            Files.createFile(destinationFile);

            BetterHud.debug("Running zip method");
            zip(sourceFile.toFile(), destinationFile.toFile());

            BetterHud.debug("Deleting temp directory");
            deleteDirectory(new File(BetterHud.getPlugin().getDataFolder(), "data"));

            BetterHud.debug("Zipping textures done");
        } catch (Exception e) {
            BetterHud.error("&cAn Error occurred while creating archive!", e);
        }

    }

    public static void zipPluginFolder() {

        BetterHud.debug("Zipping plugin folder");

        try {
            Path sourceFile = Paths.get("plugins/BetterHud");
            Path destinationFile = Paths.get("plugins/BetterHud/report.zip");

            if(Files.notExists(sourceFile)) {
                BetterHud.sendErrorToConsole("&cCan't find plugin folder!");
            }

            Files.deleteIfExists(destinationFile);

            BetterHud.debug("Creating destination file");
            Files.createFile(destinationFile);

            BetterHud.debug("Running zip method");
            zip(sourceFile.toFile(), destinationFile.toFile());

            BetterHud.debug("Zipping plugin done");
        } catch (Exception e) {
            BetterHud.error("&cFailed to create archive! Please, check &4errors.txt &cfor more information.", e);
        }

    }

    public static void generateIPFile() {

        BetterHud.debug("Generating items_packs file");
        Path dir = Paths.get("plugins/ItemsAdder/data/items_packs/betterhud");
        Path ipFile = Paths.get("plugins/ItemsAdder/data/items_packs/betterhud/hud_parts.yml");

        try {

            Files.deleteIfExists(ipFile);

            if(Files.notExists(dir)) {
                dir.toFile().mkdirs();
            }

            Files.createFile(ipFile);

            BetterHud.debug("Creating new file");

        } catch (IOException e) {
            BetterHud.error("&cFailed to generate items_packs file! See 'logs/errors.txt' for more info.", e);
            return;
        }


        BetterHud.debug("Loading YamlConfiguration");
        YamlConfiguration yamlFile = YamlConfiguration.loadConfiguration(ipFile.toFile());

        yamlFile.set("info.namespace", "betterhud");
        BetterHud.debug("Namespace set");

        BetterHud.debug("Loop through the huds");
        for(Hud hud : Hud.getHuds()) {

            String prefix = hud.getName();
            for(HudPart hudPart : hud.parts) {

                HudPart.Type type = hudPart.getType();
                if(type.equals(HudPart.Type.ICON)) {
                    String path = prefix+"-"+hudPart.getPartName();
                    yamlFile.set("font_images."+path+".path", "font"+"/"+"icons"+"/"+hudPart.getValue());
                    yamlFile.set("font_images."+path+".y_position", hudPart.getPositionY());
                    yamlFile.set("font_images."+path+".scale_ratio", hudPart.getScale());
                } else if(type.equals(HudPart.Type.TEXT)) {

                    //NORMAL
                    for(Character ch : BetterHud.alphabet) {
                        String path = prefix+"-"+hudPart.getPartName()+"-"+ch;

                        yamlFile.set("font_images."+path+".path", "font"+"/"+"chars"+"/"+ch+".png");
                        yamlFile.set("font_images."+path+".y_position", hudPart.getPositionY());
                        yamlFile.set("font_images."+path+".scale_ratio", hudPart.getScale());

                    }

                    //UPPERCASE
                    for(Character ch : BetterHud.alphabet) {
                        String path = prefix+"-"+hudPart.getPartName()+"-"+ch+"-big";

                        yamlFile.set("font_images."+path+".path", "font"+"/"+"chars"+"/"+ch+"-big.png");
                        yamlFile.set("font_images."+path+".y_position", hudPart.getPositionY());
                        yamlFile.set("font_images."+path+".scale_ratio", hudPart.getScale());

                    }

                    //SPECIAL
                    for(Character ch : BetterHud.special_chars) {

                        String path;
                        if(ch.toString().equalsIgnoreCase(".")) {
                            path = prefix+"-"+hudPart.getPartName()+"-"+"dot";
                        } else {
                            path = prefix+"-"+hudPart.getPartName()+"-"+ch;
                        }
                        String textureName = ch.toString();

                        if("?/\\:;<>*.=".contains(ch.toString())) {
                            textureName = textureName.replace("?", "question_mark");
                            textureName = textureName.replace("/", "slash");
                            textureName = textureName.replace("\\", "back_slash");
                            textureName = textureName.replace(";", "semicolon");
                            textureName = textureName.replace(":", "colon");
                            textureName = textureName.replace("<", "less-than");
                            textureName = textureName.replace(">", "greater-than");
                            textureName = textureName.replace("*", "asterisk");
                            textureName = textureName.replace(".", "dot");
                            textureName = textureName.replace("=", "equals");
                        }

                        yamlFile.set("font_images."+path+".path", "font"+"/"+"chars"+"/"+textureName+".png");
                        yamlFile.set("font_images."+path+".y_position", hudPart.getPositionY());
                        yamlFile.set("font_images."+path+".scale_ratio", hudPart.getScale());

                    }

                    //NUMBERS
                    for(Character ch : BetterHud.numbers) {
                        String path = prefix+"-"+hudPart.getPartName()+"-"+ch;

                        yamlFile.set("font_images."+path+".path", "font"+"/"+"numbers"+"/"+ch+".png");
                        yamlFile.set("font_images."+path+".y_position", hudPart.getPositionY());
                        yamlFile.set("font_images."+path+".scale_ratio", hudPart.getScale());

                    }

                    //BLANK
                    String path = prefix+"-"+hudPart.getPartName()+"-"+"blank";
                    yamlFile.set("font_images."+path+".path", "font"+"/"+"chars"+"/"+"blank.png");
                    yamlFile.set("font_images."+path+".y_position", -5000);
                    yamlFile.set("font_images."+path+".scale_ratio", hudPart.getScale());

                } else if(type.equals(HudPart.Type.INTEGER)) {

                    for(Character ch : BetterHud. numbers) {
                        String path = prefix+"-"+hudPart.getPartName()+"-"+ch;

                        yamlFile.set("font_images."+path+".path", "font"+"/"+"numbers"+"/"+ch+".png");
                        yamlFile.set("font_images."+path+".y_position", hudPart.getPositionY());
                        yamlFile.set("font_images."+path+".scale_ratio", hudPart.getScale());

                    }

                    for(Character ch : BetterHud.number_chars) {

                        String path;
                        if(ch.toString().equalsIgnoreCase(".")) {
                            path = prefix+"-"+hudPart.getPartName()+"-"+"dot";
                        } else {
                            path = prefix+"-"+hudPart.getPartName()+"-"+ch;
                        }

                        String textureName = ch.toString();

                        if(textureName.equalsIgnoreCase(".")) {
                            textureName = textureName.replace(".", "dot");
                        }

                        yamlFile.set("font_images."+path+".path", "font"+"/"+"chars"+"/"+textureName+".png");
                        yamlFile.set("font_images."+path+".y_position", hudPart.getPositionY());
                        yamlFile.set("font_images."+path+".scale_ratio", hudPart.getScale());

                    }

                    //BLANK
                    String path = prefix+"-"+hudPart.getPartName()+"-"+"blank";
                    yamlFile.set("font_images."+path+".path", "font"+"/"+"chars"+"/"+"blank.png");
                    yamlFile.set("font_images."+path+".y_position", -5000);
                    yamlFile.set("font_images."+path+".scale_ratio", hudPart.getScale());

                }

            }


        }

        BetterHud.debug("Loop end");

        BetterHud.debug("Saving file");
        try {
            yamlFile.save(ipFile.toFile());
        } catch (IOException e) {
            BetterHud.error("&cAn Error occurred while saving items_packs file!", e);
        }

    }

    public static void updateTextures() {

        //ASYNC
        Bukkit.getScheduler().runTaskAsynchronously(BetterHud.getPlugin(), () -> {

            String ver = ConfigManager.getConfig("config.yml").getString("texture-version");
            if(!BetterHud.TEXTURE_LATEST_VERSION.equals(ver)) {

                BetterHud.debug("Texture update found");

                BetterHud.ConsoleError = false;

                BetterHud.sendMessageToConsole("&aUpdating texture files..");

                if(BetterHud.isIASelfHosted()) {
                    BetterHud.sendMessageToConsole("&aExporting new textures..");
                    BetterHud.debug("Exporting textures");
                    FileUtils.exportTextures("ItemsAdder");
                } else {
                    BetterHud.debug("exporting new textures");
                    BetterHud.sendMessageToConsole("&aExporting new textures..");
                    FileUtils.exportTextures("BetterHud");
                    BetterHud.debug("creating new archive");
                    BetterHud.sendMessageToConsole("&aCreating a new archive..");
                    FileUtils.zipTextures();
                }
                FileUtils.generateIPFile();

                if(BetterHud.ConsoleError) {
                    BetterHud.debug("failed to update textures");
                    BetterHud.sendMessageToConsole("&cFailed to update texture files! Please, try /bh exportTextures");
                } else {
                    BetterHud.debug("textures updated successfully");
                    ConfigManager.set("config.yml", "texture-version", BetterHud.TEXTURE_LATEST_VERSION);
                    BetterHud.sendMessageToConsole("&aSuccessfully updated all texture files!");
                }

            }

        });

    }

    public static void vanillaHud(boolean bool) {

        Bukkit.getScheduler().runTaskAsynchronously(BetterHud.getPlugin(), () -> {

            if(bool) {

                BetterHud.debug("vanillahud: true");

                Path iconFile = Paths.get("plugins/ItemsAdder/data/resource_pack/assets/minecraft/textures/gui/icons.png");

                if(Files.exists(iconFile)) {
                    BetterHud.debug("deleting transparent hud");
                    try {
                        Files.delete(iconFile);
                    } catch (IOException e) {
                        BetterHud.error("&cFailed to delete texture &4icons.png&c! See 'logs/errors.txt' for more info.", e);
                    }
                }
            } else {

                BetterHud.debug("vanillahud: false");

                Path destFile = Paths.get("plugins/ItemsAdder/data/resource_pack/assets/minecraft/textures/gui/icons.png");

                if(Files.exists(destFile)) {
                    BetterHud.debug("transparent hud exists, end");
                    return;
                }

                BetterHud.debug("transparent hud not found, exporting textures");
                exportTextures("BetterHud");
                Path tempFile = Paths.get("plugins/BetterHud/data/resource_pack/assets/minecraft/textures/gui/icons.png");

                if(!Files.exists(tempFile)) {
                    BetterHud.sendErrorToConsole("&cFailed to move &4icons.png &ctexture! Temp file doesn't exists!");
                    return;
                }

                try {

                    BetterHud.debug("moving temp file to ItemsAdder dir");
                    Files.move(tempFile, destFile);

                } catch (IOException exception) {
                    BetterHud.error("&cFailed to move &4icons.png &ctexture!", exception);
                }

                BetterHud.debug("deleting temp dir");
                deleteDirectory(new File(BetterHud.getPlugin().getDataFolder(), "data"));
            }

        });

    }

    private static void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }

    private static void zip(File sourceFile, File destinationFile) throws IOException {
        FileOutputStream fos = new FileOutputStream(destinationFile);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(sourceFile.getPath());

        zipFile(fileToZip, fileToZip.getName(), zipOut, destinationFile);
        zipOut.close();
        fos.close();
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut, File zipFile) throws IOException {

        if(fileToZip.equals(zipFile)) {
            return;
        }

        if (fileToZip.isHidden()) {
            return;
        }

        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/") || fileName.endsWith("\\")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
            }
            zipOut.closeEntry();
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut, zipFile);
            }
            return;
        }

        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;

        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }

        fis.close();
    }

    private static void unzipFile(Path zip, Path destDir) throws IOException {

        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zip.toString()));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = getNewFile(destDir.toFile(), zipEntry);
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {

                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }

                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();

    }

    private static File getNewFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

}
