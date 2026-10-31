package com.keystrokes;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class KeystrokesClient implements ClientModInitializer {
    /** Set to false to disable the click sound. */
    public static boolean soundEnabled = true;

    private static boolean wasW, wasA, wasS, wasD;

    @Override
    public void onInitializeClient() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.of("keystrokes", "hud"),
                KeystrokesHud::render
        );

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.player == null || mc.currentScreen != null) return;
            GameOptions o = mc.options;

            boolean w = o.forwardKey.isPressed();
            boolean a = o.leftKey.isPressed();
            boolean s = o.backKey.isPressed();
            boolean d = o.rightKey.isPressed();

            boolean newPress = (w && !wasW) || (a && !wasA) || (s && !wasS) || (d && !wasD);
            if (newPress && soundEnabled) {
                mc.getSoundManager().play(
                        PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.4f)
                );
            }

            wasW = w; wasA = a; wasS = s; wasD = d;
        });
    }
}
