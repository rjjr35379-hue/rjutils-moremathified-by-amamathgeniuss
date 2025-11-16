package net.rj.utils.util;

import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class BackportUtil {

    public interface SimpleResourceReloadListener extends SimpleSynchronousResourceReloadListener {

    }
    public enum ModelDisplayMode {
        NONE,
        THIRD_PERSON_LEFT_HAND,
        THIRD_PERSON_RIGHT_HAND,
        FIRST_PERSON_LEFT_HAND,
        FIRST_PERSON_RIGHT_HAND,
        HEAD,
        GUI,
        GROUND,
        FIXED;


        public static ModelDisplayMode fromContext() {

            return NONE;
        }

        public boolean isFirstPerson() {
            return this == FIRST_PERSON_LEFT_HAND || this == FIRST_PERSON_RIGHT_HAND;
        }

        public boolean isGui() {
            return this == GUI;
        }
        // too lazy to add the rest
    }
    public static boolean isVersion(String version) {
        try {
            String gameVersion = net.minecraft.SharedConstants.getGameVersion().getName();
            return gameVersion.startsWith(version);
        } catch (Exception e) {
            return false;
        }
    }
    public static float getBiomeTemperature(net.minecraft.world.biome.Biome biome, net.minecraft.util.math.BlockPos pos) {
        try {

            float baseTemp = biome.getTemperature();


            if (baseTemp < 0.95f && pos != null) {

                int y = pos.getY();
                if (y > 64) {
                    float tempDecrease = (y - 64) * 0.00166667f;
                    baseTemp = Math.max(baseTemp - tempDecrease, 0.0f);
                }
            }

            return baseTemp;
        } catch (Exception e) {

            return 0.5f;
        }
    }
    public static float getBiomeBaseTemperature(net.minecraft.world.biome.Biome biome) {
        try {
            return biome.getTemperature();
        } catch (Exception e) {
            return 0.5f;
        }
    }
}