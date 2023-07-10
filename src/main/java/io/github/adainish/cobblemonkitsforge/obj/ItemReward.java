package io.github.adainish.cobblemonkitsforge.obj;

import net.minecraft.world.item.ItemStack;

public class ItemReward
{
    public String displayName;
    public ItemStack stack;

    public ItemReward()
    {

    }

    public ItemReward(String displayName, ItemStack stack)
    {
        this.displayName = displayName;
        this.stack = stack;
    }
}
