package net.rogues.effect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.spell_engine.internals.WorldScheduler;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

public class ChargeEffect extends StatusEffect {
    protected ChargeEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    private static final HashSet<StatusEffect> movementImpairingEffects = new HashSet<>();

    private void updateMovementImpairingEffects() {
        if (!movementImpairingEffects.isEmpty()) {
            return;
        }
        for(var entry : Registries.STATUS_EFFECT.getIndexedEntries()) {
            var effect = entry.value();
            var attributeModifiers = effect.getAttributeModifiers();
            for (var modifierEntry : attributeModifiers.entrySet()) {
                var attribute = modifierEntry.getKey();
                var modifier = modifierEntry.getValue();
                if (attribute == EntityAttributes.GENERIC_MOVEMENT_SPEED) {
                    double treshold = 0;
                    switch (modifier.getOperation()) {
                        case ADDITION, MULTIPLY_BASE -> {
                            treshold = 0;
                        }
                        case MULTIPLY_TOTAL -> {
                            treshold = 1;
                        }
                    }
                    if (modifier.getValue() < treshold) {
                        movementImpairingEffects.add(effect);
                    }
                }
            }
        }
    }

    private void removeMovementImpairingEffects(LivingEntity entity) {
        updateMovementImpairingEffects();
        var effects = entity.getStatusEffects();
        for (var effect : effects) {
            var type = effect.getEffectType();
            if (movementImpairingEffects.contains(type)) {
                // Removing the effect immediately would cause a ConcurrentModificationException
                ((WorldScheduler)entity.getWorld()).schedule(1, () -> entity.removeStatusEffect(type));
            }
        }
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        removeMovementImpairingEffects(entity);
    }

    public void applyInstantEffect(@Nullable Entity source, @Nullable Entity attacker, LivingEntity target, int amplifier, double proximity) {
        removeMovementImpairingEffects(target);
    }

    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onApplied(entity, attributes, amplifier);
        removeMovementImpairingEffects(entity);
    }
}