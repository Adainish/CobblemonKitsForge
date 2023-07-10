package io.github.adainish.cobblemonkitsforge.subscriptions;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import io.github.adainish.cobblemonkitsforge.obj.Player;
import io.github.adainish.cobblemonkitsforge.storage.PlayerStorage;
import kotlin.Unit;

public class Subscriptions
{

    public Subscriptions()
    {
        loadPlayerSubscriptions();
    }
    public void loadPlayerSubscriptions()
    {
        CobblemonEvents.PLAYER_JOIN.subscribe(Priority.NORMAL, serverPlayer -> {

            Player player = PlayerStorage.getPlayer(serverPlayer.getUUID());
            if (player == null) {
                PlayerStorage.makePlayer(serverPlayer.getUUID());
                player = PlayerStorage.getPlayer(serverPlayer.getUUID());

            }

            if (player != null) {
                player.setUsername(serverPlayer.getName().getString());
                player.updateCache();
            }

            return Unit.INSTANCE;
        });

        CobblemonEvents.PLAYER_QUIT.subscribe(Priority.NORMAL, serverPlayer -> {

            if (serverPlayer != null) {
                Player player = PlayerStorage.getPlayer(serverPlayer.getUUID());
                if (player != null) {
                    player.save();
                }
            }
            return Unit.INSTANCE;
        });
    }
}
