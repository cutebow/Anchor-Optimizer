package me.cutebow.client_side_anchors;

import me.cutebow.client_side_anchors.client.AnchorPredictionManager;
import me.cutebow.client_side_anchors.client.ClientSideAnchorsIntro;
import me.cutebow.client_side_anchors.config.ClientSideAnchorsConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class ClientSideAnchors implements ClientModInitializer {
    public static final String MOD_ID = "client_side_anchors";

    @Override
    public void onInitializeClient() {
        ClientSideAnchorsConfig.load();
        ClientTickEvents.END_CLIENT_TICK.register(ClientSideAnchors::onEndTick);
    }

    private static void onEndTick(MinecraftClient client) {
        AnchorPredictionManager.tick();
        ClientSideAnchorsIntro.tick(client);
    }
}
