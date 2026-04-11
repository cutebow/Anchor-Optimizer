package me.cutebow.client_side_anchors.mixin;

import me.cutebow.client_side_anchors.RuntimeLatch;
import me.cutebow.client_side_anchors.client.AnchorPredictionManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    private static final String NORTHLINE_INTERACT = "mp7.press.6e42ab10";

    @Inject(method = "interactBlock", at = @At("HEAD"))
    private void client_side_anchors$onInteractBlockHead(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<?> cir) {
        if (!RuntimeLatch.isActive()) {
            return;
        }

        ClientWorld world = MinecraftClient.getInstance().world;
        if (world != null && hitResult != null) {
            AnchorPredictionManager.handleFastInteract(world, hand, hitResult);
        }
    }

    @Inject(method = "interactBlock", at = @At("RETURN"))
    private void client_side_anchors$onInteractBlockReturn(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<?> cir) {
        if (!RuntimeLatch.isActive()) {
            return;
        }

        Object result = cir.getReturnValue();
        if (!client_side_anchors$isAccepted(result)) {
            return;
        }

        ClientWorld world = MinecraftClient.getInstance().world;
        if (world != null && hitResult != null) {
            AnchorPredictionManager.handleInteract(world, hand, hitResult);
        }
    }

    private boolean client_side_anchors$isAccepted(Object result) {
        if (result == null) {
            return false;
        }

        try {
            Method acceptedMethod = result.getClass().getMethod("isAccepted");
            Object accepted = acceptedMethod.invoke(result);
            return accepted instanceof Boolean && (Boolean) accepted;
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }
}
