package com.unixkitty.client_hud_stuff;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = ClientHUDStuff.MODID, value = Dist.CLIENT)
public class ClientForgeEvents
{
    @SubscribeEvent
    public static void onRenderGuiOverlay(final RenderGuiOverlayEvent.Post event)
    {
        Minecraft minecraft = Minecraft.getInstance();

        if (event.getOverlay().id().equals(VanillaGuiOverlay.CROSSHAIR.id()) && minecraft.player != null && minecraft.player.isAlive() && !minecraft.player.isSpectator() && !minecraft.options.hideGui && isHoldingItem(minecraft.player))
        {
            final int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            final int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();

            PoseStack poseStack = event.getPoseStack();

            poseStack.pushPose();

            String text = "Too far!";

            double rayLength = Config.maxRayCastDistance.get();
            Vec3 eyePosition = minecraft.player.getEyePosition(event.getPartialTick());
            Vec3 viewVector = minecraft.player.getViewVector(event.getPartialTick());
            Vec3 target = eyePosition.add(viewVector.x * rayLength, viewVector.y * rayLength, viewVector.z * rayLength);
            BlockHitResult hitResult = minecraft.level.clip(new ClipContext(eyePosition, target, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, minecraft.player));
            boolean miss = hitResult.getType() == HitResult.Type.MISS;

            if (!miss)
            {
                text = String.format("%.1f", eyePosition.distanceTo(hitResult.getLocation())) + " m";
            }

            float x = (screenWidth / 2F) - (minecraft.font.width(text) / 2F);
            float y = (screenHeight / 2F) - (minecraft.font.lineHeight * 1.5F);

            minecraft.font.drawShadow(poseStack, text, x, y, miss ? ChatFormatting.DARK_RED.getColor() : ChatFormatting.WHITE.getColor());

            poseStack.popPose();
        }
    }

    private static boolean isHoldingItem(LocalPlayer player)
    {
        return Config.distanceHUDItems.get().contains(ForgeRegistries.ITEMS.getKey(player.getMainHandItem().getItem()).toString());
    }
}
