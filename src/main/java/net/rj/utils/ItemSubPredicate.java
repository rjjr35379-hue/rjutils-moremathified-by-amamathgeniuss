package net.rj.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
public class ItemSubPredicate {
    // Main Entry Prediactae
    public static boolean predicate(ItemStack stack, Identifier predicateId, JsonElement value) {
        if (stack == null || predicateId == null || value == null) {

            return false;
        }
        String predicatePath = predicateId.getPath();
        // case labels

        return switch (predicatePath) {
            case "damage" -> damage(stack, value);
            case "custom_model_data" -> customModelData(stack, value);
            case "enchantments" -> enchantments(stack, value);
            case "stored_enchantments" -> storedEnchantments(stack, value);
            case "trim" -> trim(stack, value);
            case "potion_contents" -> potion(stack, value);
            case "custom_data" -> customData(stack, value);
            case "unbreakable" -> unbreakable(stack, value);
            case "repair_cost" -> repairCost(stack, value);
            case "attribute_modifiers" -> attributeModifiers(stack, value);
            case "can_break" -> canBreak(stack, value);
            case "can_place_on" -> canPlaceOn(stack, value);
            case "special" -> special(stack, value);
            case "bundle/selected_item" -> bundleSelectedItem(stack, value);
            case "tint_source" -> tintSource(stack, value);
            default -> genericNbt(stack, predicatePath, value);
        };
    }

    private static boolean damage(ItemStack stack, JsonElement value) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return false;

        int actualDamage = nbt.getInt(DataComponentTypes.DAMAGE);

        // compare
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
            return actualDamage == value.getAsInt();
        }

        // min/max
        if (value.isJsonObject()) {
            JsonObject obj = value.getAsJsonObject();
            if (obj.has("min") && actualDamage < obj.get("min").getAsInt()) return false;
            if (obj.has("max") && actualDamage > obj.get("max").getAsInt()) return false;
            return true;
        }

        return false;
    }

    private static boolean customModelData(ItemStack stack, JsonElement value) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(DataComponentTypes.CUSTOM_MODEL_DATA)) return false;

        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
            return nbt.getInt(DataComponentTypes.CUSTOM_MODEL_DATA) == value.getAsInt();
        }

        return false;
    }

    private static boolean enchantments(ItemStack stack, JsonElement value) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return false;

        boolean hasEnchantments = nbt.contains(DataComponentTypes.ENCHANTMENTS);

        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) {
            return hasEnchantments == value.getAsBoolean();
        }

        return hasEnchantments;
    }

    private static boolean storedEnchantments(ItemStack stack, JsonElement value) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return false;

        boolean hasStoredEnchantments = nbt.contains(DataComponentTypes.STORED_ENCHANTMENTS);

        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) {
            return hasStoredEnchantments == value.getAsBoolean();
        }

        return hasStoredEnchantments;
    }

    private static boolean trim(ItemStack stack, JsonElement value) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return false;

        boolean hasTrim = nbt.contains(DataComponentTypes.TRIM);

        //  true/false check
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) {
            return hasTrim == value.getAsBoolean();
        }

        //  trim comparison
        if (hasTrim && value.isJsonObject()) {
            NbtCompound trim = nbt.getCompound(DataComponentTypes.TRIM);
            JsonObject obj = value.getAsJsonObject();

            if (obj.has("material")) {
                if (!trim.getString("material").equals(obj.get("material").getAsString())) return false;
            }

            if (obj.has("pattern")) {
                if (!trim.getString("pattern").equals(obj.get("pattern").getAsString())) return false;
            }

            return true;
        }

        return hasTrim;
    }

    private static boolean potion(ItemStack stack, JsonElement value) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return false;


        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
            return nbt.getString(DataComponentTypes.POTION_CONTENTS)
                    .equals(value.getAsString());
        }

        return false;
    }

    private static boolean customData(ItemStack stack, JsonElement value) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return false;

        //general check
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) {
            return (nbt.getSize() > 0) == value.getAsBoolean();
        }

        // default to true ----:/
        return true;
    }

    private static boolean unbreakable(ItemStack stack, JsonElement value) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return false;

        boolean isUnbreakable =
                nbt.contains(DataComponentTypes.UNBREAKABLE)
                        && nbt.getBoolean(DataComponentTypes.UNBREAKABLE);

        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) {
            return isUnbreakable == value.getAsBoolean();
        }

        return isUnbreakable;
    }

    private static boolean repairCost(ItemStack stack, JsonElement value) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(DataComponentTypes.REPAIR_COST)) return false;

        int actual = nbt.getInt(DataComponentTypes.REPAIR_COST);

        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
            return actual == value.getAsInt();
        }

        if (value.isJsonObject()) {
            JsonObject obj = value.getAsJsonObject();
            if (obj.has("min") && actual < obj.get("min").getAsInt()) return false;
            if (obj.has("max") && actual > obj.get("max").getAsInt()) return false;
            return true;
        }

        return false;
    }

    private static boolean attributeModifiers(ItemStack stack, JsonElement value) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return false;

        boolean hasAttributeModifiers = nbt.contains(DataComponentTypes.ATTRIBUTE_MODIFIERS);

        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) {
            return hasAttributeModifiers == value.getAsBoolean();
        }

        return hasAttributeModifiers;
    }

    private static boolean canBreak(ItemStack stack, JsonElement value) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return false;

        boolean hasCanBreak = nbt.contains(DataComponentTypes.CAN_DESTROY);

        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) {
            return hasCanBreak == value.getAsBoolean();
        }

        return hasCanBreak;
    }

    private static boolean canPlaceOn(ItemStack stack, JsonElement value) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return false;

        boolean hasCanPlaceOn = nbt.contains(DataComponentTypes.CAN_PLACE_ON);

        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) {
            return hasCanPlaceOn == value.getAsBoolean();
        }

        return hasCanPlaceOn;
    }
