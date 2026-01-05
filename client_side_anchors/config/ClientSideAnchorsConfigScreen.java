package me.cutebow.client_side_anchors.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class ClientSideAnchorsConfigScreen {
    private ClientSideAnchorsConfigScreen() {}

    public static Screen create(Screen parent) {
        ClientSideAnchorsConfig cfg = ClientSideAnchorsConfig.INSTANCE;

        return YetAnotherConfigLib.createBuilder()
                .title(Text.literal("Client Side Anchors"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("General"))
                        .option(boolOpt("Enabled", cfg::enabled, v -> cfg.enabled = v))
                        .option(boolOpt("Instant explosion visuals", cfg::instantExplosion, v -> cfg.instantExplosion = v))
                        .option(boolOpt("Hurt cam feedback", cfg::hurtCamFromAnchor, v -> cfg.hurtCamFromAnchor = v))
                        .option(boolOpt("Hide glowstone ring", cfg::hideGlowstoneRing, v -> cfg.hideGlowstoneRing = v))
                        .option(boolOpt("Swing hand on use", cfg::swingHandOnUse, v -> cfg.swingHandOnUse = v))
                        .build())
                .save(ClientSideAnchorsConfig::save)
                .build()
                .generateScreen(parent);
    }

    private static Option<Boolean> boolOpt(String name, java.util.function.BooleanSupplier getter, java.util.function.Consumer<Boolean> setter) {
        return Option.<Boolean>createBuilder()
                .name(Text.literal(name))
                .description(OptionDescription.of(Text.empty()))
                .binding(getter::getAsBoolean, () -> getter.getAsBoolean(), setter)
                .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true))
                .build();
    }
}
