package io.github.adainish.cobblemonkitsforge.command;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.permission.CobblemonPermission;
import com.cobblemon.mod.common.api.permission.PermissionLevel;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.adainish.cobblemonkitsforge.CobblemonKitsForge;
import io.github.adainish.cobblemonkitsforge.obj.ConfigurableKit;
import io.github.adainish.cobblemonkitsforge.obj.ItemReward;
import io.github.adainish.cobblemonkitsforge.obj.Player;
import io.github.adainish.cobblemonkitsforge.storage.PlayerStorage;
import io.github.adainish.cobblemonkitsforge.util.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class KitsCommand
{
    public static List<ConfigurableKit> sortedKitsList()
    {
        List<ConfigurableKit> configurableKits = new ArrayList<>(CobblemonKitsForge.kitsConfig.kitManager.kits.values());
        configurableKits.sort(Comparator.comparing(ConfigurableKit::getDisplayOrder));
        return configurableKits;
    }
    public static LiteralArgumentBuilder<CommandSourceStack> getCommand() {
        return Commands.literal("kits")
                .requires(commandSourceStack -> Cobblemon.INSTANCE.getPermissionValidator().hasPermission(commandSourceStack, new CobblemonPermission("cobblemonkits.command.kits.base", PermissionLevel.NONE)))
                .executes(cc -> {
                    try {
                        if (cc.getSource().isPlayer()) {
                            Player player = PlayerStorage.getPlayer(cc.getSource().getPlayerOrException().getUUID());
                            if (player != null) {
                                if (CobblemonKitsForge.kitsConfig.guiEnabled) {
                                    player.openKitsGUI();
                                } else {
                                    sortedKitsList().forEach(configurableKit -> {
                                        if (player.hasPermission(configurableKit)) {
                                            String status = "&a&lAvailable";
                                            if (!player.canClaim(configurableKit))
                                                status = "&c&lKit unavailable!, claim in %timer%".replace("%timer%", player.timer(configurableKit));
                                            Util.send(cc.getSource(), "%kit% %status%".replace("%kit%", configurableKit.displayName).replace("%status%", status));
                                        }
                                    });
                                }
                            }  else
                                cc.getSource().sendFailure(Component.literal(Util.formattedString("&cSomething went wrong while retrieving your player data...")));
                        } else {
                            Util.send(cc.getSource(), "&4Only a player may run this command");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return 1;
                })
                .then(Commands.literal("resetcooldown")
                        .requires(commandSourceStack -> Cobblemon.INSTANCE.getPermissionValidator().hasPermission(commandSourceStack, new CobblemonPermission("cobblemonkits.command.kits.give", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS)))
                        .executes(cc -> {
                            Util.send(cc.getSource(), "&cPlease provide a valid kitname and player");
                            return 1;
                        })
                        .then(Commands.argument("kitname", StringArgumentType.string())
                                .executes(cc -> {
                                    String kitName = StringArgumentType.getString(cc, "kitname");
                                    if (CobblemonKitsForge.kitsConfig.kitManager.kits.containsKey(kitName))
                                    {
                                        Util.send(cc.getSource(), "&cPlease provide a valid player!");
                                    } else {
                                        Util.send(cc.getSource(), "&cThat kit does not exist!");
                                    }
                                    return 1;
                                })
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(cc -> {
                                            String kitName = StringArgumentType.getString(cc, "kitname");
                                            ServerPlayer serverPlayer = EntityArgument.getPlayer(cc, "player");
                                            if (CobblemonKitsForge.kitsConfig.kitManager.kits.containsKey(kitName))
                                            {
                                                Player player = PlayerStorage.getPlayer(serverPlayer.getUUID());
                                                if (player != null)
                                                {
                                                    player.lastUsedKitData.remove(kitName);
                                                    player.save();
                                                    Util.send(cc.getSource(), "&aForcibly reset the cooldown for the kit %id% on %player%".replace("%id%", kitName).replace("%player%", player.getUsername()));
                                                } else {
                                                    Util.send(cc.getSource(), "&4Something went wrong while retrieving the player data... could not hand out the kit!");
                                                }
                                            } else {
                                                Util.send(cc.getSource(), "&cThat kit does not exist!");
                                            }
                                            return 1;
                                        })
                                )
                        )
                )
                .then(Commands.literal("resetusage")
                        .requires(commandSourceStack -> Cobblemon.INSTANCE.getPermissionValidator().hasPermission(commandSourceStack, new CobblemonPermission("cobblemonkits.command.kits.give", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS)))
                        .executes(cc -> {
                            Util.send(cc.getSource(), "&cPlease provide a valid kitname and player");
                            return 1;
                        })
                        .then(Commands.argument("kitname", StringArgumentType.string())
                                .executes(cc -> {
                                    String kitName = StringArgumentType.getString(cc, "kitname");
                                    if (CobblemonKitsForge.kitsConfig.kitManager.kits.containsKey(kitName))
                                    {
                                        Util.send(cc.getSource(), "&cPlease provide a valid player!");
                                    } else {
                                        Util.send(cc.getSource(), "&cThat kit does not exist!");
                                    }
                                    return 1;
                                })
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(cc -> {
                                            String kitName = StringArgumentType.getString(cc, "kitname");
                                            ServerPlayer serverPlayer = EntityArgument.getPlayer(cc, "player");
                                            if (CobblemonKitsForge.kitsConfig.kitManager.kits.containsKey(kitName))
                                            {
                                                Player player = PlayerStorage.getPlayer(serverPlayer.getUUID());
                                                if (player != null)
                                                {
                                                    player.oneTimeClaimedList.remove(kitName);
                                                    player.save();
                                                    Util.send(cc.getSource(), "&aForcibly reset the one time cooldown for %id% on %player%".replace("%id%", kitName).replace("%player%", player.getUsername()));
                                                } else {
                                                    Util.send(cc.getSource(), "&4Something went wrong while retrieving the player data... could not hand out the kit!");
                                                }
                                            } else {
                                                Util.send(cc.getSource(), "&cThat kit does not exist!");
                                            }
                                            return 1;
                                        })
                                )
                        )
                )
                .then(Commands.literal("give")
                        .requires(commandSourceStack -> Cobblemon.INSTANCE.getPermissionValidator().hasPermission(commandSourceStack, new CobblemonPermission("cobblemonkits.command.kits.give", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS)))
                        .executes(cc -> {
                            Util.send(cc.getSource(), "&cPlease provide a valid kitname and player");
                            return 1;
                        })
                        .then(Commands.argument("kitname", StringArgumentType.string())
                                .executes(cc -> {
                                    String kitName = StringArgumentType.getString(cc, "kitname");
                                    if (CobblemonKitsForge.kitsConfig.kitManager.kits.containsKey(kitName))
                                    {
                                        Util.send(cc.getSource(), "&cPlease provide a valid player!");
                                    } else {
                                        Util.send(cc.getSource(), "&cThat kit does not exist!");
                                    }
                                    return 1;
                                })
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(cc -> {
                                            String kitName = StringArgumentType.getString(cc, "kitname");
                                            ServerPlayer serverPlayer = EntityArgument.getPlayer(cc, "player");
                                            if (CobblemonKitsForge.kitsConfig.kitManager.kits.containsKey(kitName))
                                            {
                                                ConfigurableKit configurableKit = CobblemonKitsForge.kitsConfig.kitManager.kits.get(kitName);
                                               Player player = PlayerStorage.getPlayer(serverPlayer.getUUID());
                                               if (player != null)
                                               {
                                                   configurableKit.forceClaim(player);
                                                   Util.send(cc.getSource(), "&aForcibly gave out the kit %id% to %player%".replace("%id%", kitName).replace("%player%", player.getUsername()));
                                               } else {
                                                   Util.send(cc.getSource(), "&4Something went wrong while retrieving the player data... could not hand out the kit!");
                                               }
                                            } else {
                                                Util.send(cc.getSource(), "&cThat kit does not exist!");
                                            }
                                            return 1;
                                        })
                                )
                        )
                )
                .then(Commands.literal("claim")
                        .requires(commandSourceStack -> Cobblemon.INSTANCE.getPermissionValidator().hasPermission(commandSourceStack, new CobblemonPermission("cobblemonkits.command.kits.claim", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS)))
                        .executes(cc -> {
                            Util.send(cc.getSource(), "&cPlease provide the name of the kit you want to claim!");
                            return 1;
                        }) .then(Commands.argument("name", StringArgumentType.string())
                                .executes(cc -> {
                                    String name = StringArgumentType.getString(cc, "name");
                                    if (!CobblemonKitsForge.kitsConfig.kitManager.kits.containsKey(name))
                                    {
                                        Util.send(cc.getSource(), "&cA kit with that name does not exist!");
                                        return 1;
                                    }
                                    if (cc.getSource().isPlayer()) {
                                        Player player = PlayerStorage.getPlayer(cc.getSource().getPlayerOrException().getUUID());
                                        if (player != null) {
                                            CobblemonKitsForge.kitsConfig.kitManager.kits.get(name).claim(player);
                                        } else {
                                            Util.send(cc.getSource(), "&cFailed to claim your kit!");
                                        }
                                    } else {
                                        Util.send(cc.getSource(), "&4&lOnly a player may run this command");
                                    }
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("delete")
                        .requires(commandSourceStack -> Cobblemon.INSTANCE.getPermissionValidator().hasPermission(commandSourceStack, new CobblemonPermission("cobblemonkits.command.kits.delete", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS)))
                        .executes(cc -> {
                            Util.send(cc.getSource(), "&cPlease provide a name for this kit!");
                            return 1;
                        }) .then(Commands.argument("name", StringArgumentType.string())
                                .executes(cc -> {
                                    String name = StringArgumentType.getString(cc, "name");
                                    if (!CobblemonKitsForge.kitsConfig.kitManager.kits.containsKey(name))
                                    {
                                        Util.send(cc.getSource(), "&cA kit with that name does not exist!");
                                        return 1;
                                    }
                                    CobblemonKitsForge.kitsConfig.kitManager.kits.remove(name);
                                    //save
                                    CobblemonKitsForge.instance.save();
                                    Util.send(cc.getSource(), "&eAttempted to delete the kit... Please check your config and console for any issues!");
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("create")
                        .requires(commandSourceStack -> Cobblemon.INSTANCE.getPermissionValidator().hasPermission(commandSourceStack, new CobblemonPermission("cobblemonkits.command.kits.create", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS)))
                        .executes(cc -> {
                            Util.send(cc.getSource(), "&cPlease provide a name for this kit!");
                            return 1;
                        }) .then(Commands.argument("name", StringArgumentType.string())
                                .executes(cc -> {
                                    String name = StringArgumentType.getString(cc, "name");
                                    if (CobblemonKitsForge.kitsConfig.kitManager.kits.containsKey(name))
                                    {
                                        Util.send(cc.getSource(), "&cA kit with that name already exists!");
                                        return 1;
                                    }
                                    ConfigurableKit configurableKit = new ConfigurableKit(name);
                                    if (cc.getSource().isPlayer())
                                    {
                                        configurableKit.items.clear();
                                        cc.getSource().getPlayer().getInventory().items.forEach(stack -> {
                                            if (stack.isEmpty())
                                                return;
                                            ItemReward itemReward = new ItemReward(stack.getDisplayName().getString(), stack);
                                            configurableKit.items.add(itemReward);
                                        });
                                        Util.send(cc.getSource(), "&aCopied your inventory for the created kit!");
                                    }
                                    CobblemonKitsForge.kitsConfig.kitManager.kits.put(name, configurableKit);
                                    //save
                                    CobblemonKitsForge.instance.save();
                                    Util.send(cc.getSource(), "&eAttempted to create the kit... Please check your config and console for any issues!");
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("reload")
                        .requires(commandSourceStack -> Cobblemon.INSTANCE.getPermissionValidator().hasPermission(commandSourceStack, new CobblemonPermission("cobblemonkits.command.kits.reload", PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS)))
                        .executes(cc -> {
                            Util.send(cc.getSource(), "&eReloaded kits, please check the console for any errors.");
                            CobblemonKitsForge.instance.reload();
                            return 1;
                        })
                )
                ;
    }
}
