package net.rj.utils.util;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import java.lang.reflect.Field;
public class MathUtils {
    private static Field dimensionField;
    private static Field posField;
    private static boolean fieldsInitialized = false;

    private static void initializeFields() {
        if (fieldsInitialized) return;

        try {
            // Try to get the dimesinos
            dimensionField = GlobalPos.class.getDeclaredField("dimension");
            dimensionField.setAccessible(true);

            // Try to get the pos
            posField = GlobalPos.class.getDeclaredField("pos");
            posField.setAccessible(true);

            fieldsInitialized = true;
        } catch (NoSuchFieldException e) {
            // if don't work try diffrent mappings
            try {
                // try mcp
                dimensionField = GlobalPos.class.getDeclaredField("field_25064");
                dimensionField.setAccessible(true);

                posField = GlobalPos.class.getDeclaredField("field_25065");
                posField.setAccessible(true);

                fieldsInitialized = true;
            } catch (NoSuchFieldException ex) {
                System.err.println("Failed to find GlobalPos");
                ex.printStackTrace();
            }
        }

    }

    @Nullable //utility methods for global spos getpos returns blockpos from a globalpos if no then null
    public static BlockPos getPos(@Nullable GlobalPos globalPos) {
        if (globalPos == null) return null;

        initializeFields();

        if (posField == null) return null;

        try {
            return (BlockPos) posField.get(globalPos);
        } catch (IllegalAccessException e) {
            System.err.println("Failed to access GlobalPos.pos");
            e.printStackTrace();
            return null;
        }
    }

    @Nullable // returns dimesinpon RegistryKey<World> of a Globalpos or null if none
    @SuppressWarnings("unchecked")
    public static RegistryKey<World> getDimension(@Nullable GlobalPos globalPos) {
        if (globalPos == null) return null;

        initializeFields();

        if (dimensionField == null) return null;

        try {
            return (RegistryKey<World>) dimensionField.get(globalPos);
        } catch (IllegalAccessException e) {
            System.err.println("Failed to access GlobalPosdimension");
            e.printStackTrace();
            return null;
        }
        //checks if two positions are in the same positino/diemension

    }

    public static boolean isSameDimension(@Nullable GlobalPos pos1, @Nullable GlobalPos pos2, @Nullable RegistryKey<World> dimension2) {
        if (pos1 == null) return false;

        RegistryKey<World> dim1 = getDimension(pos1);
        RegistryKey<World> dim2 = pos2 != null ? getDimension(pos2) : dimension2;

        return dim1 != null && dim1.equals(dim2);
    }

    public static boolean isValid(@Nullable GlobalPos globalPos) {
        return globalPos != null && getDimension(globalPos) != null && getPos(globalPos) != null;
    }
    // i used reflection to get acces to mathpos since idk but minecraft said it's private :(




    //MATH UTILS VERY EXTRA
    public static final float PI = (float) Math.PI;
    public static final float TWO_PI = PI * 2.0f;
    public static final float HALF_PI = PI * 0.5f;
    public static final float QUARTER_PI = PI * 0.25f;
    public static final float DEG_TO_RAD = PI / 180.0f;
    public static final float RAD_TO_DEG = 180.0f / PI;
    public static final float EPSILON = 1e-6f;
    public static final float GOLDEN_RATIO = 1.618033988749895f;

