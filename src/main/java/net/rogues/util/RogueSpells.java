package net.rogues.util;

import net.minecraft.util.Identifier;
import net.rogues.RoguesMod;
import net.rogues.effect.RogueEffects;
import net.spell_engine.api.spell.ExternalSpellSchools;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.api.spell.fx.Sound;
import net.spell_engine.client.gui.SpellTooltip;
import net.spell_engine.fx.SpellEngineParticles;
import net.spell_engine.internals.target.SpellTarget;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RogueSpells {
    public record Entry(Identifier id, Spell spell, String title, String description,
                        @Nullable SpellTooltip.DescriptionMutator mutator) { }
    public static final List<Entry> entries = new ArrayList<>();
    private static Entry add(Entry entry) {
        entries.add(entry);
        return entry;
    }

    private static Spell activeSpellBase() {
        var spell = new Spell();
        spell.type = Spell.Type.ACTIVE;
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;
        spell.active = new Spell.Active();
        spell.active.cast = new Spell.Active.Cast();

        spell.learn = new Spell.Learn();
        spell.active.scroll = new Spell.Active.Scroll();

        return spell;
    }

    private static Spell.Impact createEffectImpact(Identifier effectId, float duration) {
        var buff = new Spell.Impact();
        buff.action = new Spell.Impact.Action();
        buff.action.type = Spell.Impact.Action.Type.STATUS_EFFECT;
        buff.action.status_effect = new Spell.Impact.Action.StatusEffect();
        buff.action.status_effect.effect_id = effectId.toString();
        buff.action.status_effect.duration = duration;
        return buff;
    }

    private static void configureCooldown(Spell spell, float duration) {
        if (spell.cost == null) {
            spell.cost = new Spell.Cost();
        }
        spell.cost.cooldown = new Spell.Cost.Cooldown();
        spell.cost.cooldown.duration = duration;
    }

    public static final Entry SLICE_AND_DICE = add(slice_and_dice());
    private static Entry slice_and_dice() {
        var id = Identifier.of(RoguesMod.NAMESPACE, "slice_and_dice");
        var title = "Slice and Dice";
        var description = "";
        var effect = RogueEffects.SLICE_AND_DICE;
        var spell = activeSpellBase();
        spell.range = 0;
        spell.tier = 1;

        spell.release.animation = "spell_engine:dual_handed_weapon_charge";
        spell.release.sound = new Sound(RogueSounds.SLICE_AND_DICE.id());
        spell.release.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.getMagicParticleVariant(
                        SpellEngineParticles.WHITE,
                        SpellEngineParticles.MagicParticleFamily.Shape.SPARK,
                        SpellEngineParticles.MagicParticleFamily.Motion.FLOAT).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.LAUNCH_POINT,
                        15, 0.2F, 0.3F)
                        .preSpawnTravel(7)
                        .invert()
        };

        spell.deliver.type = Spell.Delivery.Type.STASH_EFFECT;
        spell.deliver.stash_effect = new Spell.Delivery.StashEffect();
        spell.deliver.stash_effect.id = effect.id.toString();
        spell.deliver.stash_effect.consume = 0;
        var stashMeleeTrigger = new Spell.Trigger();
        stashMeleeTrigger.type = Spell.Trigger.Type.MELEE_IMPACT;
        stashMeleeTrigger.target_override = Spell.Trigger.TargetSelector.CASTER;
        spell.deliver.stash_effect.triggers = List.of(stashMeleeTrigger);

        var buff = createEffectImpact(effect.id, 10);
        buff.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.ADD;
        buff.action.status_effect.amplifier = 9;
        buff.action.status_effect.refresh_duration = false;
        spell.impacts = List.of(buff);

        configureCooldown(spell, 15);
        spell.cost.exhaust = 0.2F;

        return new Entry(id, spell, title, description, null);
    }

    public static Entry shock_powder = add(shock_powder());
    private static Entry shock_powder() {
        var id = Identifier.of(RoguesMod.NAMESPACE, "shock_powder");
        var title = "Shock Powder";
        var description = "";
        var effect = RogueEffects.SHOCK;
        var spell = activeSpellBase();
        spell.range = 5;
        spell.tier = 2;

        spell.release.animation = "spell_engine:dual_handed_ground_release";
        spell.release.sound = new Sound(RogueSounds.SHOCK_POWDER_RELEASE.id());
        spell.release.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.smoke_medium.id().toString(),
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        50, 0.2F, 0.3F)
                        .preSpawnTravel(6),
                new ParticleBatch(SpellEngineParticles.smoke_medium.id().toString(),
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        60, 0.2F, 0.3F)
                        .preSpawnTravel(8),
                new ParticleBatch(SpellEngineParticles.smoke_medium.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        50, 0.25F, 0.25F)
                        .preSpawnTravel(4),
                new ParticleBatch(SpellEngineParticles.electric_arc_A.id().toString(),
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        6, 0.01F, 0.05F)
                        .extent(3),
                new ParticleBatch(SpellEngineParticles.electric_arc_B.id().toString(),
                        ParticleBatch.Shape.PILLAR, ParticleBatch.Origin.FEET,
                        8, 0.01F, 0.05F)
                        .extent(5)
        };

        spell.target.type = Spell.Target.Type.AREA;
        spell.target.area = new Spell.Target.Area();
        spell.target.area.vertical_range_multiplier = 0.5F;

        var buff = createEffectImpact(effect.id, 3);
        buff.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.ADD;
        buff.action.status_effect.apply_limit = new Spell.Impact.Action.StatusEffect.ApplyLimit();
        buff.action.status_effect.apply_limit.health_base = 50;
        buff.action.status_effect.apply_limit.spell_power_multiplier = 2F;
        buff.sound = new Sound(RogueSounds.SHOCK_POWDER_IMPACT.id());
        spell.impacts = List.of(buff);

        configureCooldown(spell, 16);
        spell.cost.exhaust = 0.3F;

        return new Entry(id, spell, title, description, null);
    }

    public static Entry shadow_step = add(shadow_step());
    private static Entry shadow_step() {
        var id = Identifier.of(RoguesMod.NAMESPACE, "shadow_step");
        var title = "Shadowstep";
        var description = "";
        var effect = RogueEffects.SHADOW_STEP;
        var spell = activeSpellBase();
        spell.range = 15;
        spell.tier = 3;

        spell.release.animation = "spell_engine:one_handed_area_release";
        spell.release.sound = new Sound(RogueSounds.SHADOW_STEP_DEPART.id());

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();
        spell.target.aim.required = true;

        var impact = new Spell.Impact();
        impact.action = new Spell.Impact.Action();
        impact.action.type = Spell.Impact.Action.Type.TELEPORT;
        var teleport = new Spell.Impact.Action.Teleport();
        teleport.mode = Spell.Impact.Action.Teleport.Mode.BEHIND_TARGET;
        teleport.intent = SpellTarget.Intent.HARMFUL;
        teleport.behind_target = new Spell.Impact.Action.Teleport.BehindTarget();
        teleport.behind_target.distance = 1.5F;
        teleport.depart_particles = new ParticleBatch[]{
                new ParticleBatch("cloud",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.FEET,
                        20, 0.05F, 0.1F)
                        .preSpawnTravel(15)
                        .invert()
        };
        teleport.arrive_particles = new ParticleBatch[]{
                new ParticleBatch("poof",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.FEET,
                        10, 0.05F, 0.1F)
                        .preSpawnTravel(2)
        };
        impact.action.teleport = teleport;

        var buff = createEffectImpact(effect.id, 1.5F);
        buff.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.SET;

        spell.impacts = List.of(impact, buff);

        configureCooldown(spell, 12);
        spell.cost.exhaust = 0.4F;

        return new Entry(id, spell, title, description, null);
    }

    public static Entry vanish = add(vanish());
    private static Entry vanish() {
        var id = Identifier.of(RoguesMod.NAMESPACE, "vanish");
        var title = "Vanish";
        var description = "";
        var effect = RogueEffects.STEALTH;
        var spell = activeSpellBase();
        spell.range = 0;
        spell.tier = 4;

        spell.release.animation = "spell_engine:dual_handed_weapon_cross";
        spell.release.sound = new Sound(RogueSounds.VANISH_COMBINED.id());
        spell.release.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.smoke_medium.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        20, 0.12F, 0.15F)
                        .preSpawnTravel(3),
                new ParticleBatch(SpellEngineParticles.smoke_medium.id().toString(),
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.FEET,
                        20, 0.12F, 0.15F)
                        .preSpawnTravel(4),
                new ParticleBatch("poof",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        10, 0.01F, 0.1F),
                new ParticleBatch("campfire_cosy_smoke",
                        ParticleBatch.Shape.WIDE_PIPE, ParticleBatch.Origin.FEET,
                        10, 0.01F, 0.1F)
        };

        var buff = createEffectImpact(effect.id, 8);
        spell.impacts = List.of(buff);

        configureCooldown(spell, 30);
        spell.cost.exhaust = 0.4F;

        return new Entry(id, spell, title, description, null);
    }
}
