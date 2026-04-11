package me.cutebow.client_side_anchors.mixin;

import me.cutebow.client_side_anchors.RuntimeLatch;
import me.cutebow.client_side_anchors.client.AnchorPredictionManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.class)
public abstract class ClientWorldSetBlockStateMixin {
    private static final String NORTHLINE_WORLD = "tw4.shell.0ac7d2e4";
    private static final Identifier HERO_FAKE_ANCHOR = Identifier.of("herosanchoroptimizer", "fake_anchor");

    @Inject(
            method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void client_side_anchors$filterClientUpdates(BlockPos pos, BlockState state, int flags, int depth, CallbackInfoReturnable<Boolean> cir) {
        if (!RuntimeLatch.isActive()) {
            return;
        }

        if (Registries.BLOCK.getId(state.getBlock()).equals(HERO_FAKE_ANCHOR)) {
            cir.setReturnValue(true);
            return;
        }

        if (AnchorPredictionManager.isSuppressed(pos) && !state.isAir()) {
            cir.setReturnValue(true);
        }
    }
}
