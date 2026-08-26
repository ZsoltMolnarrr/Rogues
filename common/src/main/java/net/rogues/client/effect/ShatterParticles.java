package net.rogues.client.effect;

import net.minecraft.world.entity.LivingEntity;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.spell.fx.ParticleGroup;
import net.spell_engine.api.spell.fx.ParticleGroupBuilder;
import net.spell_engine.fx.ParticleHelper;
import net.spell_engine.fx.SpellEngineParticles;

public class ShatterParticles implements CustomParticleStatusEffect.Spawner {
    private final ParticleGroup particles;

    public ShatterParticles(int particleCount) {
        this.particles = ParticleGroupBuilder.of(SpellEngineParticles.dripping_blood)
                .batch(b -> b.shape(ParticleGroup.Shape.SPHERE).count(particleCount).speed(0.1F, 0.3F));
    }

    @Override
    public void spawnParticles(LivingEntity livingEntity, int amplifier) {
        var scaledParticles = particles.copy();
        scaledParticles.batch.count *= (amplifier + 1);
        ParticleHelper.play(livingEntity.level(), livingEntity, scaledParticles);
    }
}
