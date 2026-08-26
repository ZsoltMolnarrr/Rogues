package net.rogues.client.effect;

import net.minecraft.world.entity.LivingEntity;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.spell.fx.ParticleGroup;
import net.spell_engine.api.spell.fx.ParticleGroupBuilder;
import net.spell_engine.client.util.Color;
import net.spell_engine.fx.ParticleHelper;
import net.spell_engine.fx.SpellEngineParticles;

public class ChargeParticles  implements CustomParticleStatusEffect.Spawner {
    private final ParticleGroup particles;

    public ChargeParticles(int particleCount) {
        this.particles = ParticleGroupBuilder
                .magic(SpellEngineParticles.magic_stripe, ParticleGroup.Motion.ASCEND, Color.RAGE)
                .batch(ParticleGroupBuilder.Batches.casting(particleCount, 0.12F)
                        .andThen(b -> b.speed(0.11F, 0.12F)));
    }

    @Override
    public void spawnParticles(LivingEntity livingEntity, int amplifier) {
        var scaledParticles = particles.copy();
        scaledParticles.batch.count *= (amplifier + 1);
        ParticleHelper.play(livingEntity.level(), livingEntity, scaledParticles);
    }
}
