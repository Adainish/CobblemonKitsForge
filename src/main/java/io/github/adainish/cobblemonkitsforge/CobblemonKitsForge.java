package io.github.adainish.cobblemonkitsforge;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.mojang.logging.LogUtils;
import io.github.adainish.cobblemonkitsforge.command.KitsCommand;
import io.github.adainish.cobblemonkitsforge.config.KitsConfig;
import io.github.adainish.cobblemonkitsforge.config.LanguageConfig;
import io.github.adainish.cobblemonkitsforge.subscriptions.Subscriptions;
import io.github.adainish.cobblemonkitsforge.wrapper.DataWrapper;
import kotlin.Unit;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CobblemonKitsForge.MODID)
public class CobblemonKitsForge {


    // Define mod id in a common place for everything to reference
    public static final String MODID = "cobblemonkitsforge";

    public static CobblemonKitsForge instance;
    // Define mod id in a common place for everything to reference
    // Directly reference a slf4j logger
    public static final String MOD_NAME = "CobblemonKits";
    public static final String VERSION = "1.0.0";
    public static final String AUTHORS = "Winglet";
    public static final String YEAR = "2023";

    public static final Logger log = LogManager.getLogger(MOD_NAME);
    private static MinecraftServer server;
    private static File configDir;
    private static File storageDir;
    private static File playerStorageDir;

    public static LanguageConfig languageConfig;

    public static KitsConfig kitsConfig;

    public static Subscriptions subscriptions;

    public DataWrapper dataWrapper;


    public static MinecraftServer getServer() {
        return server;
    }

    public static void setServer(MinecraftServer server) {
        CobblemonKitsForge.server = server;
    }

    public static File getConfigDir() {
        return configDir;
    }

    public static void setConfigDir(File configDir) {
        CobblemonKitsForge.configDir = configDir;
    }

    public static File getStorageDir() {
        return storageDir;
    }

    public static void setStorageDir(File storageDir) {
        CobblemonKitsForge.storageDir = storageDir;
    }

    public static File getPlayerStorageDir() {
        return playerStorageDir;
    }

    public static void setPlayerStorageDir(File playerStorageDir) {
        CobblemonKitsForge.playerStorageDir = playerStorageDir;
    }


    // Directly reference a slf4j logger
    public CobblemonKitsForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        instance = this;
        log.info("Booting up %n by %authors %v %y"
                .replace("%n", MOD_NAME)
                .replace("%authors", AUTHORS)
                .replace("%v", VERSION)
                .replace("%y", YEAR)
        );
        CobblemonEvents.SERVER_STARTED.subscribe(Priority.NORMAL, server -> {
            setServer(server);
            dataWrapper = new DataWrapper();
            //load data from config
            reload();
            //register listeners
            subscriptions = new Subscriptions();


            return Unit.INSTANCE;
        });
    }

    @SubscribeEvent
    public void onCommandRegistration(RegisterCommandsEvent event)
    {
        event.getDispatcher().register(KitsCommand.getCommand());
    }


    public void initDirs() {
        setConfigDir(new File(FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()) + "/CobblemonKits/"));
        getConfigDir().mkdir();
        setPlayerStorageDir(new File(configDir, "/playerdata/"));
        getPlayerStorageDir().mkdirs();
    }



    public void initConfigs() {
        LanguageConfig.writeConfig();
        languageConfig = LanguageConfig.getConfig();

        KitsConfig.writeConfig();
        kitsConfig = KitsConfig.getConfig();


    }

    public void save()
    {
        if (kitsConfig != null)
            KitsConfig.saveConfig(kitsConfig);
        else log.error("Failed to save..");
    }

    public void reload()
    {
        initDirs();
        initConfigs();
    }



}
