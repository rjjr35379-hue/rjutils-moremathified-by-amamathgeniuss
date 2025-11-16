package net.rj.utils.util;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

//for custom predicates if wanted
public interface Customs {

    //Calculates the predicate value for the given context
    float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed);

    // gets the type  identifier for the predicateme

    String getType();

    //optinoal
    default void onRegister() {

    }
}
