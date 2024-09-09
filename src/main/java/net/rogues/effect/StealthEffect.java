package net.rogues.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.rogues.util.RogueSounds;
import net.spell_engine.api.spell.ParticleBatch;
import net.spell_engine.particle.ParticleHelper;
import net.spell_engine.utils.SoundHelper;

public class StealthEffect extends StatusEffect {
    protected StealthEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    public static final ParticleBatch POP_PARTICLES = new ParticleBatch(
            "spell_engine:smoke_medium",
            ParticleBatch.Shape.CIRCLE,
            ParticleBatch.Origin.FEET,
            null,
            20,
            0.18F,
            0.2F,
            0);

    public static void onRemove(LivingEntity entity) {
        if (!entity.getWorld().isClient()) {
            RogueSounds.playSoundEvent(entity.getWorld(), entity, RogueSounds.STEALTH_LEAVE.sound());
            ParticleHelper.sendBatches(entity, new ParticleBatch[]{POP_PARTICLES});
        }
    }
}
