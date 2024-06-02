package net.rogues.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.rogues.effect.Effects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityStealth {
    @WrapOperation(method = "updatePotionVisibility", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z"))
    private boolean updatePotionVisibility_WRAP_Stealth(LivingEntity instance, StatusEffect effect, Operation<Boolean> original) {
        return original.call(instance, effect) || instance.hasStatusEffect(Effects.STEALTH);
    }

    @Inject(method = "getAttackDistanceScalingFactor", at = @At("RETURN"), cancellable = true)
    private void getAttackDistanceScalingFactor_RETURN_Stealth(Entity entity, CallbackInfoReturnable<Double> cir) {
        var thisEntity = (LivingEntity) (Object) this;
        if (thisEntity.hasStatusEffect(Effects.STEALTH)) {
            cir.setReturnValue(cir.getReturnValue() * 0.1);
        }
    }
}
