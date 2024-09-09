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

    private void removeMovementImpairingEffects(LivingEntity entity) {
        if (entity.getWorld().isClient()) {
            return;
        }
        var effects = entity.getStatusEffects();
        for (var instance : effects) {
            var effect = instance.getEffectType().value();
            if (((StatusEffectExtension)effect).isMovementImpairing()) {
                // Removing the effect immediately would cause a ConcurrentModificationException
                ((WorldScheduler)entity.getWorld()).schedule(1, () -> entity.removeStatusEffect(instance.getEffectType()));
            }
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        removeMovementImpairingEffects(entity);
        return true;
    }

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        super.onApplied(entity, amplifier);
        removeMovementImpairingEffects(entity);
    }
}