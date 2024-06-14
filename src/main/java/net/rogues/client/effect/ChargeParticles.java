package net.rogues.client.effect;

import net.minecraft.entity.LivingEntity;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.spell.ParticleBatch;
import net.spell_engine.particle.ParticleHelper;

public class ChargeParticles  implements CustomParticleStatusEffect.Spawner {
    private final ParticleBatch particles;

    public ChargeParticles(int particleCount) {
        this.particles = new ParticleBatch(
                "spell_engine:buff_rage",
                ParticleBatch.Shape.PIPE,
                ParticleBatch.Origin.FEET,
                null,
                particleCount,
                0.11F,
                0.12F,
                0,
                -0.2F);
    }

    @Override
    public void spawnParticles(LivingEntity livingEntity, int amplifier) {
        var scaledParticles = new ParticleBatch(particles);
        scaledParticles.count *= (amplifier + 1);
        ParticleHelper.play(livingEntity.getWorld(), livingEntity, scaledParticles);
    }
}