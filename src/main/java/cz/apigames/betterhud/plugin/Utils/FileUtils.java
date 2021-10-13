package cz.apigames.betterhud.plugin.Utils;

import cz.apigames.betterhud.BetterHud;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

public class FileUtils {

    public static String checksum(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            FileInputStream fis = new FileInputStream(file);
            byte[] byteArray = new byte[1024];
            int bytesCount = 0;
            while ((bytesCount = fis.read(byteArray)) != -1)
                digest.update(byteArray, 0, bytesCount);
            fis.close();
            byte[] bytes = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes)
                sb.append(
                        Integer.toString((aByte & 0xFF) + 256, 16)
                                .substring(1));
            return sb.toString();
        } catch (IOException |java.security.NoSuchAlgorithmException e) {
            BetterHud.error("Failed to get File checksum! Path: "+file.getAbsolutePath(), e);
            return "";
        }
    }

}
