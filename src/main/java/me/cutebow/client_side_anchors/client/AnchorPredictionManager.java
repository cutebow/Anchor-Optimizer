package me.cutebow.client_side_anchors.client;

import me.cutebow.client_side_anchors.compat.CrystalAnchorCounterCompat;
import me.cutebow.client_side_anchors.config.ClientSideAnchorsConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AnchorPredictionManager {
    private static final Map<BlockPos, Prediction> PREDICTIONS = new HashMap<>();
    private static int tickCounter = 0;

    public static void handleInteract(ClientWorld world, Hand hand, BlockHitResult hitResult) {
        ClientSideAnchorsConfig cfg = ClientSideAnchorsConfig.INSTANCE;
        if (!cfg.enabled) return;
        if (!cfg.instantExplosion) return;
        if (world == null || hitResult == null) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        if (mc.player.isSneaking()) return;

        BlockPos pos = hitResult.getBlockPos();
        if (pos == null || !world.isChunkLoaded(pos)) return;
        if (world.getRegistryKey().equals(World.NETHER)) return;

        BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof RespawnAnchorBlock)) return;

        int charges = state.contains(RespawnAnchorBlock.CHARGES) ? state.get(RespawnAnchorBlock.CHARGES) : 0;
        if (charges <= 0) return;

        ItemStack stack = mc.player.getStackInHand(hand);
        if (stack.isOf(Items.GLOWSTONE)) return;

        if (cfg.swingHandOnUse) {
            mc.player.swingHand(hand);
        }

        beginPrediction(world, pos, state);
        CrystalAnchorCounterCompat.recordAnchorExplosion(pos);
    }

    private static void beginPrediction(ClientWorld world, BlockPos pos, BlockState before) {
        BlockPos key = pos.toImmutable();
        BlockState predicted = Blocks.AIR.getDefaultState();

        world.setBlockState(key, predicted, 0);

        Prediction p = new Prediction();
        p.pos = key;
        p.before = before;
        p.after = predicted;
        p.createdTick = tickCounter;
        PREDICTIONS.put(key, p);

        scheduleRerender(key);
    }

    public static void onServerBlockUpdate(ClientWorld world, BlockPos pos, BlockState newState) {
        if (world == null || pos == null) return;

        Prediction p = PREDICTIONS.get(pos);
        if (p == null) return;

        if (newState.equals(p.after) || newState.isAir()) {
            PREDICTIONS.remove(pos);
            scheduleRerender(pos);
            return;
        }

        p.lastServerState = newState;

        BlockState current = world.getBlockState(pos);
        if (!current.equals(p.after)) {
            world.setBlockState(pos, p.after, 0);
            scheduleRerender(pos);
        }
    }

    public static boolean isSuppressed(BlockPos pos) {
        Prediction p = PREDICTIONS.get(pos);
        if (p == null) return false;
        int age = tickCounter - p.createdTick;
        return age <= 40;
    }

    public static BlockState getBefore(BlockPos pos) {
        Prediction p = PREDICTIONS.get(pos);
        if (p == null) return null;
        return p.before;
    }

    public static void tick() {
        tickCounter++;
        if (PREDICTIONS.isEmpty()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;
        if (world == null) {
            PREDICTIONS.clear();
            return;
        }

        Iterator<Map.Entry<BlockPos, Prediction>> it = PREDICTIONS.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, Prediction> e = it.next();
            Prediction p = e.getValue();
            if (p.pos == null || !world.isChunkLoaded(p.pos)) {
                it.remove();
                continue;
            }

            if (tickCounter - p.createdTick > 40) {
                it.remove();

                BlockState target = p.lastServerState != null ? p.lastServerState : p.before;
                if (target != null) {
                    BlockState current = world.getBlockState(p.pos);
                    if (current.equals(p.after)) {
                        world.setBlockState(p.pos, target, 0);
                    }
                }

                scheduleRerender(p.pos);
            }
        }
    }

    private static void scheduleRerender(BlockPos pos) {
        MinecraftClient mc = MinecraftClient.getInstance();
        WorldRenderer wr = mc.worldRenderer;
        if (wr == null) return;
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        wr.scheduleBlockRenders(x, y, z, x, y, z);
    }

    private static class Prediction {
        BlockPos pos;
        BlockState before;
        BlockState after;
        BlockState lastServerState;
        int createdTick;
    }
}
