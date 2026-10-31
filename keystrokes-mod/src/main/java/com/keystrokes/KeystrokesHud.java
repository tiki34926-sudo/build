package com.keystrokes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.RenderTickCounter;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.Deque;

public class KeystrokesHud {
    // Layout
    private static final int X = 10, Y = 10;
    private static final int S = 22;      // key size
    private static final int G = 3;       // gap
    private static final int W = S * 3 + G * 2;

    // Colors (ARGB)
    private static final int BG_IDLE = 0xA0141416;
    private static final int BG_DOWN = 0xE6FFFFFF;
    private static final int FG_IDLE = 0xFFFFFFFF;
    private static final int FG_DOWN = 0xFF141416;

    // Fade state: W A S D LMB RMB SPACE
    private static final float[] anim = new float[7];
    private static long lastNanos = System.nanoTime();

    // CPS tracking
    private static final Deque<Long> lmbClicks = new ArrayDeque<>();
    private static final Deque<Long> rmbClicks = new ArrayDeque<>();
    private static boolean lastLmb, lastRmb;

    public static void render(DrawContext ctx, RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options.hudHidden || mc.currentScreen != null || mc.player == null) return;

        long now = System.nanoTime();
        float dt = (now - lastNanos) / 1_000_000_000f;
        lastNanos = now;

        GameOptions o = mc.options;
        long window = mc.getWindow().getHandle();
        boolean lmb = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean rmb = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        long ms = System.currentTimeMillis();
        if (lmb && !lastLmb) lmbClicks.add(ms);
        if (rmb && !lastRmb) rmbClicks.add(ms);
        lastLmb = lmb;
        lastRmb = rmb;
        trim(lmbClicks, ms);
        trim(rmbClicks, ms);

        boolean[] down = {
                o.forwardKey.isPressed(), o.leftKey.isPressed(),
                o.backKey.isPressed(), o.rightKey.isPressed(),
                lmb, rmb, o.jumpKey.isPressed()
        };
        for (int i = 0; i < anim.length; i++) {
            float target = down[i] ? 1f : 0f;
            float step = dt * 14f;
            if (anim[i] < target) anim[i] = Math.min(target, anim[i] + step);
            else anim[i] = Math.max(target, anim[i] - step);
        }

        int row2 = Y + S + G;
        int row3 = row2 + S + G;
        int row4 = row3 + S + G;
        int half = (W - G) / 2;

        key(ctx, mc, "W", anim[0], X + S + G, Y, S, S);
        key(ctx, mc, "A", anim[1], X, row2, S, S);
        key(ctx, mc, "S", anim[2], X + S + G, row2, S, S);
        key(ctx, mc, "D", anim[3], X + (S + G) * 2, row2, S, S);
        key(ctx, mc, "LMB " + lmbClicks.size(), anim[4], X, row3, half, S);
        key(ctx, mc, "RMB " + rmbClicks.size(), anim[5], X + half + G, row3, W - half - G, S);

        // Space bar: wide bar with a line in the middle
        int bg = lerp(BG_IDLE, BG_DOWN, anim[6]);
        int fg = lerp(FG_IDLE, FG_DOWN, anim[6]);
        rrect(ctx, X, row4, W, 14, bg);
        ctx.fill(X + 12, row4 + 6, X + W - 12, row4 + 8, fg);
    }

    private static void key(DrawContext ctx, MinecraftClient mc, String label,
                            float t, int x, int y, int w, int h) {
        rrect(ctx, x, y, w, h, lerp(BG_IDLE, BG_DOWN, t));
        ctx.drawCenteredTextWithShadow(mc.textRenderer, label, x + w / 2,
                y + (h - 8) / 2, lerp(FG_IDLE, FG_DOWN, t));
    }

    /** Rounded rectangle (radius ~2) built from three overlapping fills. */
    private static void rrect(DrawContext c, int x, int y, int w, int h, int col) {
        c.fill(x + 2, y, x + w - 2, y + h, col);
        c.fill(x + 1, y + 1, x + w - 1, y + h - 1, col);
        c.fill(x, y + 2, x + w, y + h - 2, col);
    }

    private static void trim(Deque<Long> q, long now) {
        while (!q.isEmpty() && now - q.peekFirst() > 1000) q.pollFirst();
    }

    private static int lerp(int a, int b, float t) {
        int aa = a >>> 24, ar = (a >> 16) & 255, ag = (a >> 8) & 255, ab = a & 255;
        int ba = b >>> 24, br = (b >> 16) & 255, bg = (b >> 8) & 255, bb = b & 255;
        return ((int) (aa + (ba - aa) * t) << 24)
                | ((int) (ar + (br - ar) * t) << 16)
                | ((int) (ag + (bg - ag) * t) << 8)
                | (int) (ab + (bb - ab) * t);
    }
}
