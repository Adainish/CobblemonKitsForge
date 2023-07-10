package io.github.adainish.cobblemonkitsforge.manager;

import io.github.adainish.cobblemonkitsforge.obj.ConfigurableKit;

import java.util.HashMap;

public class KitManager
{
    public HashMap<String, ConfigurableKit> kits = new HashMap<>();

    public KitManager()
    {
        init();
    }

    public void init()
    {
        if (this.kits.isEmpty())
        {
            for (int i = 0; i < 3; i++) {
                //gen new kit
                String id = "example" + i + 1;
                ConfigurableKit configurableKit = new ConfigurableKit(id);
                configurableKit.setDisplayOrder(i);
                kits.put(id, configurableKit);
            }
        }
    }
}
