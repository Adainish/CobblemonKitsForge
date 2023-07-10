package io.github.adainish.cobblemonkitsforge.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.adainish.cobblemonkitsforge.CobblemonKitsForge;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Util
{
    public static final long HOUR_IN_MILLIS = 3600000;
    public static final long MINUTE_IN_MILLIS = 60000;

    public static String formattedString(String s) {
        return s.replaceAll("&", "§");
    }

    public static List<String> formattedArrayList(List<String> list) {

        List<String> formattedList = new ArrayList<>();
        for (String s : list) {
            formattedList.add(formattedString(s));
        }

        return formattedList;
    }

    public static ServerPlayer getPlayer(UUID uuid) {
        return CobblemonKitsForge.getServer().getPlayerList().getPlayer(uuid);
    }

    public static void send(UUID uuid, String message) {
        if (message == null)
            return;
        if (message.isEmpty())
            return;
        ServerPlayer player = getPlayer(uuid);
        if (player != null)
            player.sendSystemMessage(Component.literal(((TextUtil.getMessagePrefix()).getString() + message).replaceAll("&([0-9a-fk-or])", "\u00a7$1")));
    }

    public static void send(CommandSourceStack sender, String message) {
        sender.sendSystemMessage(Component.literal(((TextUtil.getMessagePrefix()).getString() + message).replaceAll("&([0-9a-fk-or])", "\u00a7$1")));
    }

    public static void runCommand(String cmd)
    {
        try {
            CobblemonKitsForge.getServer().getCommands().getDispatcher().execute(cmd, CobblemonKitsForge.getServer().createCommandSourceStack());
        } catch (CommandSyntaxException e) {
            CobblemonKitsForge.log.error(e);
        }
    }
}
