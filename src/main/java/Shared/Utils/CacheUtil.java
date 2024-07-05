package Shared.Utils;

import Shared.Models.Account;

import com.fasterxml.jackson.databind.ObjectMapper;


public class CacheUtil {

    private static final String CACHE_FILE_PATH = "account_cache.json";
    private static final String CHECKSUM_FILE_PATH = "account_cache_checksum.txt";
    private static final ObjectMapper objectMapper = new ObjectMapper();

}
