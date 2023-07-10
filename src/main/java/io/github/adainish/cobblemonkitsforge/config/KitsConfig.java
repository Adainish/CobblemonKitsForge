package io.github.adainish.cobblemonkitsforge.config;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import io.github.adainish.cobblemonkitsforge.CobblemonKitsForge;
import io.github.adainish.cobblemonkitsforge.manager.KitManager;
import io.github.adainish.cobblemonkitsforge.util.Adapters;

import java.io.*;

public class KitsConfig
{
    public KitManager kitManager;
    public boolean guiEnabled;

    public KitsConfig()
    {
        this.kitManager = new KitManager();
        this.guiEnabled = true;
    }

    public static void saveConfig(KitsConfig config) {
        CobblemonKitsForge.log.warn("Saving data...");
        File dir = CobblemonKitsForge.getConfigDir();
        dir.mkdirs();
        File file = new File(dir, "kits.json");
        Gson gson = Adapters.PRETTY_MAIN_GSON;
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (reader == null) {
            CobblemonKitsForge.log.error("Something went wrong attempting to save");
            return;
        }


        try {
            FileWriter writer = new FileWriter(file);
            writer.write(gson.toJson(config));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        CobblemonKitsForge.log.warn("Data saved successfully!");

    }

    public static void writeConfig()
    {
        File dir = CobblemonKitsForge.getConfigDir();
        dir.mkdirs();
        Gson gson  = Adapters.PRETTY_MAIN_GSON;
        KitsConfig config = new KitsConfig();
        try {
            File file = new File(dir, "kits.json");
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

    public static KitsConfig getConfig()
    {
        File dir = CobblemonKitsForge.getConfigDir();
        dir.mkdirs();
        Gson gson  = Adapters.PRETTY_MAIN_GSON;
        File file = new File(dir, "kits.json");
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            CobblemonKitsForge.log.error("Something went wrong attempting to read the Config");
            return null;
        }

        return gson.fromJson(reader, KitsConfig.class);
    }
}
