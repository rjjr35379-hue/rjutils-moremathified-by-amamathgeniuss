package net.rj.utils.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;


/**
 * Vanilla-like model predicate provider system
 * Works with resource packs just like vanilla predicates (pulling, damage, etc.)
 *
 * Register predicates in your mod initializer:
 * ModelPredicateProvider.register();
 */
@Environment(EnvType.CLIENT)
public class ModelPredicateProvider {

    /**
     * Registers all custom model predicates
     * Call this in your ClientModInitializer
     */
    public static void register() {
        registerGlobalPredicates();
    }

    /**
     * Registers global predicates that work on all items
     */
    private static void registerGlobalPredicates() {
        // Health-based predicates
        registerGlobal("player_health", (stack, world, entity, seed) -> {
            if (entity == null) return 0.0f;
            float maxHealth = entity.getMaxHealth();
            if (maxHealth <= 0) return 0.0f;
            return MathUtils.clamp(entity.getHealth() / maxHealth, 0.0f, 1.0f);
        });

        registerGlobal("player_health_low", (stack, world, entity, seed) -> {
            if (entity == null) return 0.0f;
            float percent = entity.getHealth() / entity.getMaxHealth();
            return percent < 0.3f ? 1.0f : 0.0f;
        });

        registerGlobal("player_health_critical", (stack, world, entity, seed) -> {
            if (entity == null) return 0.0f;
            float percent = entity.getHealth() / entity.getMaxHealth();
            return percent < 0.15f ? 1.0f : 0.0f;
        });

        // Biome predicates
        registerGlobal("biome_temperature", (stack, world, entity, seed) -> {
            if (world == null || entity == null) return 0.5f;
            Biome biome = getBiome(world, entity);
            if (biome == null) return 0.5f;
            return MathUtils.clamp(BackportUtil.getBiomeTemperature(biome, entity.getBlockPos()), 0.0f, 2.0f) / 2.0f;
        });

        registerGlobal("biome_icy", (stack, world, entity, seed) -> {
            if (world == null || entity == null) return 0.0f;
            Biome biome = getBiome(world, entity);
            if (biome == null) return 0.0f;
            float temp = BackportUtil.getBiomeTemperature(biome, entity.getBlockPos());
            return temp < 0.2f ? 1.0f : 0.0f;
        });

        registerGlobal("biome_desert", (stack, world, entity, seed) -> {
            if (world == null || entity == null) return 0.0f;
            Biome biome = getBiome(world, entity);
            if (biome == null) return 0.0f;
            float temp = BackportUtil.getBiomeTemperature(biome, entity.getBlockPos());
            boolean hasRain = biome.getPrecipitation(entity.getBlockPos()) != Biome.Precipitation.NONE;
            return (temp > 1.5f && !hasRain) ? 1.0f : 0.0f;
        });

        registerGlobal("biome_jungle", (stack, world, entity, seed) -> {
            if (world == null || entity == null) return 0.0f;
            Biome biome = getBiome(world, entity);
            if (biome == null) return 0.0f;
            float temp = BackportUtil.getBiomeTemperature(biome, entity.getBlockPos());
            boolean hasRain = biome.getPrecipitation(entity.getBlockPos()) != Biome.Precipitation.NONE;
            return (temp > 0.95f && hasRain) ? 1.0f : 0.0f;
        });

        registerGlobal("biome_taiga", (stack, world, entity, seed) -> {
            if (world == null || entity == null) return 0.0f;
            Biome biome = getBiome(world, entity);
            if (biome == null) return 0.0f;
            float temp = BackportUtil.getBiomeTemperature(biome, entity.getBlockPos());
            boolean hasRain = biome.getPrecipitation(entity.getBlockPos()) != Biome.Precipitation.NONE;
            return (temp >= 0.2f && temp < 0.5f && hasRain) ? 1.0f : 0.0f;
        });

        // Environmental predicates
        registerGlobal("in_water", (stack, world, entity, seed) ->
                entity != null && entity.isTouchingWater() ? 1.0f : 0.0f
        );

        registerGlobal("on_fire", (stack, world, entity, seed) ->
                entity != null && entity.isOnFire() ? 1.0f : 0.0f
        );

        registerGlobal("raining", (stack, world, entity, seed) ->
                world != null && world.isRaining() ? 1.0f : 0.0f
        );

        // Time predicates
        registerGlobal("time", (stack, world, entity, seed) -> {
            if (world == null) return 0.0f;
            return (float) (world.getTimeOfDay() % 24000L) / 24000.0f;
        });

        registerGlobal("day_time", (stack, world, entity, seed) -> {
            if (world == null) return 0.0f;
            long time = world.getTimeOfDay() % 24000L;
            return (time >= 0 && time < 12000) ? 1.0f : 0.0f;
        });

        registerGlobal("night_time", (stack, world, entity, seed) -> {
            if (world == null) return 0.0f;
            long time = world.getTimeOfDay() % 24000L;
            return (time >= 12000 && time < 24000) ? 1.0f : 0.0f;
        });

        // Entity state predicates
        registerGlobal("sneaking", (stack, world, entity, seed) ->
                entity != null && entity.isSneaking() ? 1.0f : 0.0f
        );

        registerGlobal("sprinting", (stack, world, entity, seed) ->
                entity != null && entity.isSprinting() ? 1.0f : 0.0f
        );

        // Location predicates
        registerGlobal("y_level", (stack, world, entity, seed) -> {
            if (entity == null) return 0.0f;
            int y = entity.getBlockPos().getY();
            // Normalize to 0-1 range (assuming -64 to 320 range)
            return MathUtils.clamp((y + 64.0f) / 384.0f, 0.0f, 1.0f);
        });

        registerGlobal("light_level", (stack, world, entity, seed) -> {
            if (world == null || entity == null) return 0.0f;
            int light = world.getLightLevel(entity.getBlockPos());
            return light / 15.0f;
        });

        registerGlobal("underwater", (stack, world, entity, seed) ->
                entity != null && entity.isSubmergedInWater() ? 1.0f : 0.0f
        );

        // Dimension predicates
        registerGlobal("in_nether", (stack, world, entity, seed) -> {
            if (world == null) return 0.0f;
            return world.getRegistryKey().getValue().toString().contains("nether") ? 1.0f : 0.0f;
        });

        registerGlobal("in_end", (stack, world, entity, seed) -> {
            if (world == null) return 0.0f;
            return world.getRegistryKey().getValue().toString().contains("the_end") ? 1.0f : 0.0f;
        });
    }

    /**
     * Registers a global predicate that works on all items
     */
    public static void registerGlobal(String id, ClampedModelPredicateProvider provider) {
        ModelPredicateProviderRegistry.register(new Identifier("modefite", id), provider);
    }

    /**
     * Registers a predicate for a specific item
     */
    public static void register(Item item, String id, ClampedModelPredicateProvider provider) {
        ModelPredicateProviderRegistry.register(item, new Identifier("modefite", id), provider);
    }

    /**
     * Registers a predicate for a specific item by identifier
     */
    public static void register(Identifier itemId, String predicateId, ClampedModelPredicateProvider provider) {
        Item item = Registries.ITEM.get(itemId);
        if (item != null) {
            register(item, predicateId, provider);
        }
    }

    // Helper methods

    @Nullable
    private static Biome getBiome(@Nullable ClientWorld world, @Nullable LivingEntity entity) {
        if (world == null || entity == null) return null;
        RegistryEntry<Biome> entry = world.getBiome(entity.getBlockPos());
        return entry != null ? entry.value() : null;
    }
}