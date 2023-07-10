package io.github.adainish.cobblemonkitsforge.storage;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import io.github.adainish.cobblemonkitsforge.CobblemonKitsForge;
import io.github.adainish.cobblemonkitsforge.obj.Player;
import io.github.adainish.cobblemonkitsforge.util.Adapters;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerStorage
{
    public static void makePlayer(UUID uuid) {
        File dir = CobblemonKitsForge.getPlayerStorageDir();
        dir.mkdirs();


        Player playerData = new Player(uuid);

        File file = new File(dir, "%uuid%.json".replaceAll("%uuid%", String.valueOf(uuid)));
        if (file.exists()) {
            return;
        }

        Gson gson = Adapters.PRETTY_MAIN_GSON;
        String json = gson.toJson(playerData);

        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void makePlayer(ServerPlayer player) {
        File dir = CobblemonKitsForge.getPlayerStorageDir();
        dir.mkdirs();


        Player playerData = new Player(player.getUUID());

        File file = new File(dir, "%uuid%.json".replaceAll("%uuid%", String.valueOf(player.getUUID())));
        if (file.exists()) {
            return;
        }

        Gson gson = Adapters.PRETTY_MAIN_GSON;
        String json = gson.toJson(playerData);

        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void savePlayer(Player player) {

        File dir = CobblemonKitsForge.getPlayerStorageDir();
        dir.mkdirs();

        File file = new File(dir, "%uuid%.json".replaceAll("%uuid%", String.valueOf(player.uuid)));
        Gson gson = Adapters.PRETTY_MAIN_GSON;
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (reader == null) {
            CobblemonKitsForge.log.error("Something went wrong attempting to read the Player Data");
            return;
        }


        try {
            FileWriter writer = new FileWriter(file);
            writer.write(gson.toJson(player));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.updateCache();
    }

    public static List<UUID> getAllPlayerUUIDS()
    {
        List<UUID> uuids = new ArrayList<>();

        File dir = CobblemonKitsForge.getPlayerStorageDir();
        if (dir != null) {
            for (File f : dir.listFiles()) {
                UUID uuid;
                try {
                    uuid = UUID.fromString(f.getName().replace(".json", ""));
                } catch (IllegalArgumentException e)
                {
                    continue;
                }

                uuids.add(uuid);

            }
        }

        return uuids;
    }

    public static List<Player> getAllPlayers()
    {

        List<UUID> addedPlayers = new ArrayList<>();

        List<Player> playerList = new ArrayList<>();

        for (Player p:CobblemonKitsForge.instance.dataWrapper.playerCache.values()) {
            playerList.add(p);
            addedPlayers.add(p.uuid);
        }

        File dir = CobblemonKitsForge.getPlayerStorageDir();
        if (dir != null) {
            for (File f : dir.listFiles()) {
                UUID uuid;
                try {
                    uuid = UUID.fromString(f.getName().replace(".json", ""));
                } catch (IllegalArgumentException e)
                {
                    continue;
                }
                if (addedPlayers.contains(uuid))
                    continue;
                Player p = getPlayer(uuid);
                if (p == null)
                    continue;
                playerList.add(p);
                addedPlayers.add(uuid);
            }
        }

        return playerList;
    }

    @Nullable
    public static Player getPlayer(UUID uuid) {
        File dir = CobblemonKitsForge.getPlayerStorageDir();
        dir.mkdirs();

        if (CobblemonKitsForge.instance.dataWrapper.playerCache.containsKey(uuid))
            return CobblemonKitsForge.instance.dataWrapper.playerCache.get(uuid);

        File guildFile = new File(dir, "%uuid%.json".replaceAll("%uuid%", String.valueOf(uuid)));
        Gson gson = Adapters.PRETTY_MAIN_GSON;
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(guildFile));
        } catch (FileNotFoundException e) {
            CobblemonKitsForge.log.error("Detected non-existing player, making new player data file");
            return null;
        }

        return gson.fromJson(reader, Player.class);
    }
}
