package net.rogues.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.spell_engine.api.spell.ParticleBatch;
import net.spell_engine.particle.ParticleHelper;

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

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onRemoved(entity, attributes, amplifier);
        System.out.println();
        if (!entity.getWorld().isClient()) {
            ParticleHelper.sendBatches(entity, new ParticleBatch[]{ POP_PARTICLES } );
        }
    }
}
