package net.rj.utils.util;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

public class CustomsUtil {
    @Nullable
    public static Biome getBiome(@Nullable ClientWorld world, @Nullable LivingEntity entity) {
        if (world == null || entity == null) return null;
        RegistryEntry<Biome> entry = world.getBiome(entity.getBlockPos());
        return entry != null ? entry.value() : null;
    }


    @Nullable
    public static Biome getBiomeAt(@Nullable ClientWorld world, BlockPos pos) {
        if (world == null || pos == null) return null;
        RegistryEntry<Biome> entry = world.getBiome(pos);
        return entry != null ? entry.value() : null;
    }


    public static float getTemperature(@Nullable ClientWorld world, @Nullable LivingEntity entity) {
        Biome biome = getBiome(world, entity);
        if (biome == null || entity == null) return 0.5f;
        return BackportUtil.getBiomeTemperature(biome, entity.getBlockPos());
    }


    public static float getTemperatureAt(@Nullable ClientWorld world, BlockPos pos) {
        Biome biome = getBiomeAt(world, pos);
        if (biome == null) return 0.5f;
        return BackportUtil.getBiomeTemperature(biome, pos);
    }


    public static boolean isInDimension(@Nullable ClientWorld world, String dimensionId) {
        if (world == null) return false;
        return world.getRegistryKey().getValue().toString().contains(dimensionId);
    }

    @Nullable
    public static Identifier getDimension(@Nullable ClientWorld world) {
        if (world == null) return null;
        return world.getRegistryKey().getValue();
    }


    public static float getHealthPercent(@Nullable LivingEntity entity) {
        if (entity == null) return 1.0f;
        float maxHealth = entity.getMaxHealth();
        if (maxHealth <= 0) return 1.0f;
        return MathUtils.clamp(entity.getHealth() / maxHealth, 0.0f, 1.0f);
    }


    public static float getHealth(@Nullable LivingEntity entity) {
        return entity != null ? entity.getHealth() : 0.0f;
    }


    public static float getMaxHealth(@Nullable LivingEntity entity) {
        return entity != null ? entity.getMaxHealth() : 20.0f;
    }

    public static boolean isLowHealth(@Nullable LivingEntity entity) {
        return getHealthPercent(entity) < 0.3f;
    }

    public static boolean isCriticalHealth(@Nullable LivingEntity entity) {
        return getHealthPercent(entity) < 0.15f;
    }

    public static boolean isFullHealth(@Nullable LivingEntity entity) {
        if (entity == null) return false;
        return entity.getHealth() >= entity.getMaxHealth();
    }

    public static boolean isHealthInRange(@Nullable LivingEntity entity, float min, float max) {
        float health = getHealthPercent(entity);
        return health >= min && health <= max;
    }

    public static float getDamagePercent(ItemStack stack) {
        if (!stack.isDamageable()) return 0.0f;
        if (stack.getMaxDamage() == 0) return 0.0f;
        return MathUtils.clamp((float) stack.getDamage() / stack.getMaxDamage(), 0.0f, 1.0f);
    }

