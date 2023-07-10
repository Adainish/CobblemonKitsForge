package io.github.adainish.cobblemonkitsforge.obj;

public class KitProperties
{
    public int cooldown;
    public boolean oneTime;
    public String permission;

    public KitProperties()
    {

    }

    public KitProperties(int cooldown, boolean oneTime, String permission)
    {
        this.cooldown = cooldown;
        this.oneTime = oneTime;
        this.permission = permission;
    }
}
