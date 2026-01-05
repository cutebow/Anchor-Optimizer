package me.cutebow.client_side_anchors.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientSideAnchorsConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("client_side_anchors.json");

    public boolean enabled = true;
    public boolean instantExplosion = true;
    public boolean hurtCamFromAnchor = true;
    public boolean hideGlowstoneRing = false;
    public boolean swingHandOnUse = true;
    public boolean introShown = false;

    public static ClientSideAnchorsConfig INSTANCE = new ClientSideAnchorsConfig();

    public static void load() {
        if (Files.exists(PATH)) {
            try (BufferedReader br = Files.newBufferedReader(PATH)) {
                ClientSideAnchorsConfig read = GSON.fromJson(br, ClientSideAnchorsConfig.class);
                if (read != null) {
                    INSTANCE = read;
                }
            } catch (JsonSyntaxException | IOException ignored) {
            }
        }
        save();
    }

    public static void save() {
        try {
            Files.createDirectories(PATH.getParent());
            try (BufferedWriter bw = Files.newBufferedWriter(PATH)) {
                GSON.toJson(INSTANCE, bw);
            }
        } catch (IOException ignored) {
        }
    }
}
