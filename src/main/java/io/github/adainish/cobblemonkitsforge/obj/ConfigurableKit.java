package io.github.adainish.cobblemonkitsforge.obj;

import io.github.adainish.cobblemonkitsforge.CobblemonKitsForge;
import io.github.adainish.cobblemonkitsforge.util.Util;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigurableKit {
    public String identifier;
    public int displayOrder;
    public String displayName;
    public ItemStack displayItem;
    public ItemStack unavailableItem;
    public List<String> description;
    public KitProperties properties;
    public List<CommandReward> commands;
    public List<ItemReward> items;

    public ConfigurableKit(String id) {
        this.identifier = id;
        this.displayName = "&b" + id;
        this.displayItem = new ItemStack(Items.PAPER);
        this.unavailableItem = new ItemStack(Items.BARRIER);
        this.description = new ArrayList<>(Arrays.asList("Line 1 of Description", "Line 2 of &cDescription"));
        this.properties = new KitProperties(500, false, "cobblemonkits.kit." + id);
        this.commands = new ArrayList<>(Arrays.asList(new CommandReward("say %pl% received a kit")));
        this.items = new ArrayList<>(Arrays.asList(new ItemReward("&6Dirt", new ItemStack(Items.DIRT))));
    }

    public void forceClaim(Player player) {
        ServerPlayer serverPlayer = player.serverPlayer();
        if (serverPlayer != null) {
            this.commands.forEach(commandReward -> {
                Util.runCommand(commandReward.command.replace("%pl%", serverPlayer.getName().getString()));
            });
            this.items.forEach(itemReward -> {
                serverPlayer.getInventory().add(itemReward.stack.copy());
            });

        } else {
            //error
            CobblemonKitsForge.log.error("A players data could not be verified while handing out kit rewards, this is bad and likely an internal error as this message should not be reachable! Please contact the developer on Discord @adenydd!");
        }
    }


    public void claim(Player player) {
        ServerPlayer serverPlayer = player.serverPlayer();
        if (serverPlayer != null) {
            if (!properties.permission.isBlank() || !properties.permission.isEmpty()) {
                if (!player.hasPermission(this)) {
                    player.sendMessage(CobblemonKitsForge.languageConfig.noPermission);
                    return;
                }
            }
            if (this.properties.oneTime && player.hasOneTimeClaimed(this))
            {
                player.sendMessage(CobblemonKitsForge.languageConfig.alreadyClaimed);
                return;
            }
            if (player.canClaim(this)) {
                this.commands.forEach(commandReward -> {
                    Util.runCommand(commandReward.command.replace("%pl%", serverPlayer.getName().getString()));
                });
                this.items.forEach(itemReward -> {
                    serverPlayer.getInventory().add(itemReward.stack.copy());
                });
                player.lastUsedKitData.put(this.identifier, System.currentTimeMillis());
                if (this.properties.oneTime) {
                    if (!player.oneTimeClaimedList.contains(this.identifier))
                        player.oneTimeClaimedList.add(this.identifier);
                }
                player.save();
            } else {
                    player.sendMessage(CobblemonKitsForge.languageConfig.claimIn.replace("%timer%", player.timer(this)));
            }
        } else {
            //error
            CobblemonKitsForge.log.error("A players data could not be verified while handing out kit rewards, this is bad and likely an internal error as this message should not be reachable! Please contact the developer on Discord @adenydd!");
        }
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}
