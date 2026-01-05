package me.cutebow.client_side_anchors.mixin;

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
    private static final Identifier HERO_FAKE_ANCHOR = Identifier.of("herosanchoroptimizer", "fake_anchor");

    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z", at = @At("HEAD"), cancellable = true)
    private void client_side_anchors$holdAir(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
        Identifier id = Registries.BLOCK.getId(state.getBlock());
        if (HERO_FAKE_ANCHOR.equals(id)) {
            cir.setReturnValue(true);
            return;
        }

        if (AnchorPredictionManager.isSuppressed(pos) && !state.isAir()) {
            cir.setReturnValue(true);
        }
    }
}
