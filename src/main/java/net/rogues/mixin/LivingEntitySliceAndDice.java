package net.rogues.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.rogues.effect.Effects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntitySliceAndDice {
    @Unique
    private int lastSliceAndDiceTime = 0;

    @Inject(method = "onAttacking", at = @At("HEAD"))
    private void onAttacking_TAIL_SliceAndDice(Entity target, CallbackInfo ci) {
        var entity = (LivingEntity) (Object) this;

        if (entity.hasStatusEffect(Effects.SLICE_AND_DICE.registryEntry) // Check if the entity has the slice and dice effect
                && lastSliceAndDiceTime != entity.age // Only once within a single game tick
                && !target.isSpectator()
        ) {
            var instance = entity.getActiveStatusEffects().get(Effects.SLICE_AND_DICE.registryEntry);
            var stack = instance.getAmplifier();
            if (stack < (Effects.sliceAndDiceMaxStacks() - 1)) {
                entity.addStatusEffect(new StatusEffectInstance(
                                Effects.SLICE_AND_DICE.registryEntry, instance.getDuration(), stack + 1,
                                false, false, true),
                        entity);
                lastSliceAndDiceTime = entity.age;
            }
        }
    }
}