  //clamps
  //since Java 21 added Math.Clamp; compat with java 17
  public static float clamp(float value, float min, float max) {
      return Math.max(min, Math.min(value, max));
  }
//clamps double
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }

    //clamps int value betwen max and min
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    //clampsvalue 0-1
    public static float clamp01(float value) {
        return clamp(value, 0.0f, 1.0f);
    }
    //wraps
    public static float wrap(float value, float max) {
        return ((value % max) + max) % max;
    }
    //wraps value to range
    public static float wrap(float value, float min, float max) {
        float range = max - min;
        return min + wrap(value - min, range);
    }

    //check value is ij range(min max)
    public static boolean inRange(float value, float min, float max) {
        return value >= min && value <= max;
    }
    //checks if value is z0
    public static boolean isZero(float value) {
        return Math.abs(value) < EPSILON;
    }
    //check if two floar euqal
    public static boolean approximately(float a, float b) {
        return Math.abs(a - b) < EPSILON;
    }
    //interpolation
    //linear
    public static float lerp(float a, float b, float t) {
        return a + (b - a) * clamp01(t);
    }

    //unclamped
    public static float lerpUnclamped(float a, float b, float t) {
        return a + (b - a) * t;
    }
    public static float inverseLerp(float a, float b, float value) {
        if (approximately(a, b)) return 0.0f;
        return clamp01((value - a) / (b - a));
    }
    public static float smoothstep(float edge0, float edge1, float x) {
        float t = clamp01((x - edge0) / (edge1 - edge0));
        return t * t * (3.0f - 2.0f * t);
    }
    public static float smootherstep(float edge0, float edge1, float x) {
        float t = clamp01((x - edge0) / (edge1 - edge0));
        return t * t * t * (t * (t * 6.0f - 15.0f) + 10.0f);
    }
    public static float cosineInterp(float a, float b, float t) {
        float cos_t = (1.0f - (float) Math.cos(t * PI)) * 0.5f;
        return lerp(a, b, cos_t);
    }
    public static float easeInExpo(float t) {
        return isZero(t) ? 0.0f : (float) Math.pow(2.0, 10.0 * (t - 1.0));
    }
    public static float easeOutExpo(float t) {
        return approximately(t, 1.0f) ? 1.0f : 1.0f - (float) Math.pow(2.0, -10.0 * t);
    }
    public static float easeInOutExpo(float t) {
        if (isZero(t)) return 0.0f;
        if (approximately(t, 1.0f)) return 1.0f;

        if (t < 0.5f) {
            return (float) Math.pow(2.0, 20.0 * t - 10.0) * 0.5f;
        } else {
            return (2.0f - (float) Math.pow(2.0, -20.0 * t + 10.0)) * 0.5f;
        }
    }
    public static float map(float value, float inMin, float inMax, float outMin, float outMax) {
        return outMin + (value - inMin) * (outMax - outMin) / (inMax - inMin);
    }
    public static float mapClamped(float value, float inMin, float inMax, float outMin, float outMax) {
        float mapped = map(value, inMin, inMax, outMin, outMax);
        return outMin < outMax ? clamp(mapped, outMin, outMax) : clamp(mapped, outMax, outMin);
    }
    public static float normalize(float value, float min, float max) {
        return map(value, min, max, 0.0f, 1.0f);
    }
    public static float toRadians(float degrees) {
        return degrees * DEG_TO_RAD;
    }
    public static float toDegrees(float radians) {
        return radians * RAD_TO_DEG;
    }
    public static float sin01(float t) {
        return (float) Math.sin(t) * 0.5f + 0.5f;
    }
    public static float cos01(float t) {
        return (float) Math.cos(t) * 0.5f + 0.5f;
    }
    public static float pingPong(float t, float length) {
        t = wrap(t, length * 2.0f);
        return length - Math.abs(t - length);
    }
    public static float triangle(float t) {
        return Math.abs(2.0f * (t - (float) Math.floor(t + 0.5f)));
    }
    public static float square(float t) {
        return Math.sin(t) >= 0 ? 1.0f : 0.0f;
    }
    public static float sawtooth(float t) {
        return 2.0f * (t - (float) Math.floor(t + 0.5f));
    }
    public static double distance(BlockPos a, BlockPos b) {
        return Math.sqrt(a.getSquaredDistance(b));
    }
    public static int manhattanDistance(BlockPos a, BlockPos b) {
        return Math.abs(a.getX() - b.getX()) +
                Math.abs(a.getY() - b.getY()) +
                Math.abs(a.getZ() - b.getZ());
    }
    public static int chebyshevDistance(BlockPos a, BlockPos b) {
        return Math.max(
                Math.max(Math.abs(a.getX() - b.getX()), Math.abs(a.getY() - b.getY())),
                Math.abs(a.getZ() - b.getZ())
        );
    }
    public static double distance(Vec3d a, Vec3d b) {
        return a.distanceTo(b);
    }
    public static double distanceSquared(Vec3d a, Vec3d b) {
        return a.squaredDistanceTo(b);
    }
    public static boolean isWithinRadius(BlockPos pos, BlockPos center, double radius) {
        return pos.getSquaredDistance(center) <= radius * radius;
    }
    public static float angleBetween(float x1, float y1, float x2, float y2) {
        return (float) Math.atan2(y2 - y1, x2 - x1);
    }
    public static int round(float value) {
        return Math.round(value);
    }
    public static int ceil(float value) {
        return (int) Math.ceil(value);
    }
    public static int floor(float value) {
        return (int) Math.floor(value);
    }
    public static float roundToNearest(float value, float multiple) {
        return Math.round(value / multiple) * multiple;
    }
    public static float snapToGrid(float value, float gridSize) {
        return Math.round(value / gridSize) * gridSize;
    }
    public static float roundToDecimals(float value, int decimals) {
        float multiplier = (float) Math.pow(10, decimals);
        return Math.round(value * multiplier) / multiplier;
    }
    public static float min(float a, float b) {
        return Math.min(a, b);
    }
    public static float max(float a, float b) {
        return Math.max(a, b);
    }

    public static float min(float a, float b, float c) {
        return Math.min(Math.min(a, b), c);
    }
    public static float max(float a, float b, float c) {
        return Math.max(Math.max(a, b), c);
    }
    public static float abs(float value) {
        return Math.abs(value);
    }
    public static int sign(float value) {
        if (value > EPSILON) return 1;
        if (value < -EPSILON) return -1;
        return 0;
    }
    public static float square2(float value) {
        return value * value;
    }
    public static float cube(float value) {
        return value * value * value;
    }
    public static float pow(float base, float exponent) {
        return (float) Math.pow(base, exponent);
    }
    public static float sqrt(float value) {
        return (float) Math.sqrt(value);
    }
    public static float invSqrt(float value) {
        return 1.0f / (float) Math.sqrt(value);
    }
    public static float log2(float value) {
        return (float) (Math.log(value) / Math.log(2));
    }
    public static float log(float value) {
        return (float) Math.log(value);
    }
    public static float exp(float value) {
        return (float) Math.exp(value);
    }
    public static float mod(float a, float b) {
        return ((a % b) + b) % b;
    }
    public static float fract(float value) {
        return value - (float) Math.floor(value);
    }
    public static float percentage(float value, float total) {
        if (isZero(total)) return 0.0f;
        return (value / total) * 100.0f;
    }
    public static float fromPercentage(float percentage, float total) {
        return (percentage / 100.0f) * total;
    }

    public static float random(int seed) {
        seed = (seed ^ 61) ^ (seed >> 16);
        seed = seed + (seed << 3);
        seed = seed ^ (seed >> 4);
        seed = seed * 0x27d4eb2d;
        seed = seed ^ (seed >> 15);
        return (seed & 0x7FFFFFFF) / (float) 0x7FFFFFFF;
    }

    public static float random(int seed, float min, float max) {
        return lerp(min, max, random(seed));
    }

    public static int hash(int x, int y, int z) {
        int hash = x;
        hash = hash * 31 + y;
        hash = hash * 31 + z;
        hash = ((hash >> 16) ^ hash) * 0x45d9f3b;
        hash = ((hash >> 16) ^ hash) * 0x45d9f3b;
        hash = (hash >> 16) ^ hash;
        return hash;
    }
    public static float noise1D(float x) {
        int xi = (int) Math.floor(x);
        float xf = x - xi;

        float a = random(xi);
        float b = random(xi + 1);

        return lerp(a, b, smoothstep(0, 1, xf));
    }

    public static float fbm(float x, int octaves, float persistence) {
        float total = 0.0f;
        float frequency = 1.0f;
        float amplitude = 1.0f;
        float maxValue = 0.0f;

        for (int i = 0; i < octaves; i++) {
            total += noise1D(x * frequency) * amplitude;
            maxValue += amplitude;
            amplitude *= persistence;
            frequency *= 2.0f;
        }
        return total / maxValue;
    }
    public static boolean isPowerOfTwo(int value) {
        return value > 0 && (value & (value - 1)) == 0;
    }
    public static int nextPowerOfTwo(int value) {
        value--;
        value |= value >> 1;
        value |= value >> 2;
        value |= value >> 4;
        value |= value >> 8;
        value |= value >> 16;
        value++;
        return value;
    }
    public static float boolToFloat(boolean value) {
        return value ? 1.0f : 0.0f;
    }
    public static int boolToInt(boolean value) {
        return value ? 1 : 0;
    }
    //
}


