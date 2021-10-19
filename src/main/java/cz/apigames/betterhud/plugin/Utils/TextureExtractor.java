package cz.apigames.betterhud.plugin.Utils;

import cz.apigames.betterhud.BetterHud;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.security.CodeSource;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TextureExtractor {

    public static boolean extract() throws IOException {

        CodeSource codeSource = BetterHud.class.getProtectionDomain().getCodeSource();
        if (codeSource != null) {
            ZipInputStream zipInputStream = new ZipInputStream(codeSource.getLocation().openStream());
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                if (!zipEntry.isDirectory() && zipEntry.getName().startsWith("data/")) {
                    File file = new File((BetterHud.getPlugin().getDataFolder().getParent() + "/ItemsAdder/" + zipEntry.getName()).replace("/", File.separator));
                    if (!file.exists()) {
                        FileUtils.copyInputStreamToFile(Objects.requireNonNull(BetterHud.getPlugin().getResource(zipEntry.getName())), file);
                    }
                }
                zipEntry = zipInputStream.getNextEntry();
            }
            return true;
        }
        return false;
    }

}
