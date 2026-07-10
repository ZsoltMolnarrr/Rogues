package net.rogues.client.effect;

import net.minecraft.entity.LivingEntity;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.client.util.Color;
import net.spell_engine.fx.ParticleHelper;
import net.spell_engine.fx.SpellEngineParticles;

import java.util.List;

/// Arcing sparks around a Recklessness-afflicted entity, mirroring Wizards' Evocation spawner but
/// tinted with the warrior RAGE color. Recklessness is a single-stack effect, so unlike Evocation
/// the spawn rate does not ramp with amplifier.
public class RecklessnessParticles implements CustomParticleStatusEffect.Spawner {
    private final List<ParticleBatch> particles;

    public RecklessnessParticles() {
        this.particles = List.of(
                new ParticleBatch(
                        SpellEngineParticles.lightning_arc_A.id().toString(),
                        ParticleBatch.Shape.SPHERE,
                        ParticleBatch.Origin.CENTER,
                        null,
                        1,
                        0.05F,
                        0.1F,
                        0)
                        .color(Color.RAGE.toRGBA())
                        .extent(0.5F),
                new ParticleBatch(
                        SpellEngineParticles.lightning_arc_B.id().toString(),
                        ParticleBatch.Shape.SPHERE,
                        ParticleBatch.Origin.CENTER,
                        null,
                        1,
                        0.05F,
                        0.1F,
                        0)
                        .color(Color.RAGE.toRGBA())
                        .extent(0.5F)
        );
    }

    @Override
    public void spawnParticles(LivingEntity livingEntity, int amplifier) {
        if (livingEntity.age % 4 != 0) {
            return;
        }
        for (var batch : particles) {
            ParticleHelper.play(livingEntity.getWorld(), livingEntity, batch);
        }
    }
}
