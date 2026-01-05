package me.cutebow.client_side_anchors.client;

import me.cutebow.client_side_anchors.config.ClientSideAnchorsConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class AnchorPredictionManager {
    private static final Map<BlockPos, Prediction> PREDICTIONS = new HashMap<>();
    private static int tickCounter = 0;

    private AnchorPredictionManager() {}

    public static void handleInteract(ClientWorld world, Hand hand, BlockHitResult hitResult) {
        ClientSideAnchorsConfig cfg = ClientSideAnchorsConfig.INSTANCE;
        if (!cfg.enabled) return;
        if (!cfg.instantExplosion) return;
        if (world == null || hitResult == null) return;

        BlockPos pos = hitResult.getBlockPos();
        if (pos == null || !world.isChunkLoaded(pos)) return;

        if (World.NETHER.equals(world.getRegistryKey())) return;

        BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof RespawnAnchorBlock)) return;

        int charge = state.contains(RespawnAnchorBlock.CHARGES) ? state.get(RespawnAnchorBlock.CHARGES) : 0;
        if (charge <= 0) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        ItemStack held = player != null ? player.getStackInHand(hand) : ItemStack.EMPTY;
        if (held.isOf(Items.GLOWSTONE)) return;

        if (cfg.swingHandOnUse && player != null) {
            player.swingHand(hand);
        }

        applyTemporaryAir(world, pos, state);

        if (cfg.hideGlowstoneRing) {
            hideGlowstoneRing(world, pos);
        }

        if (cfg.hurtCamFromAnchor && player != null) {
            player.animateDamage(2);
        }
    }

    private static void hideGlowstoneRing(ClientWorld world, BlockPos center) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;
                BlockPos p = center.add(dx, 0, dz);
                BlockState s = world.getBlockState(p);
                if (s.isOf(Blocks.GLOWSTONE)) {
                    applyTemporaryAir(world, p, s);
                }
            }
        }
    }

    private static void applyTemporaryAir(ClientWorld world, BlockPos pos, BlockState before) {
        BlockState air = Blocks.AIR.getDefaultState();
        world.setBlockState(pos, air, 0);
        Prediction pred = new Prediction();
        pred.pos = pos.toImmutable();
        pred.before = before;
        pred.after = air;
        pred.createdTick = tickCounter;
        PREDICTIONS.put(pred.pos, pred);
    }

    public static void onServerBlockUpdate(ClientWorld world, BlockPos pos, BlockState state) {
        Prediction pred = PREDICTIONS.get(pos);
        if (pred == null) return;

        if (!state.equals(pred.after)) {
            world.setBlockState(pos, state, 0);
        }
        PREDICTIONS.remove(pos);
    }

    public static boolean isSuppressed(BlockPos pos) {
        Prediction pred = PREDICTIONS.get(pos);
        if (pred == null) return false;
        int age = tickCounter - pred.createdTick;
        return age <= 40;
    }

    public static void tick() {
        tickCounter++;

        if (PREDICTIONS.isEmpty()) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        ClientWorld world = mc.world;
        if (world == null) {
            PREDICTIONS.clear();
            return;
        }

        Iterator<Map.Entry<BlockPos, Prediction>> it = PREDICTIONS.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, Prediction> e = it.next();
            Prediction pred = e.getValue();

            if (pred.pos == null || !world.isChunkLoaded(pred.pos)) {
                it.remove();
                continue;
            }

            if (tickCounter - pred.createdTick > 40) {
                BlockState current = world.getBlockState(pred.pos);
                if (current.equals(pred.after)) {
                    world.setBlockState(pred.pos, pred.before, 0);
                }
                it.remove();
            }
        }
    }

    static final class Prediction {
        BlockPos pos;
        BlockState before;
        BlockState after;
        int createdTick;

        private Prediction() {}
    }
}
