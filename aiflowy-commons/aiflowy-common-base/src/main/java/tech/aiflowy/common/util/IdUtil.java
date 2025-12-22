package tech.aiflowy.common.util;

import java.util.UUID;

public class IdUtil {

    public static String generateUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "").toLowerCase();
    }
}
