package utils.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

/*Why final class, private constructor and static methods: it’s a utility class. You call
ConfigFileReader.get("db.url") and never create one with new.*/
public final class ConfigFileReader {

    private ConfigFileReader(){

    }
    private static final Properties PROPS = new Properties();

    /* static { ... } is a static initializer. It runs once, the first time anything touches
        ConfigFileReader. So the file is read once per test run, not once per call. */
    static {
        load("config.properties");
//        load();
        String env = System.getProperty("env");
        if (env != null && !env.isBlank()) {
            load("config-" + env.trim() + ".properties");
        }
    }

    private static void load(String fileName) {
        try (InputStream input = ConfigFileReader.class.getClassLoader().getResourceAsStream(fileName)) {
//        try (InputStream input = ConfigFileReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IllegalStateException("Configuration file not found on classpath: " + fileName);
            }
            PROPS.load(input);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read " + fileName, e);
        }
    }

    public static String get(String key) {
        String value = System.getProperty(key);// 1st: -Dkey=value

        if (value == null) {
            value = System.getenv(key.toUpperCase().replace('.', '_')); // 2nd: env var DB_URL
        }
        if (value == null) {
            value = PROPS.getProperty(key);// 3rd: the file
        }
        if (value == null) {
            throw new IllegalStateException("Missing Configuration key: " + key);
        }
        return value.trim();
        /*This is the most important method. It checks three places in priority order:
        1.-D system property, for example mvn test -Ddb.password=secret.
        2.Environment variable. db.password becomes DB_PASSWORD.
        3.The file, as the default.*/
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }
}
