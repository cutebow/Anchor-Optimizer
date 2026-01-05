package me.cutebow.client_side_anchors.mixin;

import me.cutebow.client_side_anchors.client.AnchorPredictionManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onBlockUpdate", at = @At("TAIL"))
    private void client_side_anchors$onBlockUpdate(BlockUpdateS2CPacket packet, CallbackInfo ci) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world != null) {
            AnchorPredictionManager.onServerBlockUpdate(world, packet.getPos(), packet.getState());
        }
    }

    @Inject(method = "onChunkDeltaUpdate", at = @At("TAIL"))
    private void client_side_anchors$onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket packet, CallbackInfo ci) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world != null) {
            packet.visitUpdates((BlockPos pos, BlockState state) -> AnchorPredictionManager.onServerBlockUpdate(world, pos, state));
        }
    }
}
