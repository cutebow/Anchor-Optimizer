package me.cutebow.client_side_anchors.client;

import me.cutebow.client_side_anchors.config.ClientSideAnchorsConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class ClientSideAnchorsIntro {
    private static boolean sentThisSession = false;

    private ClientSideAnchorsIntro() {}

    public static void tick(MinecraftClient client) {
        if (sentThisSession) return;
        if (client == null) return;

        if (client.player == null || client.world == null) return;

        ClientSideAnchorsConfig cfg = ClientSideAnchorsConfig.INSTANCE;
        if (cfg == null) {
            sentThisSession = true;
            return;
        }

        if (!cfg.enabled) {
            sentThisSession = true;
            return;
        }

        if (cfg.introShown) {
            sentThisSession = true;
            return;
        }

        ClientPlayerEntity p = client.player;

        MutableText title = Text.literal("? Client Side Anchors ?").formatted(Formatting.AQUA, Formatting.BOLD);
        p.sendMessage(title, false);

        p.sendMessage(Text.literal("------------------------------").formatted(Formatting.DARK_GRAY), false);

        p.sendMessage(Text.literal("This mod can help you react/chain faster with less hesitation and instantly removes ghosted blocks as well, so you know to re-anchor if your block ghosted. also visually removes the heros anchor opti anchor so it looks like normal anchoring while the anchor opti functions as normal  this message will only show once so dw").formatted(Formatting.YELLOW), false);

        MutableText sig = Text.literal("- cutebow").formatted(Formatting.DARK_PURPLE, Formatting.ITALIC);
        p.sendMessage(sig, false);

        cfg.introShown = true;
        ClientSideAnchorsConfig.save();
        sentThisSession = true;
    }
}
