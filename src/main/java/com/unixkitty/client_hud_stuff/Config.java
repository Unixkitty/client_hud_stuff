package com.unixkitty.client_hud_stuff;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
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
    public static final ImmutableSet<ChatFormatting> ACCEPTABLE_COLOURS = ImmutableSet.of(
            ChatFormatting.BLACK,
            ChatFormatting.DARK_BLUE,
            ChatFormatting.DARK_GREEN,
            ChatFormatting.DARK_AQUA,
            ChatFormatting.DARK_RED,
            ChatFormatting.DARK_PURPLE,
            ChatFormatting.GOLD,
            ChatFormatting.GRAY,
            ChatFormatting.DARK_GRAY,
            ChatFormatting.BLUE,
            ChatFormatting.GREEN,
            ChatFormatting.AQUA,
            ChatFormatting.RED,
            ChatFormatting.LIGHT_PURPLE,
            ChatFormatting.YELLOW,
            ChatFormatting.WHITE
    );

    public static ForgeConfigSpec CLIENT_CONFIG;

    public static ForgeConfigSpec.BooleanValue distanceHUDEnabled;
    public static ForgeConfigSpec.DoubleValue maxRayCastDistance;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> distanceHUDItems;

    public static ForgeConfigSpec.BooleanValue enableNumberHUDs;

    public static ForgeConfigSpec.BooleanValue healthNumbersEnabled;
    public static ForgeConfigSpec.EnumValue<ChatFormatting> healthNumberColour;
    public static ForgeConfigSpec.EnumValue<ChatFormatting> absorptionNumberColour;
    public static ForgeConfigSpec.IntValue healthNumberDecimal;

    public static ForgeConfigSpec.BooleanValue hungerNumbersEnabled;
    public static ForgeConfigSpec.EnumValue<ChatFormatting> hungerNumberColour;
    public static ForgeConfigSpec.EnumValue<ChatFormatting> saturationNumberColour;
    public static ForgeConfigSpec.IntValue saturationNumberDecimal;

    public static ForgeConfigSpec.BooleanValue airNumbersEnabled;
    public static ForgeConfigSpec.EnumValue<ChatFormatting> airNumberColour;

    public static ForgeConfigSpec.BooleanValue armourNumbersEnabled;
    public static ForgeConfigSpec.EnumValue<ChatFormatting> armourNumberColour;
    public static ForgeConfigSpec.EnumValue<ChatFormatting> armourToughnessColour;
    public static ForgeConfigSpec.IntValue armourNumberDecimal;

    static
    {
        ForgeConfigSpec.Builder clientConfig = new ForgeConfigSpec.Builder();

        {
            clientConfig.push("Distance HUD");
            distanceHUDEnabled = clientConfig.comment("Enable distance HUD").define("distanceHUDEnabled", true);
            maxRayCastDistance = clientConfig.comment("Maximum distance to raycast").defineInRange("maxRayCastDistance", 30D, 0, 256D);
            distanceHUDItems = clientConfig.comment("List of item ids for the distance HUD to render with").defineListAllowEmpty(Lists.newArrayList("distanceHUDItems"), Collections::emptyList, (potentialEntry) -> potentialEntry instanceof String string && ResourceLocation.isValidResourceLocation(string));
            clientConfig.pop();
        }

        {
            clientConfig.push("Numbers for vanilla HUDs");
            enableNumberHUDs = clientConfig.comment("Enable numbers for vanilla HUDs").define("enableNumberHUDs", true);
            {
                clientConfig.push("Health numbers");
                healthNumbersEnabled = clientConfig.define("healthNumbersEnabled", true);
                healthNumberColour = clientConfig.comment("Colour for health numbers").defineEnum("healthNumberColour", ChatFormatting.RED, ACCEPTABLE_COLOURS);
                absorptionNumberColour = clientConfig.comment("Colour for health absorption numbers").defineEnum("absorptionNumberColour", ChatFormatting.GOLD, ACCEPTABLE_COLOURS);
                healthNumberDecimal = clientConfig.comment("Decimal points to show").defineInRange("healthNumberDecimal", 1, 0, 2);
                clientConfig.pop();
            }
            {
                clientConfig.push("Hunger numbers");
                hungerNumbersEnabled = clientConfig.define("hungerNumbersEnabled", true);
                hungerNumberColour = clientConfig.comment("Colour for hunger numbers").defineEnum("hungerNumberColour", ChatFormatting.DARK_GREEN, ACCEPTABLE_COLOURS);
                saturationNumberColour = clientConfig.comment("Colour for saturation numbers").defineEnum("saturationNumberColour", ChatFormatting.YELLOW, ACCEPTABLE_COLOURS);
                saturationNumberDecimal = clientConfig.comment("Decimal points to show for saturation").defineInRange("hungerNumberDecimal", 1, 0, 2);
                clientConfig.pop();
            }
            {
                clientConfig.push("Air numbers");
                airNumbersEnabled = clientConfig.define("airNumbersEnabled", true);
                airNumberColour = clientConfig.comment("Colour for air numbers").defineEnum("airNumberColour", ChatFormatting.AQUA, ACCEPTABLE_COLOURS);
                clientConfig.pop();
            }
            {
                clientConfig.push("Armour numbers");
                armourNumbersEnabled = clientConfig.define("armourNumbersEnabled", true);
                armourNumberColour = clientConfig.comment("Colour for armour numbers").defineEnum("armourNumberColour", ChatFormatting.GRAY, ACCEPTABLE_COLOURS);
                armourToughnessColour = clientConfig.comment("Colour for armour toughness numbers").defineEnum("armourToughnessColour", ChatFormatting.WHITE, ACCEPTABLE_COLOURS);
                armourNumberDecimal = clientConfig.comment("Decimal points to show").defineInRange("armourNumberDecimal", 1, 0, 2);
                clientConfig.pop();
            }
            clientConfig.pop();
        }

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