//WIP
    //for things like

    //{
    //  "predicate": { "special": 1 },
    //  "model": "item/custom_special_item"
    //}
    private static boolean special(ItemStack stack, JsonElement value) {
        NbtCompound nbt = stack.getNbt();
        //again boolean check
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) {
            if (nbt == null) return !value.getAsBoolean();

            //check if item has special rendinrg NBT blockentitnytag for banners shieslds blah blah
            boolean hasSpecial = nbt.contains("BlockEntityTag")      ||
                    nbt.contains("display") ||
                    nbt.contains("Patterns");
            return hasSpecial == value.getAsBoolean();
        }
// string check
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
            if (nbt == null) return false;

            String specialT = value.getAsString();

            return switch (specialT) {
                case "banner" -> {
                    // Banners have blockEntityTag with patterns
                    if (!nbt.contains("BlockEntityTag", 10)) yield false;
                    NbtCompound blockEntity = nbt.getCompound("BlockEntityTag");
                    yield blockEntity.contains("Patterns", 9);
                }
                case "bed" -> {
                   //beds might have custom colors
                    yield nbt.contains("color", 3);
                }
                case "chest" -> {
                    //trapped chests or with custom names
                    yield nbt.contains("display", 10) ||
                            stack.getItem().toString().contains("trapped");
                }
            //   case "conduit" -> false; disabled for reasons

                case "decorated_pot" -> {
                    //decorated pots have sherds
                    yield nbt.contains("BlockEntityTag", 10);
                }
                case "head", "player_head" -> {
                    //player head or skullwjner
                    yield nbt.contains("SkullOwner", 10);
                }
                case "shield" -> {
                    // Shields  patterns
                    yield nbt.contains("BlockEntityTag", 10) || nbt.contains("Patterns", 9);
                }
                case "shulker_box" -> {
                    //check if shukkerbox has an item inside
                    if (!nbt.contains("BlockEntityTag", 10)) yield false;
                    NbtCompound blockEntity = nbt.getCompound("BlockEntityTag");
                    yield blockEntity.contains("Items", 9);
                }
                case "standing_sign", "hanging_sign" -> {
                    //if sign has a text
                    if (!nbt.contains("BlockEntityTag", 10)) yield false;
                    NbtCompound blockEntity = nbt.getCompound("BlockEntityTag");
                    yield blockEntity.contains("Text1") || blockEntity.contains("front_text");
                }
                case "trident" -> {
                   //check if trident has a loyalty or enchantments might have loyalty for specail rendering
                    if (!nbt.contains("Enchantments", 9)) yield false;
                    NbtList enchants = nbt.getList("Enchantments", 10);
                    for (int i = 0; i < enchants.size(); i++) {
                        NbtCompound ench = enchants.getCompound(i);
                        if (ench.getString("id").contains("loyalty")) {
                            yield true;
                        }
                    }
                    yield false;
                }
                default -> false;
            };
        }
        if (value.isJsonObject()) {
            if (nbt == null) return false;
            JsonObject obj = value.getAsJsonObject();

            //this for "type": in special but can be get more specificy
            if (obj.has("type")) {
                String type = obj.get("type").getAsString();
                return special(stack, obj.get("type"));
            }
        }
        return false;
    }

            //looks for bundle
    //tracks what's inside the bundle that is sleteced
    //this will allow for you to see what's active and selecrted
    //{
    //  "predicate": { "bundle/selected_item": 0 },
    //  "model": "item/bundle_empty"
    //}
    private static boolean bundleSelectedItem(ItemStack stack, JsonElement value) {
        Identifier itemId = net.minecraft.registry.Registries.ITEM.getId(stack.getItem());
        if (!itemId.getPath().equals("bundle")) {
            return false;
        }
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return false;

        //in 1201 htey use Items nbt
        if (!nbt.contains("Items", 9)) {

            //empty bundle check for -1 or 0
            if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
                int expected = value.getAsInt();
                return expected == -1 || expected == 0;
            }
            return false;
        }
        NbtList items = nbt.getList("Items", 10);
        int itemCount = items.size();