    public static float getDurabilityPercent(ItemStack stack) {
        return 1.0f - getDamagePercent(stack);
    }
    public static boolean isDamaged(ItemStack stack) {
        return stack.isDamaged();
    }
    public static boolean isDamageInRange(ItemStack stack, float min, float max) {
        float damage = getDamagePercent(stack);
        return damage >= min && damage <= max;
    }
    public static boolean isNearlyBroken(ItemStack stack) {
        return getDamagePercent(stack) > 0.9f;
    }
    public static int getCount(ItemStack stack) {
        return stack.getCount();
    }
    public static boolean isFullStack(ItemStack stack) {
        return stack.getCount() >= stack.getMaxCount();
    }
    public static float getTimeNormalized(@Nullable ClientWorld world) {
        if (world == null) return 0.0f;
        return (float) (world.getTimeOfDay() % 24000L) / 24000.0f;
    }
    public static long getTime(@Nullable ClientWorld world) {
        if (world == null) return 0L;
        return world.getTimeOfDay() % 24000L;
    }
    public static boolean isDay(@Nullable ClientWorld world) {
        if (world == null) return true;
        long time = getTime(world);
        return time >= 0 && time < 12000;
    }
    public static boolean isNight(@Nullable ClientWorld world) {
        return !isDay(world);
    }
    public static boolean isTimeInRange(@Nullable ClientWorld world, long min, long max) {
        if (world == null) return false;
        long time = getTime(world);


        if (min > max) {
            return time >= min || time <= max;
        }
        return time >= min && time <= max;
    }
    public static int getMoonPhase(@Nullable ClientWorld world) {
        if (world == null) return 0;
        return world.getMoonPhase();
    }
    public static boolean isFullMoon(@Nullable ClientWorld world) {
        return getMoonPhase(world) == 0;
    }
    public static int getLightLevel(@Nullable ClientWorld world, @Nullable LivingEntity entity) {
        if (world == null || entity == null) return 0;
        return world.getLightLevel(entity.getBlockPos());
    }

    public static float getLightLevelNormalized(@Nullable ClientWorld world, @Nullable LivingEntity entity) {
        return getLightLevel(world, entity) / 15.0f;
    }


    public static boolean isDark(@Nullable ClientWorld world, @Nullable LivingEntity entity) {
        return getLightLevel(world, entity) < 8;
    }

    public static boolean isBright(@Nullable ClientWorld world, @Nullable LivingEntity entity) {
        return getLightLevel(world, entity) > 12;
    }
    public static boolean isRaining(@Nullable ClientWorld world) {
        return world != null && world.isRaining();
    }
    public static boolean isThundering(@Nullable ClientWorld world) {
        return world != null && world.isThundering();
    }
    public static float getRainGradient(@Nullable ClientWorld world) {
        if (world == null) return 0.0f;
        return world.getRainGradient(1.0f);
    }
    public static float getThunderGradient(@Nullable ClientWorld world) {
        if (world == null) return 0.0f;
        return world.getThunderGradient(1.0f);
    }
    public static boolean isInWater(@Nullable LivingEntity entity) {
        return entity != null && entity.isTouchingWater();
    }
    public static boolean isUnderwater(@Nullable LivingEntity entity) {
        return entity != null && entity.isSubmergedInWater();
    }
    public static boolean isOnFire(@Nullable LivingEntity entity) {
        return entity != null && entity.isOnFire();
    }
    public static boolean isSneaking(@Nullable LivingEntity entity) {
        return entity != null && entity.isSneaking();
    }
    public static boolean isSprinting(@Nullable LivingEntity entity) {
        return entity != null && entity.isSprinting();
    }
    public static boolean isSwimming(@Nullable LivingEntity entity) {
        return entity != null && entity.isSwimming();
    }
    public static boolean isGliding(@Nullable LivingEntity entity) {
        return entity != null && entity.isFallFlying();
    }
    public static boolean isOnGround(@Nullable LivingEntity entity) {
        return entity != null && entity.isOnGround();
    }
    public static boolean isInAir(@Nullable LivingEntity entity) {
        return !isOnGround(entity);
    }
    public static boolean isInvisible(@Nullable LivingEntity entity) {
        return entity != null && entity.isInvisible();
    }
    public static double getVerticalVelocity(@Nullable LivingEntity entity) {
        if (entity == null) return 0.0;
        return entity.getVelocity().y;
    }
    public static boolean isFalling(@Nullable LivingEntity entity) {
        return getVerticalVelocity(entity) < -0.1;
    }
    public static boolean isRising(@Nullable LivingEntity entity) {
        return getVerticalVelocity(entity) > 0.1;
    }
    public static int getYLevel(@Nullable LivingEntity entity) {
        if (entity == null) return 0;
        return entity.getBlockPos().getY();
    }
    public static float getYLevelNormalized(@Nullable LivingEntity entity) {
        int y = getYLevel(entity);
        return MathUtils.clamp((y + 64.0f) / 384.0f, 0.0f, 1.0f);
    }
    public static boolean isBelowY(@Nullable LivingEntity entity, int y) {
        return getYLevel(entity) < y;
    }
    public static boolean isAboveY(@Nullable LivingEntity entity, int y) {
        return getYLevel(entity) > y;
    }
    public static boolean isYInRange(@Nullable LivingEntity entity, int min, int max) {
        int y = getYLevel(entity);
        return y >= min && y <= max;
    }
    @Nullable
    public static BlockPos getPosition(@Nullable LivingEntity entity) {
        return entity != null ? entity.getBlockPos() : null;
    }
    public static double getDistanceTo(@Nullable LivingEntity entity, BlockPos pos) {
        if (entity == null || pos == null) return Double.MAX_VALUE;
        return entity.getBlockPos().getSquaredDistance(pos);
    }
    public static boolean isInColdBiome(@Nullable ClientWorld world, @Nullable LivingEntity entity) {
        return getTemperature(world, entity) < 0.2f;
    }
    public static boolean isInHotBiome(@Nullable ClientWorld world, @Nullable LivingEntity entity) {
        return getTemperature(world, entity) > 1.5f;
    }
    public static boolean isInTemperateBiome(@Nullable ClientWorld world, @Nullable LivingEntity entity) {
        float temp = getTemperature(world, entity);
        return temp >= 0.5f && temp <= 1.0f;
    }
    public static boolean biomeHasPrecipitation(@Nullable ClientWorld world, @Nullable LivingEntity entity) {
        Biome biome = getBiome(world, entity);
        if (biome == null || entity == null) return false;
        return biome.getPrecipitation(entity.getBlockPos()) != Biome.Precipitation.NONE;
    }
    public static boolean biomeHasRain(@Nullable ClientWorld world, @Nullable LivingEntity entity) {
        Biome biome = getBiome(world, entity);
        if (biome == null || entity == null) return false;
        return biome.getPrecipitation(entity.getBlockPos()) == Biome.Precipitation.RAIN;
    }
    public static boolean biomeHasSnow(@Nullable ClientWorld world, @Nullable LivingEntity entity) {
        Biome biome = getBiome(world, entity);
        if (biome == null || entity == null) return false;
        return biome.getPrecipitation(entity.getBlockPos()) == Biome.Precipitation.SNOW;
    }
    public static float boolToFloat(boolean value) {
        return value ? 1.0f : 0.0f;
    }

