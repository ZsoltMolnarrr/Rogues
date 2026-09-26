package net.rogues.mixin;

import net.minecraft.entity.effect.DamageModifierStatusEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DamageModifierStatusEffect.class)
public interface DamageModifierStatusEffectAccessor {
    @Mutable
    @Accessor("modifier")
    void rogues$setModifier(double modifier);
}
