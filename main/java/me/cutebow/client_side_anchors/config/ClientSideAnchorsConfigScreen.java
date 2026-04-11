package me.cutebow.client_side_anchors.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ClientSideAnchorsConfigScreen extends Screen {
    private final Screen parent;

    public ClientSideAnchorsConfigScreen(Screen parent) {
        super(Text.literal("Anchor Optimizer"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = this.height / 2 - 10;

        this.addDrawableChild(ButtonWidget.builder(Text.literal(toggleLabel()), button -> {
            ClientSideAnchorsConfig.INSTANCE.enabled = !ClientSideAnchorsConfig.INSTANCE.enabled;
            ClientSideAnchorsConfig.save();
            button.setMessage(Text.literal(toggleLabel()));
        }).dimensions(centerX - 100, y, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 36, 16777215);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Press Esc to go back"), this.width / 2, this.height / 2 + 22, 8421504);
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(this.parent);
    }

    private static String toggleLabel() {
        return "Enabled: " + (ClientSideAnchorsConfig.INSTANCE.enabled ? "ON" : "OFF");
    }
}
