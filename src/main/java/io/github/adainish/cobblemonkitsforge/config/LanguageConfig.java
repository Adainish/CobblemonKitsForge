package io.github.adainish.cobblemonkitsforge.config;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import io.github.adainish.cobblemonkitsforge.CobblemonKitsForge;
import io.github.adainish.cobblemonkitsforge.util.Adapters;

import java.io.*;

public class LanguageConfig
{

    public String prefix;
    public String splitter;
    public String alreadyClaimed;
    public String kitUnavailable;
    public String kitAvailab;
    public String kitStatus;
    public String claimIn;
    public String noPermission;

    public LanguageConfig()
    {
        this.prefix = "&6[&bKits&6]";
        this.splitter = " &e» ";
        this.alreadyClaimed = "&cYou've already claimed this one time kit!";
        this.claimIn = "&cYou can not currently claim this kit! You may claim this kit in %timer%";
        this.kitAvailab = "&a&lAvailable";
        this.kitUnavailable = "&c&lKit unavailable!, claim in %timer%";
        this.kitStatus = "%kit% %status%";
        this.noPermission = "&cYou're not allowed to claim this kit!";
    }


    public static void writeConfig()
    {
        File dir = CobblemonKitsForge.getConfigDir();
        dir.mkdirs();
        Gson gson  = Adapters.PRETTY_MAIN_GSON;
        LanguageConfig config = new LanguageConfig();
        try {
            File file = new File(dir, "language.json");
            if (file.exists())
                return;
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            String json = gson.toJson(config);
            writer.write(json);
            writer.close();
        } catch (IOException e)
        {
            CobblemonKitsForge.log.warn(e);
        }
    }

    public static LanguageConfig getConfig()
    {
        File dir = CobblemonKitsForge.getConfigDir();
        dir.mkdirs();
        Gson gson  = Adapters.PRETTY_MAIN_GSON;
        File file = new File(dir, "language.json");
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            CobblemonKitsForge.log.error("Something went wrong attempting to read the Config");
            return null;
        }

        return gson.fromJson(reader, LanguageConfig.class);
    }
}
