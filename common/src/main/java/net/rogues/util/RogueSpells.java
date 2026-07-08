package net.rogues.util;

import net.minecraft.util.Identifier;
import net.rogues.RoguesMod;
import net.rogues.effect.RogueEffects;
import net.rogues.entity.RogueEntities;
import net.spell_engine.api.datagen.SpellBuilder;
import net.spell_engine.api.render.LightEmission;
import net.spell_engine.api.spell.ExternalSpellSchools;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.api.spell.fx.PlayerAnimation;
import net.spell_engine.api.spell.fx.Sound;
import net.spell_engine.client.gui.SpellTooltip;
import net.spell_engine.client.util.Color;
import net.spell_engine.fx.SpellEngineParticles;
import net.spell_engine.internals.target.SpellTarget;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RogueSpells {
    public enum Book {
        ROGUE("Rogue Handbook", "Rogue Skill Scroll",
                "Spell Book of Rogues, using quick weapons and skills to strike enemies from the shadows\n- Strengths: High mobility and melee attack damage\n- Weaknesses: Mediocre defense and limited ranged attacks\n- Equipment: Moderately armored"),
        WARRIOR("Warriors' Codex", "Warriors Skill Scroll",
                "Spell Book of Warriors, using heavy weapons and skills to dominate close combat\n- Strengths: Strong melee attacks with good mobility\n- Weaknesses: Limited ranged attacks and supportive skills\n- Equipment: Heavily armored");

        public final String bookName;
        public final String scrollName;
        public final String bindingDescription;
        Book(String bookName, String scrollName, String bindingDescription) {
            this.bookName = bookName;
            this.scrollName = scrollName;
            this.bindingDescription = bindingDescription;
        }
    }
    public record Entry(Identifier id, Spell spell, String title, String description,
                        @Nullable SpellTooltip.DescriptionMutator mutator,
                        @Nullable List<Object> weaponGroups,
                        @Nullable Book book) {
        public Entry(Identifier id, Spell spell, String title, String description) {
            this(id, spell, title, description, null, List.of(), null);
        }
        public Entry book(Book book) { return new Entry(id, spell, title, description, mutator, weaponGroups, book); }
    }
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

    public static final Entry SLICE_AND_DICE = add(slice_and_dice().book(Book.ROGUE));
    private static Entry slice_and_dice() {
        var id = Identifier.of(RoguesMod.NAMESPACE, "slice_and_dice");
        var title = "Slice & Dice";
        var description = "Enter a battle trance, during which your attacks will each grant extra power, stacking up to {effect_amplifier_cap} times. Expires after {effect_duration} sec.";
        var effect = RogueEffects.SLICE_AND_DICE;
        var spell = activeSpellBase();
        spell.range = 0;
        spell.tier = 2;

        spell.release.animation = PlayerAnimation.of("spell_engine:dual_handed_weapon_charge");
        spell.release.sound = new Sound(RogueSounds.SLICE_AND_DICE.id());
        spell.release.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.MagicParticles.get(
                        SpellEngineParticles.MagicParticles.Shape.SPARK,
                        SpellEngineParticles.MagicParticles.Motion.FLOAT).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.LAUNCH_POINT,
                        15, 0.15F, 0.2F)
                        .preSpawnTravel(7)
                        .invert()
                        .color(Color.WHITE.toRGBA())
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
        buff.action.status_effect.amplifier = 1;
        buff.action.status_effect.amplifier_cap = 9;
        buff.action.status_effect.refresh_duration = false;
        spell.impacts = List.of(buff);

        configureCooldown(spell, 15);
        spell.cost.exhaust = 0.2F;

        return new Entry(id, spell, title, description);
    }

    public static final Entry SHOCK_POWDER = add(shock_powder().book(Book.ROGUE));
    private static Entry shock_powder() {
        var id = Identifier.of(RoguesMod.NAMESPACE, "shock_powder");
        var title = "Shock Powder";
        var description = "Stuns nearby enemies for {effect_duration} sec.";
        var effect = RogueEffects.SHOCK;
        var spell = activeSpellBase();
        spell.range = 5;
        spell.tier = 2;

        spell.release.animation = PlayerAnimation.of("spell_engine:dual_handed_ground_release");
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
        buff.action.status_effect.apply_limit = new Spell.Impact.Action.StatusEffect.ApplyLimit();
        buff.action.status_effect.apply_limit.health_base = 50;
        buff.action.status_effect.apply_limit.spell_power_multiplier = 2F;
        buff.sound = new Sound(RogueSounds.SHOCK_POWDER_IMPACT.id());
        spell.impacts = List.of(buff);

        configureCooldown(spell, 16);
        spell.cost.exhaust = 0.3F;

        return new Entry(id, spell, title, description);
    }

    public static final Entry SHADOW_STEP = add(shadow_step().book(Book.ROGUE));
    private static Entry shadow_step() {
        var id = Identifier.of(RoguesMod.NAMESPACE, "shadow_step");
        var title = "Shadowstep";
        var description = "Step through the shadows to appear behind your target.";
        var effect = RogueEffects.SHADOW_STEP;
        var spell = activeSpellBase();
        spell.range = 15;
        spell.tier = 3;

        spell.release.animation = PlayerAnimation.of("spell_engine:one_handed_area_release");
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

        return new Entry(id, spell, title, description);
    }

    public static final Entry VANISH = add(vanish().book(Book.ROGUE));
    private static Entry vanish() {
        var id = Identifier.of(RoguesMod.NAMESPACE, "vanish");
        var title = "Vanish";
        var description = "Vanish from sight, entering stealth for {effect_duration} sec. Performing any action or taking damage will break the effect.";
        var effect = RogueEffects.STEALTH;
        var spell = activeSpellBase();
        spell.range = 0;
        spell.tier = 4;

        spell.release.animation = PlayerAnimation.of("spell_engine:dual_handed_weapon_cross");
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

        return new Entry(id, spell, title, description);
    }

    public static final Entry BEAR_TRAP = add(bear_trap().book(Book.ROGUE));
    private static Entry bear_trap() {
        var id = Identifier.of(RoguesMod.NAMESPACE, "bear_trap");
        var title = "Bear Trap";
        var description = "Set 3 bear traps around you, lasting {cloud_duration} sec. The first enemy to step into a trap springs it shut, taking {damage} damage and being held in place for {effect_duration} sec.";
        var effect = RogueEffects.BEAR_TRAP;
        var spell = activeSpellBase();
        spell.range = 0;
        spell.tier = 3;
        spell.order = 2;

        spell.release.animation = PlayerAnimation.of("spell_engine:dual_handed_ground_release");
        spell.release.sound = Sound.of(RogueSounds.BEAR_TRAP_RELEASE.id());

        spell.deliver.type = Spell.Delivery.Type.CLOUD;
        var cloud = new Spell.Delivery.Cloud();
        cloud.entity_type_id = RogueEntities.BEAR_TRAP_ID.toString();
        cloud.volume.radius = 0.6F;
        cloud.volume.area.vertical_range_multiplier = 0.5F;
        cloud.impact_tick_interval = 2;
        cloud.impact_cap = 1; // Trap: springs once, then winds down over BearTrapEntity.ATTACK_TICKS
        cloud.time_to_live_seconds = 20;
        cloud.spawn_ticks = 20;
        cloud.despawn_ticks = 15;
        cloud.spawn.sound = Sound.of(RogueSounds.BEAR_TRAP_SPAWN.id());
        cloud.impact_particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.MagicParticles.get(
                        SpellEngineParticles.MagicParticles.Shape.SPARK,
                        SpellEngineParticles.MagicParticles.Motion.DECELERATE).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        10, 0.2F, 0.3F)
                        .preSpawnTravel(1)
                        .color(Color.WHITE.toRGBA())
        };

        var placements = SpellBuilder.Placements.delayCascade(SpellBuilder.Placements.ring(3, 2F), 3);
        placements.forEach(p -> p.apply_yaw = true);
        cloud.placement = placements.get(0);
        cloud.additional_placements = placements.subList(1, placements.size());


        spell.deliver.clouds = List.of(cloud);

        var damage = new Spell.Impact();
        damage.action = new Spell.Impact.Action();
        damage.action.type = Spell.Impact.Action.Type.DAMAGE;
        damage.action.damage = new Spell.Impact.Action.Damage();
        damage.action.damage.spell_power_coefficient = 1F;
        damage.action.damage.knockback = 0F; // A trap clamps down, it doesn't fling
        damage.sound = Sound.of(RogueSounds.BEAR_TRAP_IMPACT.id());
        // Slice & Dice's converging spark shell, thinned out and dropped to the ankles — `invert()`
        // plus `preSpawnTravel` makes the sparks rush inward, reading as the jaws snapping closed.

        // Rooted, not stunned — the victim can still fight back. Gated so it can't pin a boss.
        var root = createEffectImpact(effect.id, 3);
        root.action.status_effect.apply_limit = new Spell.Impact.Action.StatusEffect.ApplyLimit();
        root.action.status_effect.apply_limit.health_base = 100;
        root.action.status_effect.apply_limit.spell_power_multiplier = 2F;

        spell.impacts = List.of(damage, root);

        configureCooldown(spell, 15);
        spell.cost.exhaust = 0.3F;

        return new Entry(id, spell, title, description);
    }

    public static final Entry WARRIOR_THROW = add(warrior_throw().book(Book.WARRIOR));
    private static Entry warrior_throw() {
        var id = Identifier.of(RoguesMod.NAMESPACE, "throw");
        var title = "Shattering Throw";
        var description = "Throw your weapon at the target, dealing {damage} damage and reducing their armor by {armor_reduction} for {effect_duration} sec.";
        var effect = RogueEffects.SHATTER;
        var spell = activeSpellBase();
        spell.range = 24;
        spell.tier = 2;

        spell.active.cast.duration = 0.5F;
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:one_handed_throw_charge");

        spell.release.animation = PlayerAnimation.of("spell_engine:one_handed_throw_release_instant");
        spell.release.sound = new Sound(RogueSounds.THROW.id());

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();

        spell.deliver.type = Spell.Delivery.Type.PROJECTILE;
        spell.deliver.projectile = new Spell.Delivery.ShootProjectile();
        spell.deliver.projectile.launch_properties.velocity = 0.8F;
        var projectile = new Spell.ProjectileData();
        projectile.homing_angle = 2F;
        projectile.perks.bounce = 1;
        projectile.client_data = new Spell.ProjectileData.Client();
        var model = SpellBuilder.ProjectileModels.heldItem();
        model.fx.light_emission = LightEmission.NONE;
        model.rotate_degrees_per_tick = -36;
        model.orientation = Spell.ProjectileModelComposite.Orientation.ALONG_MOTION;
        projectile.client_data.composite_model = SpellBuilder.ProjectileModels.composite(model);
        projectile.travel_sound_interval = 8;
        projectile.travel_sound = new Sound(RogueSounds.THROW.id());
        spell.deliver.projectile.projectile = projectile;

        var damage = new Spell.Impact();
        damage.action = new Spell.Impact.Action();
        damage.action.type = Spell.Impact.Action.Type.DAMAGE;
        damage.action.damage = new Spell.Impact.Action.Damage();
        damage.action.damage.spell_power_coefficient = 1F;
        damage.sound = new Sound(RogueSounds.THROW_IMPACT.id());

        var debuff = createEffectImpact(effect.id, 8);
        debuff.action.status_effect.apply_limit = new Spell.Impact.Action.StatusEffect.ApplyLimit();
        debuff.action.status_effect.apply_limit.health_base = 100;
        debuff.action.status_effect.apply_limit.spell_power_multiplier = 2F;
        debuff.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.dripping_blood.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        10, 0.05F, 0.3F)
        };

        spell.impacts = List.of(damage, debuff);
        configureCooldown(spell, 8);
        spell.cost.exhaust = 0.3F;

        return new Entry(id, spell, title, description);
    }

    public static final Entry THROW_NET = add(throw_net().book(Book.WARRIOR));
    private static Entry throw_net() {
        var id = Identifier.of(RoguesMod.NAMESPACE, "throw_net");
        var title = "Throw Net";
        var description = "Hurl a weighted net, dealing {damage} damage and pinning the target in place for {effect_duration} sec. The longer the cast is held, the harder it lands and the further it flies.";
        var effect = RogueEffects.NET_TRAP;
        var spell = activeSpellBase();
        spell.range = 16;
        spell.tier = 2;
        spell.order = 2;

        // Short charge: holding it scales the innate output (base impact values are the FULL-charge
        // values, scaled down toward `1 - output_scaling` as the ratio drops) and the throw distance.
        var charge = SpellBuilder.Casting.charge(spell, 0.45F);
        charge.min_release_ratio = 0.2F;
        charge.output_scaling = 0.5F; // a snap throw still lands at half damage
        charge.bonus.range_add = 12F; // 16 -> 28 blocks at full charge

        spell.active.cast.animation = PlayerAnimation.of("spell_engine:one_handed_throw_charge");
        spell.active.cast.sound = Sound.of(RogueSounds.NET_CASTING.id());

        spell.release.animation = PlayerAnimation.of("spell_engine:one_handed_throw_release_instant");
        spell.release.sound = Sound.of(RogueSounds.THROW.id());

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();

        spell.deliver.type = Spell.Delivery.Type.PROJECTILE;
        spell.deliver.projectile = new Spell.Delivery.ShootProjectile();
        spell.deliver.projectile.launch_properties.velocity = 1.0F;
        var projectile = new Spell.ProjectileData();
        projectile.homing_angle = 1F;
        projectile.client_data = new Spell.ProjectileData.Client();
        var model = SpellBuilder.ProjectileModels.model(
                Identifier.of(RoguesMod.NAMESPACE, "spell_projectile/throw_net").toString(),
                1F, LightEmission.NONE);
        model.rotate_degrees_per_tick = 12; // a thrown net tumbles slowly
        projectile.client_data.composite_model = SpellBuilder.ProjectileModels.composite(model);
        projectile.travel_sound = Sound.of(RogueSounds.NET_TRAVEL.id());
        projectile.travel_sound_interval = 8;
        spell.deliver.projectile.projectile = projectile;

        var damage = new Spell.Impact();
        damage.action = new Spell.Impact.Action();
        damage.action.type = Spell.Impact.Action.Type.DAMAGE;
        damage.action.damage = new Spell.Impact.Action.Damage();
        damage.action.damage.spell_power_coefficient = 0.1F; // full-charge value
        damage.action.damage.knockback = 0.1F; // a net tangles, it doesn't shove

        // Rooted, not stunned — the netted target can still fight back. Gated so it can't pin a boss.
        var root = createEffectImpact(effect.id, 4);
        root.action.status_effect.apply_limit = new Spell.Impact.Action.StatusEffect.ApplyLimit();
        root.action.status_effect.apply_limit.health_base = 100;
        root.action.status_effect.apply_limit.spell_power_multiplier = 2F;
        root.sound = Sound.of(RogueSounds.NET_IMPACT.id());

        spell.impacts = List.of(damage, root);

        configureCooldown(spell, 12);
        spell.cost.exhaust = 0.3F;

        return new Entry(id, spell, title, description);
    }

    public static final Entry SHOUT = add(shout().book(Book.WARRIOR));
    private static Entry shout() {
        var id = Identifier.of(RoguesMod.NAMESPACE, "shout");
        var title = "Demoralizing Shout";
        var description = "Shout at nearby enemies, reducing their attack damage by {damage_reduction} for {effect_duration} sec, and dealing a small amount of damage.";
        var effect = RogueEffects.DEMORALIZE;
        var spell = activeSpellBase();
        var radius = 12F;
        spell.range = radius;
        spell.tier = 3;

        spell.release.animation = PlayerAnimation.of("spell_engine:one_handed_shout_release");
        spell.release.sound = new Sound(RogueSounds.SHOUT_RELEASE.id());
        spell.release.particles = new ParticleBatch[]{
                SpellBuilder.Particles.area(SpellEngineParticles.area_effect_609.id())
                        .scale(radius * 0.25F)
                        .color(Color.RAGE.alpha(0.5F).toRGBA()),
        };

        // Area target
        spell.target.type = Spell.Target.Type.AREA;
        spell.target.area = new Spell.Target.Area();
        spell.target.area.vertical_range_multiplier = 0.5F;

        var debuff = createEffectImpact(effect.id, 8);
        debuff.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.ADD;
        debuff.action.status_effect.amplifier = 1;
        debuff.action.status_effect.amplifier_cap = 5;
        debuff.action.status_effect.apply_limit = new Spell.Impact.Action.StatusEffect.ApplyLimit();
        debuff.action.status_effect.apply_limit.health_base = 50;
        debuff.action.status_effect.apply_limit.spell_power_multiplier = 2F;
        debuff.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.smoke_medium.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        25, 0.2F, 0.2F)
                        .color(Color.RAGE.toRGBA())
        };
        debuff.sound = new Sound(RogueSounds.DEMORALIZE_IMPACT.id());

        var damage = new Spell.Impact();
        damage.action = new Spell.Impact.Action();
        damage.action.type = Spell.Impact.Action.Type.DAMAGE;
        damage.action.damage = new Spell.Impact.Action.Damage();
        damage.action.damage.spell_power_coefficient = 0.05F;
        damage.action.damage.knockback = 0F;

        spell.impacts = List.of(debuff, damage);

        configureCooldown(spell, 12);
        spell.cost.exhaust = 0.3F;

        return new Entry(id, spell, title, description);
    }

    public static final Entry CHARGE = add(charge().book(Book.WARRIOR));
    private static Entry charge() {
        var id = Identifier.of(RoguesMod.NAMESPACE, "charge");
        var title = "Charge";
        var description = "Increases movement speed and knockback resistance, frees you from movement impairing effects, lasts for {effect_duration} sec.";
        var effect = RogueEffects.CHARGE;
        var spell = activeSpellBase();
        spell.range = 0;
        spell.tier = 4;

        spell.release.animation = PlayerAnimation.of("spell_engine:one_handed_area_release");
        spell.release.sound = new Sound(RogueSounds.CHARGE_ACTIVATE.id());
        spell.release.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.sign_speed.id().toString(),
                        ParticleBatch.Shape.LINE_VERTICAL, ParticleBatch.Origin.CENTER,
                        1, 0.75F, 0.75F)
                        .scale(0.8F)
                        .color(Color.RAGE.toRGBA())
                        .followEntity(true),
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.STRIPE,
                                SpellEngineParticles.MagicParticles.Motion.FLOAT).id().toString(),
                        ParticleBatch.Shape.WIDE_PIPE, ParticleBatch.Origin.FEET,
                        25, 0.2F, 0.25F)
                        .extent(-0.2F)
                        .color(Color.RAGE.toRGBA()),
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.SPARK,
                                SpellEngineParticles.MagicParticles.Motion.DECELERATE).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        25, 0.1F, 0.1F)
                        .extent(0.2F)
                        .color(Color.RAGE.toRGBA()),
                new ParticleBatch(SpellEngineParticles.smoke_medium.id().toString(),
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.FEET,
                        50, 0.15F, 0.15F)
                        .preSpawnTravel(1)
        };


        var buff = createEffectImpact(effect.id, 2F);
        spell.impacts = List.of(buff);

        configureCooldown(spell, 12);
        spell.cost.exhaust = 0.4F;

        return new Entry(id, spell, title, description);
    }
}
