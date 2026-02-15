package me.cutebow.client_side_anchors.mixin;

import me.cutebow.client_side_anchors.client.AnchorPredictionManager;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class BlockStateCollisionMixin { // this class is for keeping the anchor block collision.

    @Inject(
            method = "getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void client_side_anchors$restoreCollisionWhileAir(BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        BlockState self = (BlockState) (Object) this;
        if (!self.isAir()) return;
        if (!AnchorPredictionManager.isSuppressed(pos)) return;

        BlockState before = AnchorPredictionManager.getBefore(pos);
        if (before == null) return;

        cir.setReturnValue(before.getCollisionShape(world, pos, context));
    }
}
