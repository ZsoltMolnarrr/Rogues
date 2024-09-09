package net.rogues.mixin;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.rogues.effect.StatusEffectExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatusEffect.class)
public class StatusEffectMixin implements StatusEffectExtension {
    private boolean isMovementImpairing = false;

    @Inject(method = "addAttributeModifier", at = @At("HEAD"))
    private void addAttributeModifier_HEAD(
            RegistryEntry<EntityAttribute> attribute, Identifier id, double amount,
            EntityAttributeModifier.Operation operation, CallbackInfoReturnable<StatusEffect> cir) {
        if (attribute.equals(EntityAttributes.GENERIC_MOVEMENT_SPEED)) {
            double treshold = 0;
            switch (operation) {
                case ADD_VALUE, ADD_MULTIPLIED_BASE -> {
                    treshold = 0;
                }
                case ADD_MULTIPLIED_TOTAL -> {
                    treshold = 1;
                }
            }
            if (amount < treshold) {
                this.isMovementImpairing = true;
            }
        }
    }

    @Override
    public boolean isMovementImpairing() {
        return this.isMovementImpairing;
    }
}
