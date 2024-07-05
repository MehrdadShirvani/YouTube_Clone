package Shared.Utils;

import Shared.Models.Account;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CacheUtil {

    private static final String CACHE_DIR_PATH = "src/main/java/Client/.cache";
    private static final String CACHE_FILE_PATH = CACHE_DIR_PATH + "/account_cache.json";
    private static final String CHECKSUM_FILE_PATH = CACHE_DIR_PATH + "/account_cache_checksum.txt";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static void ensureDirectoryExists() {
        File cacheDir = new File(CACHE_DIR_PATH);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
    }

    public static void cacheAccount(Account account) throws IOException {
        ensureDirectoryExists();
        objectMapper.writeValue(new File(CACHE_FILE_PATH), account);
        String checksum = calculateChecksum(CACHE_FILE_PATH);
        Files.write(Paths.get(CHECKSUM_FILE_PATH), checksum.getBytes());
    }

    public static Account readAccountFromCache() throws IOException {
        return objectMapper.readValue(new File(CACHE_FILE_PATH), Account.class);
    }

    public static boolean isCacheAvailable() {
        File cacheFile = new File(CACHE_FILE_PATH);
        return cacheFile.exists() && cacheFile.length() > 0;
    }

    private static String calculateChecksum(String filePath) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
            byte[] hashBytes = digest.digest(fileBytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    public static boolean isCacheUnchanged() throws IOException {
        if (!Files.exists(Paths.get(CHECKSUM_FILE_PATH))) {
            return false;
        }
        String originalChecksum = new String(Files.readAllBytes(Paths.get(CHECKSUM_FILE_PATH)));
        String currentChecksum = calculateChecksum(CACHE_FILE_PATH);
        return originalChecksum.equals(currentChecksum);
    }
}
