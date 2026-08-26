package net.rogues.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.rogues.RoguesMod;
import net.rogues.effect.RogueEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityStealth {
    @WrapOperation(method = "updateInvisibilityStatus", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/core/Holder;)Z"))
    private boolean updatePotionVisibility_WRAP_Stealth(LivingEntity instance, Holder<MobEffect> effect, Operation<Boolean> original) {
        return original.call(instance, effect) || instance.hasEffect(RogueEffects.STEALTH.entry);
    }

    @Inject(method = "getVisibilityPercent", at = @At("RETURN"), cancellable = true)
    private void getAttackDistanceScalingFactor_RETURN_Stealth(Entity entity, CallbackInfoReturnable<Double> cir) {
        var thisEntity = (LivingEntity) (Object) this;
        if (thisEntity.hasEffect(RogueEffects.STEALTH.entry)) {
            cir.setReturnValue(cir.getReturnValue() * RoguesMod.tweaksConfig.value.stealth_visibility_multiplier);
        }
    }
}
