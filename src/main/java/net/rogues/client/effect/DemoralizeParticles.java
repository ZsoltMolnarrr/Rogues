package net.rogues.client.effect;

import net.minecraft.entity.LivingEntity;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.particle.ParticleHelper;

public class DemoralizeParticles implements CustomParticleStatusEffect.Spawner {
    private final ParticleBatch particles;

    public DemoralizeParticles(int particleCount) {
        this.particles = new ParticleBatch(
                "spell_engine:weakness_smoke",
                ParticleBatch.Shape.SPHERE,
                ParticleBatch.Origin.CENTER,
                null,
                particleCount,
                0.08F,
                0.2F,
                0);
    }

    @Override
    public void spawnParticles(LivingEntity livingEntity, int amplifier) {
        var scaledParticles = new ParticleBatch(particles);
        scaledParticles.count *= (amplifier + 1);
        ParticleHelper.play(livingEntity.getWorld(), livingEntity, scaledParticles);
    }
}