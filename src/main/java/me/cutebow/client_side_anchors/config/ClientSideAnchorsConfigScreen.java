package me.cutebow.client_side_anchors.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClientSideAnchorsConfigScreen {
    public static Screen create(Screen parent) {
        ClientSideAnchorsConfig cfg = ClientSideAnchorsConfig.INSTANCE;

        return YetAnotherConfigLib.createBuilder()
                .title(Text.literal("Anchor Optimizer"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("General"))
                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("Main"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Enable Mod"))
                                        .description(OptionDescription.of(Text.literal("Enable or disable the mod")))
                                        .binding(true, () -> cfg.enabled, v -> cfg.enabled = v)
                                        .controller(BooleanControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Instant hide on right-click"))
                                        .description(OptionDescription.of(Text.literal("Immediately hides a exploded anchor client-side (keeps collision)")))
                                        .binding(true, () -> cfg.instantExplosion, v -> cfg.instantExplosion = v)
                                        .controller(BooleanControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Swing hand on use"))
                                        .description(OptionDescription.of(Text.literal("Play the hand swing animation instantly when you pop an anchor")))
                                        .binding(true, () -> cfg.swingHandOnUse, v -> cfg.swingHandOnUse = v)
                                        .controller(BooleanControllerBuilder::create)
                                        .build())
                                .build())
                        .build())
                .save(ClientSideAnchorsConfig::save)
                .build()
                .generateScreen(parent);
    }
}