    public static float normalizeInt(int value, int max) {
        return MathUtils.clamp((float) value / max, 0.0f, 1.0f);
    }


    public static float invert(float value) {
        return 1.0f - MathUtils.clamp(value, 0.0f, 1.0f);
    }


    public static float smoothstep(float value) {
        float clamped = MathUtils.clamp(value, 0.0f, 1.0f);
        return clamped * clamped * (3.0f - 2.0f * clamped);
    }


    public static float lerp(float start, float end, float t) {
        return start + (end - start) * MathUtils.clamp(t, 0.0f, 1.0f);
    }


    public static float map(float value, float inMin, float inMax, float outMin, float outMax) {
        float normalized = (value - inMin) / (inMax - inMin);
        return lerp(outMin, outMax, normalized);
    }
    public static float pulse(float value) {
        float clamped = MathUtils.clamp(value, 0.0f, 1.0f);
        return clamped < 0.5f ? clamped * 2.0f : (1.0f - clamped) * 2.0f;
    }

    public static boolean isInStealth(@Nullable ClientWorld world, @Nullable LivingEntity entity) {
        return isSneaking(entity) && isDark(world, entity);
    }

    public static boolean isInDanger(@Nullable LivingEntity entity) {
        return isLowHealth(entity) || isOnFire(entity);
    }

    public static boolean isPerfectCombatCondition(@Nullable ClientWorld world, @Nullable LivingEntity entity) {
        return isFullHealth(entity) && isDay(world) && !isOnFire(entity);
    }
    public static boolean isInDeepOcean(@Nullable ClientWorld world, @Nullable LivingEntity entity) {
        return isUnderwater(entity) && isDark(world, entity);
    }
}