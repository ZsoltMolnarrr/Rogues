package net.rogues.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.rogues.util.RogueSounds;
import net.spell_engine.api.spell.fx.ParticleGroup;
import net.spell_engine.api.spell.fx.ParticleGroupBuilder;
import net.spell_engine.api.spell.fx.ParticleGroupBuilder.Batches;
import net.spell_engine.fx.ParticleHelper;

import java.util.List;

public class StealthEffect extends MobEffect {
    protected StealthEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public static final ParticleGroup POP_PARTICLES = ParticleGroupBuilder.of("spell_engine:smoke_medium")
            .batch(b -> b.shape(ParticleGroup.Shape.CIRCLE).count(20)
                    .speed(0.18F, 0.2F).verticalOrigin(Batches.FEET));

    public static void onRemove(LivingEntity entity) {
        if (!entity.level().isClientSide()) {
            RogueSounds.playSoundEvent(entity.level(), entity, RogueSounds.STEALTH_LEAVE.soundEvent());
            ParticleHelper.sendBatches(entity, List.of(POP_PARTICLES));
        }
    }
}
