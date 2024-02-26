package com.unixkitty.client_hud_stuff;

import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = ClientHUDStuff.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    public static ForgeConfigSpec CLIENT_CONFIG;

    public static ForgeConfigSpec.DoubleValue maxRayCastDistance;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> distanceHUDItems;

    static
    {
        ForgeConfigSpec.Builder clientConfig = new ForgeConfigSpec.Builder();

        clientConfig.push("Distance HUD");
        maxRayCastDistance = clientConfig.comment("Maximum distance to raycast").defineInRange("maxRayCastDistance", 30D, 0, 256D);
        distanceHUDItems = clientConfig.comment("List of item ids for the distance HUD to render with").defineListAllowEmpty(Lists.newArrayList("distanceHUDItems"), Collections::emptyList, (potentialEntry) -> potentialEntry instanceof String string && ResourceLocation.isValidResourceLocation(string));
        clientConfig.pop();

        CLIENT_CONFIG = clientConfig.build();
    }

    private static void reload(ModConfig config, ModConfig.Type type)
    {
        if (Objects.requireNonNull(type) == ModConfig.Type.CLIENT)
        {
            CLIENT_CONFIG.setConfig(config.getConfigData());
        }
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading event)
    {
        if (!event.getConfig().getModId().equals(ClientHUDStuff.MODID)) return;

        reload(event.getConfig(), event.getConfig().getType());
    }

    @SubscribeEvent
    public static void onFileChange(final ModConfigEvent.Reloading event)
    {
        if (!event.getConfig().getModId().equals(ClientHUDStuff.MODID)) return;

        reload(event.getConfig(), event.getConfig().getType());
    }
}
