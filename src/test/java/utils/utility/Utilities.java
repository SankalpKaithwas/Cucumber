package utils.utility;

import java.util.UUID;

public class Utilities {

    public static String generateRandomName(String name) {
        return name + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
}
