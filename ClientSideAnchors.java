package me.cutebow.client_side_anchors;

import me.cutebow.client_side_anchors.client.AnchorTracker;
import me.cutebow.client_side_anchors.config.ClientSideAnchorsConfig;
import me.cutebow.client_side_anchors.update.ModrinthUpdateManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public final class ClientSideAnchors implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientSideAnchorsConfig.load();
        ModrinthUpdateManager.refresh();

        ClientTickEvents.END_CLIENT_TICK.register(client -> AnchorTracker.clientTick());
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> AnchorTracker.clearAll());
    }
}
