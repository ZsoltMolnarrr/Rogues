package net.rogues.client;

import mod.azure.azurelibarmor.common.render.armor.AzArmorRenderer;
import mod.azure.azurelibarmor.common.render.armor.AzArmorRendererRegistry;
import net.minecraft.util.Identifier;
import net.rogues.RoguesMod;
import net.rogues.client.armor.RogueArmorRenderer;
import net.rogues.client.armor.WarriorArmorRenderer;
import net.rogues.client.effect.ChargeParticles;
import net.rogues.client.effect.DemoralizeParticles;
import net.rogues.client.effect.ShatterParticles;
import net.rogues.effect.RogueEffects;
import net.rogues.util.RogueSpells;
import net.spell_engine.api.effect.CustomModelStatusEffect;
import net.spell_engine.api.render.BuffParticleSpawner;
import net.spell_engine.api.render.LightEmission;
import net.spell_engine.api.render.ModelFxEffectRenderer;
import net.spell_engine.api.spell.fx.ModelEffect;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.fx.SpellEngineParticles;

import java.util.List;
import net.rogues.item.armor.RogueArmors;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.render.StunParticleSpawner;
import net.spell_engine.client.gui.SpellTooltip;
import net.spell_engine.rpg_series.item.Armor;

import java.util.function.Supplier;

public class RoguesClientMod {
    public static void init() {
        // Entity model layers + renderers are registered per-platform:
        //   Fabric   -> FabricClientMod (Fabric API)
        //   NeoForge -> NeoForgeClientMod (EntityRenderersEvent.RegisterLayerDefinitions / RegisterRenderers)
        // Layer definitions MUST be contributed during the RegisterLayerDefinitions phase, which is
        // over by the time FMLClientSetupEvent (where this init runs on NeoForge) fires.

        CustomParticleStatusEffect.register(RogueEffects.SHOCK.effect, new StunParticleSpawner());
        CustomParticleStatusEffect.register(RogueEffects.SHATTER.effect, new ShatterParticles(1));
        CustomParticleStatusEffect.register(RogueEffects.DEMORALIZE.effect, new DemoralizeParticles(1));
        CustomParticleStatusEffect.register(RogueEffects.CHARGE.effect, new ChargeParticles(1));
        // Persistent aura pulsed under the player every 20 ticks while Last Stand is active.
        CustomParticleStatusEffect.register(RogueEffects.LAST_STAND.effect,
                new BuffParticleSpawner(new ParticleBatch[]{
                        new ParticleBatch(SpellEngineParticles.area_effect_700.id().toString(),
                                ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.GROUND,
                                1, 0F, 0F)
                                .scale(1.5F)
                                .color(RogueSpells.LAST_STAND_COLOR.alpha(0.5F).toRGBA())
                                .followEntity(true)
                })
                .withFrequency(20)
                .scaleWithAmplifier(false));
        CustomModelStatusEffect.register(RogueEffects.NET_TRAP.effect, netTrapModelFxRenderer());

        SpellTooltip.addDescriptionMutator(Identifier.of(RoguesMod.NAMESPACE, "throw"), (args) -> {
            var description = args.description();
            var percent = SpellTooltip.percent(-1F * RogueEffects.SHATTER.config().firstModifier().value);
            description = description.replace(SpellTooltip.placeholder("armor_reduction"), percent);
            return description;
        });

        SpellTooltip.addDescriptionMutator(Identifier.of(RoguesMod.NAMESPACE, "shout"), (args) -> {
            var description = args.description();
            var percent = SpellTooltip.percent(-1F * RogueEffects.DEMORALIZE.config().firstModifier().value);
            description = description.replace(SpellTooltip.placeholder("damage_reduction"), percent);
            return description;
        });

        registerArmorRenderer(RogueArmors.RogueArmorSet_t1, RogueArmorRenderer::rogue);
        registerArmorRenderer(RogueArmors.RogueArmorSet_t2, RogueArmorRenderer::assassin);
        registerArmorRenderer(RogueArmors.RogueArmorSet_t3, RogueArmorRenderer::netheriteAssassin);
        registerArmorRenderer(RogueArmors.WarriorArmorSet_t1, WarriorArmorRenderer::warrior);
        registerArmorRenderer(RogueArmors.WarriorArmorSet_t2, WarriorArmorRenderer::berserker);
        registerArmorRenderer(RogueArmors.WarriorArmorSet_t3, WarriorArmorRenderer::netheriteBerserker);
    }

    /// The net pyramid drops onto the victim and snaps taut over ~8 ticks, then holds for the rest of
    /// the effect (`Playback.ONCE` keeps the final state rather than restarting).
    ///
    /// Scale transforms accumulate additively into `scale * (1 + delta)`, so an initial `-1` starts the
    /// model at zero size and the `+1` animation grows it back to full. `EASE_OUT_BACK` overshoots past
    /// 1 before settling, which reads as the corner anchors yanking the net tight.
    private static ModelFxEffectRenderer netTrapModelFxRenderer() {
        // Starts a body-height above the target, and invisible.
        var translateInitial = new ModelEffect.Transform();
        translateInitial.operation = "translate";
        translateInitial.y = 1.1F;

        var scaleInitial = new ModelEffect.Transform();
        scaleInitial.operation = "scale";
        scaleInitial.x = -1F; scaleInitial.y = -1F; scaleInitial.z = -1F;

        // Falls the 0.6 back down to a resting 0.5, accelerating as it goes.
        var drop = new ModelEffect.Animation();
        drop.operation = "translate";
        drop.start = 0; drop.end = 6;
        drop.y = -0.6F;
        drop.easing = ModelEffect.Easing.EASE_IN_QUAD;

        var snapTaut = new ModelEffect.Animation();
        snapTaut.operation = "scale";
        snapTaut.start = 0; snapTaut.end = 8;
        snapTaut.x = 1F; snapTaut.y = 1F; snapTaut.z = 1F;
        snapTaut.easing = ModelEffect.Easing.EASE_OUT_BACK;

        var effect = new ModelEffect();
        effect.model_id = Identifier.of(RoguesMod.NAMESPACE, "spell_effect/net_trap").toString();
        effect.light_emission = LightEmission.NONE; // rope and iron, not magic — no self-glow
        effect.duration = 8;
        effect.initial = List.of(translateInitial, scaleInitial);
        effect.animations = List.of(drop, snapTaut);

        return new ModelFxEffectRenderer(List.of(effect), ModelFxEffectRenderer.Playback.ONCE)
                .entityScaling(ModelFxEffectRenderer.SizeAxis.WIDTH, 0.5F);
    }

    private static void registerArmorRenderer(Armor.Set set, Supplier<AzArmorRenderer> armorRendererSupplier) {
        AzArmorRendererRegistry.register(armorRendererSupplier, set.head, set.chest, set.legs, set.feet);
    }
}