//number check specific slot on index or item count
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
            int expected = value.getAsInt();
 //is bundle full or not chekc
            return expected == itemCount ||
                    (expected == 0 && itemCount == 0) ||
                    (expected > 0 && itemCount > 0);
        }
 //blooolean check
        // abt more complex same as tint
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) {
            return (itemCount > 0) == value.getAsBoolean();
        }
        if (value.isJsonObject()) {
            JsonObject obj = value.getAsJsonObject();

            if (obj.has("min") && itemCount < obj.get("min").getAsInt()) return false;
            if (obj.has("max") && itemCount > obj.get("max").getAsInt()) return false;

            // Check for specific items on index
            if (obj.has("index") && obj.has("item")) {
                int index = obj.get("index").getAsInt();
                if (index < 0 || index >= itemCount) return false;

                NbtCompound item = items.getCompound(index);
                String itemType = item.getString("id");
                String expectedItem = obj.get("item").getAsString();

                return itemType.equals(expectedItem) ||
                        itemType.endsWith(":" + expectedItem);
            }

            return true;
        }
        return itemCount > 0;
    }

    //model tint source looks for model_tint_source
    //this detemrnies how an item is tinted
    // used on item and block models
    //example
    //{
    //  "predicate": { "model_tint_source": "layer0" },
    //  "model": "item/colored_item_layer0"
    //}
    // "constant" fixec color
     // "dye" dyed armor
     // "grass" grass tintint
     // "firework" firework star color
     // "potion" potion volor
    // "map_color" ap colors
     // "wools" or wtv // UPDATE I TRIED BUT I THINK they have color codes so no tint
    private static boolean tintSource(ItemStack stack, JsonElement value) {
        Identifier itemId = net.minecraft.registry.Registries.ITEM.getId(stack.getItem());
        String itemPath = itemId.getPath();

        //d= determine tint source on item
        String tintSource = dTintSource(stack, itemPath);

        //han dle string check
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
            String expectedTint = value.getAsString();
            return tintSource.equals(expectedTint);
        }
//handle boolean check
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) {
            boolean hasTint = !tintSource.equals("none");
            return hasTint == value.getAsBoolean();
        }

 //for a bşt fomre complex
        if (value.isJsonObject()) {
            JsonObject obj = value.getAsJsonObject();
            //Check for specific color value tint or dye or cınstant
            if (obj.has("type")) {
                return tintSource.equals(obj.get("type").getAsString());
            }
            if (obj.has("color") && (tintSource.equals("constant") || tintSource.equals("dye"))) {
                NbtCompound nbt = stack.getNbt();
                if (nbt == null) return false;

                if (nbt.contains("display", 10)) {
                    NbtCompound display = nbt.getCompound("display");
                    if (display.contains("color", 3)) {
                        int actualColor = display.getInt("color");
                        int expectedColor = obj.get("color").getAsInt();
                        return actualColor == expectedColor;
                    }
                }
            }
        }
        return !tintSource.equals("none");
    }
    //helper methods
    private static String dTintSource(ItemStack stack, String itemPath) {
        NbtCompound nbt = stack.getNbt();

        //check for leather
        if (itemPath.startsWith("leather_") || itemPath.equals("wolf_armor")) {
            if (nbt != null && nbt.contains("display", 10)) {
                NbtCompound display = nbt.getCompound("display");
                if (display.contains("color", 3)) {
                    return "dye";
                }
            }
            return "constant"; // Default leather color
        }
        //CHECKS
        if (itemPath.contains("grass") || itemPath.contains("fern") ||
                itemPath.contains("leaves") || itemPath.contains("vine")) {
            return "grass";
        }
        if (itemPath.equals("potion") || itemPath.equals("splash_potion") ||
                itemPath.equals("lingering_potion") || itemPath.equals("tipped_arrow")) {
            return "potion";
        }

        if (itemPath.equals("firework_star")) {
            return "firework";
        }
        if (itemPath.equals("filled_map")) {
            return "map_color";
        }
        //team colored beds carpers bannerds


        return "none";
    }

  //WIP
    // fallback logic for any generic Nbtr
    private static boolean genericNbt(ItemStack stack, String key, JsonElement value) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return false;

        boolean hasKey = nbt.contains(key);

        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) {
            return hasKey == value.getAsBoolean();
        }

        if (hasKey) {
            if (value.isJsonPrimitive()) {
                var prim = value.getAsJsonPrimitive();

                if (prim.isNumber()) return nbt.getInt(key) == prim.getAsInt();
                if (prim.isString()) return nbt.getString(key).equals(prim.getAsString());
            }
        }

        return false;
    }


    //WIP  special`, `bundle/selected_item`, and the `model tint source might not work proceed with caution
    //update i improved it maybe works maybe dnot


}
