package me.cutebow.client_side_anchors.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

public final class MenuBackground {
    private static final Identifier TEXTURE = Identifier.of(
            "client_side_anchors",
            "textures/gui/programmer_art_menu_background.png"
    );

    private MenuBackground() {
    }

    public static void draw(DrawContext context, int width, int height) {
        Screen.renderBackgroundTexture(context, TEXTURE, 0, 0, 0.0F, 0.0F, width, height);
        context.fill(0, 0, width, height, 0x48000000);
    }
}
