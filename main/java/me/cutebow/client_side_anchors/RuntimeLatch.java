package me.cutebow.client_side_anchors;

import org.slf4j.Logger;              // This class is an anti-tamper check. It is not part of gameplay logic by itself
import org.slf4j.LoggerFactory;       // The LOGGER here is just a normal local mod logger from SLF4J. In this class it is only used to print one warning line if the check fails: northline absent: n7. It is not sending anything anywhere
                                      // This class only imports local IO, class resource reading, jar reading, and manifest reading. There are no HTTP, socket, web, or auth imports in it. not doing network stuff
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.CodeSource;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public final class RuntimeLatch {
    private static final Logger LOGGER = LoggerFactory.getLogger("client_side_anchors");
    private static final String SELF_MARK_A = "qv6.northline.7f4e81c2";
    private static final String SELF_MARK_B = "mk4.ember.c2a91d3b";
    private static final String CLIENT_MARK = "hn4.rail.a1d0fe72";
    private static final String PREDICTION_MARK_A = "rx2.fld.41b8d3e0";
    private static final String PREDICTION_MARK_B = "rx2.fold.19c7ae44";
    private static final String COMPAT_MARK = "km8.bridge.18f0ca91";
    private static final String COLLISION_MARK = "sg3.hollow.80e1bc22";
    private static final String NETWORK_MARK = "ln2.delta.c85f9aa1";
    private static final String INTERACT_MARK = "mp7.press.6e42ab10";
    private static final String WORLD_MARK = "tw4.shell.0ac7d2e4";
    private static final String MANIFEST_KEY_A = "Anchor-Node";
    private static final String MANIFEST_KEY_B = "Anchor-Seal";
    private static final String MANIFEST_KEY_C = "Anchor-Mesh";
    private static final String MANIFEST_VALUE_A = "n7-a7f9b2c1";
    private static final String MANIFEST_VALUE_B = "s4-c2a91d3b";
    private static final String MANIFEST_VALUE_C = "m3-4d1e8aa6";
    private static volatile boolean checked;
    private static volatile boolean valid;
    private static volatile boolean logged;

    private RuntimeLatch() {
    }

    public static boolean isActive() {
        if (!checked) {
            verify();
        }
        return valid;
    }

    private static synchronized void verify() {
        if (checked) {
            return;
        }

        boolean bytecodeMarksPresent = hasBytecodeMarks();
        boolean manifestMarksPresent = hasManifestMarksIfJar();
        valid = bytecodeMarksPresent && manifestMarksPresent;
        checked = true;

        if (!valid && !logged) {
            logged = true;
            LOGGER.warn("northline absent: n7");
        }
    }

    private static boolean hasBytecodeMarks() {
        return containsClassMarks("/me/cutebow/client_side_anchors/RuntimeLatch.class", SELF_MARK_A, SELF_MARK_B)
            && containsClassMarks("/me/cutebow/client_side_anchors/ClientSideAnchors.class", CLIENT_MARK)
            && containsClassMarks("/me/cutebow/client_side_anchors/client/AnchorPredictionManager.class", PREDICTION_MARK_A, PREDICTION_MARK_B)
            && containsClassMarks("/me/cutebow/client_side_anchors/compat/CrystalAnchorCounterCompat.class", COMPAT_MARK)
            && containsClassMarks("/me/cutebow/client_side_anchors/mixin/BlockStateCollisionMixin.class", COLLISION_MARK)
            && containsClassMarks("/me/cutebow/client_side_anchors/mixin/ClientPlayNetworkHandlerMixin.class", NETWORK_MARK)
            && containsClassMarks("/me/cutebow/client_side_anchors/mixin/ClientPlayerInteractionManagerMixin.class", INTERACT_MARK)
            && containsClassMarks("/me/cutebow/client_side_anchors/mixin/ClientWorldSetBlockStateMixin.class", WORLD_MARK);
    }

    private static boolean containsClassMarks(String resourceName, String... marks) {
        byte[] classBytes = readClassBytes(resourceName);
        if (classBytes == null || classBytes.length == 0) {
            return false;
        }

        for (String mark : marks) {
            if (!contains(classBytes, mark.getBytes(StandardCharsets.UTF_8))) {
                return false;
            }
        }

        return true;
    }

    private static boolean hasManifestMarksIfJar() {
        Path jarPath = getJarPath();
        if (jarPath == null) {
            return true;
        }

        try (JarFile jarFile = new JarFile(jarPath.toFile())) {
            Manifest manifest = jarFile.getManifest();
            if (manifest == null) {
                return false;
            }

            Attributes attributes = manifest.getMainAttributes();
            if (attributes == null) {
                return false;
            }

            return MANIFEST_VALUE_A.equals(attributes.getValue(MANIFEST_KEY_A))
                && MANIFEST_VALUE_B.equals(attributes.getValue(MANIFEST_KEY_B))
                && MANIFEST_VALUE_C.equals(attributes.getValue(MANIFEST_KEY_C));
        } catch (IOException ignored) {
            return false;
        }
    }

    private static Path getJarPath() {
        try {
            CodeSource source = RuntimeLatch.class.getProtectionDomain().getCodeSource();
            if (source == null) {
                return null;
            }

            URL location = source.getLocation();
            if (location == null || !"file".equalsIgnoreCase(location.getProtocol())) {
                return null;
            }

            Path path = Path.of(location.toURI());
            if (!path.toString().endsWith(".jar")) {
                return null;
            }

            return path;
        } catch (URISyntaxException | IllegalArgumentException ignored) {
            return null;
        }
    }

    private static byte[] readClassBytes(String resourceName) {
        try (InputStream inputStream = RuntimeLatch.class.getResourceAsStream(resourceName)) {
            if (inputStream == null) {
                return null;
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) >= 0) {
                outputStream.write(buffer, 0, read);
            }
            return outputStream.toByteArray();
        } catch (IOException ignored) {
            return null;
        }
    }

    private static boolean contains(byte[] haystack, byte[] needle) {
        if (needle.length == 0 || haystack.length < needle.length) {
            return false;
        }

        for (int i = 0; i <= haystack.length - needle.length; i++) {
            boolean match = true;
            for (int j = 0; j < needle.length; j++) {
                if (haystack[i + j] != needle[j]) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return true;
            }
        }

        return false;
    }
}
