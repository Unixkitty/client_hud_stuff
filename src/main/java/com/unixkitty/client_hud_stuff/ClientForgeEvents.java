package com.unixkitty.client_hud_stuff;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = ClientHUDStuff.MODID, value = Dist.CLIENT)
public class ClientForgeEvents
{
    private static final int WHITE = 0xFFFFFF;

    @SubscribeEvent
    public static void onRenderGuiOverlay(final RenderGuiOverlayEvent.Post event)
    {
        final Minecraft minecraft = Minecraft.getInstance();
        final LocalPlayer player = minecraft.player;
        minecraft.getProfiler().push(ClientHUDStuff.MODID + "_mod");

        if (player != null && minecraft.level != null && minecraft.getCameraEntity() instanceof Player && !minecraft.options.hideGui)
        {
            final int screenWidth = minecraft.getWindow().getGuiScaledWidth();
            final int screenHeight = minecraft.getWindow().getGuiScaledHeight();

            PoseStack poseStack = event.getPoseStack();
            final ResourceLocation currentOverlay = event.getOverlay().id();

            if (Config.showMouseCoords.get() && !minecraft.mouseHandler.isMouseGrabbed() && minecraft.isWindowActive())
            {
                double mouseX = minecraft.mouseHandler.xpos();
                double mouseY = minecraft.mouseHandler.ypos();

                String text = String.format("%.1f", mouseX / minecraft.getWindow().getGuiScale()) + " , " + String.format("%.1f", mouseY / minecraft.getWindow().getGuiScale());

                int startX = (int) ((mouseX / minecraft.getWindow().getGuiScale()) - (minecraft.font.width(text) / 2));
                int startY = (int) ((mouseY / minecraft.getWindow().getGuiScale()) + minecraft.font.lineHeight);

                poseStack.pushPose();

                minecraft.font.drawShadow(poseStack, text, startX, startY, WHITE);

                poseStack.popPose();
            }

            if (player.isAlive() && !player.isSpectator())
            {
                if (minecraft.gameMode != null && minecraft.gameMode.canHurtPlayer() && Config.enableNumberHUDs.get())
                {
                    int leftHeight = 39;
                    int rightHeight = 39;

                    //Health numbers
                    if (currentOverlay == VanillaGuiOverlay.PLAYER_HEALTH.id() && Config.healthNumbersEnabled.get())
                    {
                        float currentHealth = player.getHealth();
                        float absorption = player.getAbsorptionAmount();
                        float maxHealth = player.getMaxHealth();

                        MutableComponent component = numberComponent(currentHealth, HUDElement.HEALTH);
                        if (absorption > 0)
                        {
                            component.append(separatorComponent("+"));
                            component.append(numberComponent(absorption, HUDElement.ABSORPTION));
                        }
                        component.append(separatorComponent("/"));
                        component.append(numberComponent(maxHealth, HUDElement.HEALTH));

                        int startX = screenWidth / 2 - 92 - minecraft.font.width(component.getVisualOrderText());
                        int startY = screenHeight - leftHeight;

                        poseStack.pushPose();

                        minecraft.font.drawShadow(poseStack, component, startX, startY, WHITE);

                        poseStack.popPose();
                    }

                    //Armour numbers
                    if (currentOverlay == VanillaGuiOverlay.ARMOR_LEVEL.id() && Config.armourNumbersEnabled.get())
                    {
                        float armor = (float) player.getAttributeValue(Attributes.ARMOR);
                        float armorToughness = (float) player.getAttributeValue(Attributes.ARMOR_TOUGHNESS);

                        if (armor > 0 || armorToughness > 0)
                        {
                            MutableComponent component = numberComponent(armor, HUDElement.ARMOUR);
                            if (armorToughness > 0)
                            {
                                component.append(separatorComponent("+"));
                                component.append(numberComponent(armorToughness, HUDElement.TOUGHNESS));
                            }

                            leftHeight += 10;
                            int startX = screenWidth / 2 - 92 - minecraft.font.width(component.getVisualOrderText());
                            int startY = screenHeight - leftHeight;

                            poseStack.pushPose();

                            minecraft.font.drawShadow(poseStack, component, startX, startY, WHITE);

                            poseStack.popPose();
                        }
                    }

                    //Hunger numbers
                    if (currentOverlay == VanillaGuiOverlay.FOOD_LEVEL.id() && Config.hungerNumbersEnabled.get())
                    {
                        int hunger = player.getFoodData().getFoodLevel();
                        float saturation = player.getFoodData().getSaturationLevel();

                        MutableComponent component = numberComponent(hunger, HUDElement.HUNGER);
                        if (saturation > 0)
                        {
                            component.append(separatorComponent("+"));
                            component.append(numberComponent(saturation, HUDElement.SATURATION));
                        }

                        int startX = screenWidth / 2 + 92;
                        int startY = screenHeight - rightHeight;

                        poseStack.pushPose();

                        minecraft.font.drawShadow(poseStack, component, startX, startY, WHITE);

                        poseStack.popPose();
                    }

                    //Air numbers
                    if (currentOverlay == VanillaGuiOverlay.AIR_LEVEL.id() && Config.airNumbersEnabled.get())
                    {
                        int currentAir = player.getAirSupply();
                        int maxAir = player.getMaxAirSupply();

                        if (player.isEyeInFluidType(ForgeMod.WATER_TYPE.get()) || currentAir < maxAir)
                        {
                            MutableComponent component = numberComponent(currentAir, HUDElement.AIR);
                            component.append(separatorComponent("/"));
                            component.append(numberComponent(maxAir, HUDElement.AIR));

                            rightHeight += 10;
                            int startX = screenWidth / 2 + 92;
                            int startY = screenHeight - rightHeight;

                            poseStack.pushPose();

                            minecraft.font.drawShadow(poseStack, component, startX, startY, WHITE);

                            poseStack.popPose();
                        }
                    }
                }

                //Distance HUD
                if (currentOverlay == VanillaGuiOverlay.CROSSHAIR.id() && Config.distanceHUDEnabled.get() && isHoldingItem(player))
                {
                    poseStack.pushPose();

                    String text = "Too far!";

                    double rayLength = Config.maxRayCastDistance.get();
                    Vec3 eyePosition = player.getEyePosition(event.getPartialTick());
                    Vec3 viewVector = player.getViewVector(event.getPartialTick());
                    Vec3 target = eyePosition.add(viewVector.x * rayLength, viewVector.y * rayLength, viewVector.z * rayLength);
                    BlockHitResult hitResult = minecraft.level.clip(new ClipContext(eyePosition, target, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
                    boolean miss = hitResult.getType() == HitResult.Type.MISS;
                    int colour = ChatFormatting.DARK_RED.getColor();

                    if (!miss)
                    {
                        text = String.format("%.1f", eyePosition.distanceTo(hitResult.getLocation())) + " m";
                        colour = WHITE;
                    }

                    float x = (screenWidth / 2F) - (minecraft.font.width(text) / 2F);
                    float y = (screenHeight / 2F) - (minecraft.font.lineHeight * 1.5F);

                    minecraft.font.drawShadow(poseStack, text, x, y, colour);

                    poseStack.popPose();
                }
            }
        }

        minecraft.getProfiler().pop();
    }

    private static Component separatorComponent(String s)
    {
        return Component.literal(s).withStyle(ChatFormatting.DARK_GRAY);
    }

    private static MutableComponent numberComponent(double value, HUDElement hud)
    {
        return Component.literal(String.format("%." + hud.decimalPoints() + "f", value)).withStyle(hud.formatting(), ChatFormatting.BOLD);
    }

    private static boolean isHoldingItem(LocalPlayer player)
    {
        return Config.distanceHUDItems.get().contains(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(player.getMainHandItem().getItem())).toString());
    }

    private enum HUDElement
    {
        HEALTH(Config.healthNumberDecimal, Config.healthNumberColour),
        ABSORPTION(Config.healthNumberDecimal, Config.absorptionNumberColour),
        HUNGER(() -> 0, Config.hungerNumberColour),
        SATURATION(Config.saturationNumberDecimal, Config.saturationNumberColour),
        AIR(() -> 0, Config.airNumberColour),
        ARMOUR(Config.armourNumberDecimal, Config.armourNumberColour),
        TOUGHNESS(Config.armourNumberDecimal, Config.armourToughnessColour);

        private final Supplier<Integer> decimal;
        private final Supplier<ChatFormatting> formatting;

        HUDElement(Supplier<Integer> decimal, Supplier<ChatFormatting> formatting)
        {
            this.decimal = decimal;
            this.formatting = formatting;
        }

        int decimalPoints()
        {
            return this.decimal.get();
        }

        ChatFormatting formatting()
        {
            return this.formatting.get();
        }
    }
}
