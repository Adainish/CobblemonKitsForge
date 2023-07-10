package io.github.adainish.cobblemonkitsforge.obj;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.button.PlaceholderButton;
import ca.landonjw.gooeylibs2.api.button.linked.LinkType;
import ca.landonjw.gooeylibs2.api.button.linked.LinkedPageButton;
import ca.landonjw.gooeylibs2.api.helpers.PaginationHelper;
import ca.landonjw.gooeylibs2.api.page.LinkedPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.permission.CobblemonPermission;
import com.cobblemon.mod.common.api.permission.PermissionLevel;
import io.github.adainish.cobblemonkitsforge.CobblemonKitsForge;
import io.github.adainish.cobblemonkitsforge.storage.PlayerStorage;
import io.github.adainish.cobblemonkitsforge.util.Util;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Player
{
    public UUID uuid;
    public String username;
    public HashMap<String, Long> lastUsedKitData = new HashMap<>();

    public List<String> oneTimeClaimedList = new ArrayList<>();
    public Player(UUID uuid)
    {
        this.uuid = uuid;
    }

    public void sendMessage(String msg)
    {
        if (msg == null)
            return;
        if (msg.isEmpty())
            return;
        Util.send(uuid, msg);
    }

    public String getUsername()
    {
        if (this.username != null)
            return username;
        return "";
    }

    public void setUsername(String name)
    {
        this.username = name;
    }

    public void save()
    {
        PlayerStorage.savePlayer(this);
    }

    public void updateCache()
    {
        CobblemonKitsForge.instance.dataWrapper.playerCache.put(uuid, this);
    }

    @Nullable
    public ServerPlayer serverPlayer()
    {
        return Util.getPlayer(this.uuid);
    }

    public GooeyButton filler() {
        return GooeyButton.builder()
                .display(new ItemStack(Items.GRAY_STAINED_GLASS_PANE))
                .build();
    }

    public void openKitsGUI()
    {
        ServerPlayer serverPlayer = serverPlayer();
        if (serverPlayer == null)
            return;
        UIManager.openUIForcefully(serverPlayer, kitPage());
    }

    public List<ConfigurableKit> sortedKitsList()
    {
        List<ConfigurableKit> configurableKits = new ArrayList<>(CobblemonKitsForge.kitsConfig.kitManager.kits.values());
        configurableKits.sort(Comparator.comparing(ConfigurableKit::getDisplayOrder));
        return configurableKits;
    }

    public List<String> formattedPlaceholderList(List<String> strings, ConfigurableKit configurableKit)
    {
        List<String> stringList = new ArrayList<>();
        strings.forEach(s -> {
            String formatted = formattedPlaceHolderString(s, configurableKit);
            if (formatted == null)
                return;
            stringList.add(formatted);
        });
        return stringList;
    }

    public String formattedPlaceHolderString(String s, ConfigurableKit configurableKit)
    {
        String timer = timer(configurableKit);
        if (timer == null)
            return null;
        return s.replace("%timer%", timer);
    }

    public List<Button> sortedKitButtonList() {
        List<Button> buttons = new ArrayList<>();
        sortedKitsList().forEach(configurableKit -> {
            GooeyButton.Builder buttonBuilder = GooeyButton.builder();
            buttonBuilder.onClick(b -> {
                configurableKit.claim(this);
                UIManager.closeUI(b.getPlayer());
            });
            if (canClaim(configurableKit))
                buttonBuilder.display(configurableKit.displayItem.copy());
            else buttonBuilder.display(configurableKit.unavailableItem.copy());
            buttonBuilder.title(Util.formattedString(configurableKit.displayName));
            if (!configurableKit.description.isEmpty())
                buttonBuilder.lore(Util.formattedArrayList(formattedPlaceholderList(configurableKit.description, configurableKit)));
            buttons.add(buttonBuilder.build());
        });
        return buttons;
    }

    public LinkedPage kitPage()
    {
        ChestTemplate.Builder builder = ChestTemplate.builder(5);
        builder.fill(filler());

        PlaceholderButton placeHolderButton = new PlaceholderButton();
        LinkedPageButton previous = LinkedPageButton.builder()
                .display(new ItemStack(Items.SPECTRAL_ARROW))
                .title(Util.formattedString("Previous Page"))
                .linkType(LinkType.Previous)
                .build();

        LinkedPageButton next = LinkedPageButton.builder()
                .display(new ItemStack(Items.SPECTRAL_ARROW))
                .title(Util.formattedString("Next Page"))
                .linkType(LinkType.Next)
                .build();

        builder.set(0, 3, previous)
                .set(0, 5, next)
                .rectangle(1, 1, 3, 7, placeHolderButton);

        return PaginationHelper.createPagesFromPlaceholders(builder.build(), sortedKitButtonList(), LinkedPage.builder().template(builder.build()));
    }

    public long getExpirationTime(ConfigurableKit configurableKit)
    {
        if (!lastUsedKitData.containsKey(configurableKit.identifier))
            return System.currentTimeMillis() - 1;
        if (configurableKit.properties.cooldown <= 0)
            return System.currentTimeMillis() - 1;
        return lastUsedKitData.get(configurableKit.identifier) + TimeUnit.SECONDS.toMillis(configurableKit.properties.cooldown);
    }
    public boolean onCoolDown(ConfigurableKit configurableKit)
    {
        if (configurableKit.properties.cooldown <= 0)
            return true;
        return getExpirationTime(configurableKit) > System.currentTimeMillis();
    }

    public boolean hasOneTimeClaimed(ConfigurableKit configurableKit)
    {
        return this.oneTimeClaimedList.contains(configurableKit.identifier);
    }

    public boolean canClaim(ConfigurableKit configurableKit)
    {
        if (!configurableKit.properties.permission.isBlank() || !configurableKit.properties.permission.isEmpty()) {
            if (!hasPermission(configurableKit)) {
                return false;
            }
        }
        if (configurableKit.properties.oneTime) {
            return !hasOneTimeClaimed(configurableKit);

        }
        return !onCoolDown(configurableKit);
    }

    public String timer(ConfigurableKit configurableKit)
    {
        String s = "";
        long expirationTime = getExpirationTime(configurableKit);
        long cd = expirationTime - System.currentTimeMillis();
        long hours = cd / Util.HOUR_IN_MILLIS;
        cd = cd - (hours * Util.HOUR_IN_MILLIS);
        long minutes = cd / Util.MINUTE_IN_MILLIS;
        if (cd <= 0)
        {
            hours = 0;
            minutes = 0;
        }
        s = "&a%hours% &7hours and &b%minutes% &7minutes"
                .replace("%hours%", String.valueOf(hours))
                .replace("%minutes%", String.valueOf(minutes));

        return s;
    }

    public boolean hasPermission(ConfigurableKit configurableKit)
    {
        ServerPlayer serverPlayer = serverPlayer();
        if (serverPlayer != null)
        return Cobblemon.INSTANCE.getPermissionValidator().hasPermission(serverPlayer, new CobblemonPermission(configurableKit.properties.permission, PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS));
        else return false;
    }
}
