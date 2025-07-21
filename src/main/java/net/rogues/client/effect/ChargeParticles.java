package net.rogues.client.effect;

import net.minecraft.entity.LivingEntity;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.client.util.Color;
import net.spell_engine.fx.ParticleHelper;
import net.spell_engine.fx.SpellEngineParticles;

public class ChargeParticles  implements CustomParticleStatusEffect.Spawner {
    private final ParticleBatch particles;

    public ChargeParticles(int particleCount) {
        this.particles = new ParticleBatch(
                SpellEngineParticles.MagicParticles.get(
                        SpellEngineParticles.MagicParticles.Shape.STRIPE,
                        SpellEngineParticles.MagicParticles.Motion.ASCEND).id().toString(),
                ParticleBatch.Shape.WIDE_PIPE,
                ParticleBatch.Origin.FEET,
                null,
                particleCount,
                0.11F,
                0.12F,
                0)
                .color(Color.RAGE.toRGBA());
    }

    @Override
    public void spawnParticles(LivingEntity livingEntity, int amplifier) {
        var scaledParticles = new ParticleBatch(particles);
        scaledParticles.count *= (amplifier + 1);
        ParticleHelper.play(livingEntity.getWorld(), livingEntity, scaledParticles);
    }
}