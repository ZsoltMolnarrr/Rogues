package net.rogues.client.effect;

import net.minecraft.entity.LivingEntity;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.spell.fx.ParticleGroup;
import net.spell_engine.api.spell.fx.ParticleGroupBuilder;
import net.spell_engine.client.util.Color;
import net.spell_engine.fx.ParticleHelper;
import net.spell_engine.fx.SpellEngineParticles;

public class DemoralizeParticles implements CustomParticleStatusEffect.Spawner {
    private final ParticleGroup particles;

    public DemoralizeParticles(int particleCount) {
        this.particles = ParticleGroupBuilder.of(SpellEngineParticles.smoke_medium)
                .color(Color.RAGE)
                .batch(b -> b.shape(ParticleGroup.Shape.SPHERE).count(particleCount).speed(0.08F, 0.2F));
    }

    @Override
    public void spawnParticles(LivingEntity livingEntity, int amplifier) {
        var scaledParticles = particles.copy();
        scaledParticles.batch.count *= (amplifier + 1);
        ParticleHelper.play(livingEntity.getWorld(), livingEntity, scaledParticles);
    }
}
