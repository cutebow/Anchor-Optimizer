package me.cutebow.client_side_anchors.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientSideAnchorsConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("client_side_anchors.json");

    public boolean enabled = true;
    public boolean instantExplosion = true;
    public boolean swingHandOnUse = true;
    public boolean introShown = false;

    public static ClientSideAnchorsConfig INSTANCE = new ClientSideAnchorsConfig();

    public static void load() {
        if (Files.exists(PATH)) {
            try (Reader r = Files.newBufferedReader(PATH)) {
                ClientSideAnchorsConfig c = GSON.fromJson(r, ClientSideAnchorsConfig.class);
                if (c != null) INSTANCE = c;
            } catch (JsonSyntaxException | IOException ignored) {
            }
        }
        save();
    }

    public static void save() {
        try {
            Files.createDirectories(PATH.getParent());
            try (Writer w = Files.newBufferedWriter(PATH)) {
                GSON.toJson(INSTANCE, w);
            }
        } catch (IOException ignored) {
        }
    }
}
