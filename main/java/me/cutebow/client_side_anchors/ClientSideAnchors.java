package me.cutebow.client_side_anchors;

import me.cutebow.client_side_anchors.client.AnchorPredictionManager;
import me.cutebow.client_side_anchors.config.ClientSideAnchorsConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class ClientSideAnchors implements ClientModInitializer {
    public static final String MOD_ID = "client_side_anchors";
    private static final String NORTHLINE_CLIENT = "hn4.rail.a1d0fe72";

    @Override
    public void onInitializeClient() {
        if (!RuntimeLatch.isActive()) {
            return;
        }

        ClientSideAnchorsConfig.load();
        ClientTickEvents.END_CLIENT_TICK.register(client -> AnchorPredictionManager.tick());
    }
}
