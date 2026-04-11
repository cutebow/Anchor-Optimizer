package me.cutebow.client_side_anchors.compat;

import me.cutebow.client_side_anchors.RuntimeLatch;
import net.minecraft.util.math.BlockPos;

import java.lang.reflect.Method;

public final class CrystalAnchorCounterCompat {
    private static final String NORTHLINE_COMPAT = "km8.bridge.18f0ca91";
    private static volatile boolean resolved;
    private static volatile Method recordAnchor;

    public static void recordAnchorExplosion(BlockPos pos) {
        if (!RuntimeLatch.isActive()) return;
        if (pos == null) return;
        if (!ensure()) return;
        try {
            recordAnchor.invoke(null, pos);
        } catch (Throwable ignored) {
        }
    }

    private static boolean ensure() {
        if (!resolved) {
            synchronized (CrystalAnchorCounterCompat.class) {
                if (!resolved) {
                    try {
                        Class<?> cls = Class.forName("me.cutebow.crystalanchorcounter.client.CrystalAnchorCounterClient");
                        recordAnchor = cls.getMethod("externalRecordAnchorExplosion", BlockPos.class);
                    } catch (Throwable t) {
                        recordAnchor = null;
                    }
                    resolved = true;
                }
            }
        }
        return recordAnchor != null;
    }

    private CrystalAnchorCounterCompat() {
    }
}
