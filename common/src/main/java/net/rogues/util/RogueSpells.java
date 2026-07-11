package net.rogues.util;

import net.minecraft.util.Identifier;
import net.rogues.RoguesMod;
import net.rogues.effect.RogueEffects;
import net.rogues.entity.RogueEntities;
import net.spell_engine.api.datagen.SpellBuilder;
import net.spell_engine.api.effect.SpellEngineEffects;
import net.spell_engine.api.render.LightEmission;
import net.spell_engine.api.spell.ExternalSpellSchools;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.api.spell.fx.PlayerAnimation;
import net.spell_engine.api.spell.fx.Sound;
import net.spell_engine.client.gui.SpellTooltip;
import net.spell_engine.client.util.Color;
import net.spell_engine.fx.SpellEngineParticles;
import net.spell_engine.fx.SpellEngineSounds;
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

    /// Class spells are melee-school but not weapon skills: they carry their own cooldowns and are
    /// learned from a book, so this deliberately does not go through `createMeleeSpell`.
    private static Spell activeSpellBase() {
        var spell = SpellBuilder.createSpellActive();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;
        return spell;
    }

    /// Caps which targets a control effect can land on, so a root or stun can't pin a boss.
    private static Spell.Impact limitByHealth(Spell.Impact impact, float healthBase, float spellPowerMultiplier) {
        var limit = new Spell.Impact.Action.StatusEffect.ApplyLimit();
        limit.health_base = healthBase;
        limit.spell_power_multiplier = spellPowerMultiplier;
        impact.action.status_effect.apply_limit = limit;
        return impact;
    }

    /// Life steal FX, mirroring SkillTree's Leeching Strike. Copied rather than shared, since
    /// SkillTree is not a dependency of Rogues.
    private static ParticleBatch[] leechImpactParticles() {
        var sparkFloat = SpellEngineParticles.MagicParticles.get(
                SpellEngineParticles.MagicParticles.Shape.SPARK,
                SpellEngineParticles.MagicParticles.Motion.FLOAT).id().toString();
        var sparkDecelerate = SpellEngineParticles.MagicParticles.get(
                SpellEngineParticles.MagicParticles.Shape.SPARK,
                SpellEngineParticles.MagicParticles.Motion.DECELERATE).id().toString();
        return new ParticleBatch[]{
                new ParticleBatch(sparkFloat,
                        ParticleBatch.Shape.WIDE_PIPE, ParticleBatch.Origin.CENTER,
                        15, 0.02F, 0.1F)
                        .color(Color.BLOOD.toRGBA()),
                new ParticleBatch(sparkDecelerate,
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        25, 0.08F, 0.12F)
                        .invert()
                        .preSpawnTravel(5)
                        .followEntity(true)
                        .color(Color.BLOOD.toRGBA()),
                new ParticleBatch(SpellEngineParticles.ground_glow.id().toString(),
                        ParticleBatch.Shape.LINE_VERTICAL, ParticleBatch.Origin.GROUND,
                        1, 0F, 0F)
                        .followEntity(true)
                        .scale(0.8F)
                        .color(Color.BLOOD.alpha(0.2F).toRGBA())
        };
    }

    /// Area target centered on the caster. `vertical_range_multiplier` keeps ground-level spells from
    /// reaching targets far above or below.
    private static void areaTarget(Spell spell, float verticalRangeMultiplier) {
        spell.target.type = Spell.Target.Type.AREA;
        spell.target.area = new Spell.Target.Area();
        spell.target.area.vertical_range_multiplier = verticalRangeMultiplier;
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

        SpellBuilder.Casting.instant(spell);
        SpellBuilder.Release.visuals(spell,
                "spell_engine:dual_handed_weapon_charge",
                new ParticleBatch[]{
                        new ParticleBatch(SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.SPARK,
                                SpellEngineParticles.MagicParticles.Motion.FLOAT).id().toString(),
                                ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.LAUNCH_POINT,
                                15, 0.15F, 0.2F)
                                .preSpawnTravel(7)
                                .invert()
                                .color(Color.WHITE.toRGBA())
                },
                Sound.of(RogueSounds.SLICE_AND_DICE.id()));

        // Each melee impact re-triggers the stash onto the caster, adding a stack.
        var stashMeleeTrigger = SpellBuilder.Triggers.meleeAttackImpact();
        stashMeleeTrigger.target_override = Spell.Trigger.TargetSelector.CASTER;
        SpellBuilder.Deliver.stash(spell, effect.id.toString(), 10, stashMeleeTrigger);
        spell.deliver.stash_effect.consume = 0; // Stacks build up; nothing spends them

        var buff = SpellBuilder.Impacts.effectAdd(effect.id.toString(), 10, 1, 9);
        buff.action.status_effect.refresh_duration = false; // The trance has a hard expiry
        spell.impacts = List.of(buff);

        SpellBuilder.Cost.cooldown(spell, 15);
        SpellBuilder.Cost.exhaust(spell, 0.2F);

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

        SpellBuilder.Casting.instant(spell);
        SpellBuilder.Release.visuals(spell,
                "spell_engine:dual_handed_ground_release",
                new ParticleBatch[]{
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
                },
                Sound.of(RogueSounds.SHOCK_POWDER_RELEASE.id()));

        areaTarget(spell, 0.5F);

        var stun = limitByHealth(SpellBuilder.Impacts.effectSet(effect.id.toString(), 3, 0), 50, 2F);
        stun.sound = Sound.of(RogueSounds.SHOCK_POWDER_IMPACT.id());
        spell.impacts = List.of(stun);

        SpellBuilder.Cost.cooldown(spell, 16);
        SpellBuilder.Cost.exhaust(spell, 0.3F);

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

        SpellBuilder.Casting.instant(spell);
        SpellBuilder.Release.visuals(spell,
                "spell_engine:one_handed_area_release",
                null,
                Sound.of(RogueSounds.SHADOW_STEP_DEPART.id()));

        SpellBuilder.Target.aim(spell);
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

        var buff = SpellBuilder.Impacts.effectSet(effect.id.toString(), 1.5F, 0);

        spell.impacts = List.of(impact, buff);

        SpellBuilder.Cost.cooldown(spell, 12);
        SpellBuilder.Cost.exhaust(spell, 0.4F);

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

        SpellBuilder.Casting.instant(spell);
        SpellBuilder.Release.visuals(spell,
                "spell_engine:dual_handed_weapon_cross",
                new ParticleBatch[]{
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
                },
                Sound.of(RogueSounds.VANISH_COMBINED.id()));

        var buff = SpellBuilder.Impacts.effectSet(effect.id.toString(), 8, 0);
        spell.impacts = List.of(buff);

        SpellBuilder.Cost.cooldown(spell, 30);
        SpellBuilder.Cost.exhaust(spell, 0.4F);

        return new Entry(id, spell, title, description);
    }

    public static final Entry BEAR_TRAP = add(bear_trap().book(Book.ROGUE));
    private static Entry bear_trap() {
        var id = Identifier.of(RoguesMod.NAMESPACE, "bear_trap");
        var title = "Bear Trap";
        var description = "Set 3 bear traps around you, lasting {cloud_duration} sec. The first enemy to step into a trap springs it shut, taking {damage} damage and being held in place for {effect_duration} sec.";
        var effect = RogueEffects.BEAR_TRAP;
        var spell = activeSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE_DUAL;
        spell.range = 0;
        spell.tier = 3;
        spell.order = 2;

        SpellBuilder.Casting.instant(spell);
        SpellBuilder.Release.visuals(spell,
                "spell_engine:dual_handed_ground_release",
                null,
                Sound.of(RogueSounds.BEAR_TRAP_RELEASE.id()));

        // Built by hand rather than via `Deliver.cloud`: that helper assumes an ambient volume with
        // presence particles and a light level, whereas a trap is a one-shot entity with spawn/despawn
        // windows and no ambient presence.
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
        // Slice & Dice's converging spark shell, thinned out and dropped to the ankles — `invert()`
        // plus `preSpawnTravel` makes the sparks rush inward, reading as the jaws snapping closed.
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

        var damage = SpellBuilder.Impacts.damage(1F, 0F); // A trap clamps down, it doesn't fling
        damage.sound = Sound.of(RogueSounds.BEAR_TRAP_IMPACT.id());

        // Rooted, not stunned — the victim can still fight back.
        var root = limitByHealth(SpellBuilder.Impacts.effectSet(effect.id.toString(), 3, 0), 100, 2F);

        spell.impacts = List.of(damage, root);

        SpellBuilder.Cost.cooldown(spell, 15);
        SpellBuilder.Cost.exhaust(spell, 0.3F);

        return new Entry(id, spell, title, description);
    }

    public static final Entry MUTILATE = add(mutilate().book(Book.ROGUE));
    private static Entry mutilate() {
        var id = Identifier.of(RoguesMod.NAMESPACE, "mutilate");
        var title = "Mutilate";
        var description = "Tear into everything in front of you with both weapons, healing you for {heal} per enemy struck. Strikes with the damage of both held weapons.";
        var spell = activeSpellBase();
        // Powered by both hands: the off-hand weapon's damage counts towards this spell.
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE_DUAL;
        spell.range = 0;
        spell.range_mechanic = Spell.RangeMechanic.MELEE;
        spell.tier = 4;
        spell.order = 2;

        SpellBuilder.Casting.instant(spell);
        SpellBuilder.Target.none(spell);

        var attack = new Spell.Delivery.Melee.Attack();
        attack.attack_speed_multiplier = 1F;
        attack.delay = 0.1F;
        attack.hitbox = new Spell.Delivery.Melee.HitBox();
        attack.hitbox.arc = 160;
        attack.hitbox.height = 0.2F;
        attack.hitbox.width = 0.5F;
        // Unlike Thrust, Mutilate stabs on the spot: no `forward_momentum`, so no slipperiness either.
//        attack.additional_strikes = 4;
//        attack.additional_strike_delay = 0.15F;
        attack.additional_hits_on_same_target = false;
        attack.animation = PlayerAnimation.of("spell_engine:weapon_dual_slash_cross");
        attack.delay = 0.5F;
        attack.swing_sound = Sound.of(SpellEngineSounds.WEAPON_SWORD_SWING.id());
        attack.impact_sound = Sound.of(SpellEngineSounds.WEAPON_DAGGER_IMPACT.id());

        SpellBuilder.Deliver.melee(spell, List.of(attack));
        spell.deliver.melee.allow_airborne = true;

        // The swing itself carries the weapon damage, so life steal is the only impact.
        var leech = SpellBuilder.Impacts.heal(0.1F);
        leech.action.apply_to_caster = true;
        leech.particles = leechImpactParticles();
        leech.sound = Sound.of(SpellEngineSounds.LEECHING_IMPACT.id());
        spell.impacts = List.of(leech);

        SpellBuilder.Cost.cooldown(spell, 12);
        SpellBuilder.Cost.exhaust(spell, 0.4F);

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

        SpellBuilder.Casting.cast(spell, 0.5F, "spell_engine:one_handed_throw_charge");
        SpellBuilder.Release.visuals(spell,
                "spell_engine:one_handed_throw_release_instant",
                null,
                Sound.of(RogueSounds.THROW.id()));

        SpellBuilder.Target.aim(spell);

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
        projectile.travel_sound = Sound.of(RogueSounds.THROW.id());
        spell.deliver.projectile.projectile = projectile;

        var damage = SpellBuilder.Impacts.damage(1F);
        damage.sound = Sound.of(RogueSounds.THROW_IMPACT.id());

        var debuff = limitByHealth(SpellBuilder.Impacts.effectSet(effect.id.toString(), 8, 0), 100, 2F);
        debuff.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.dripping_blood.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        10, 0.05F, 0.3F)
        };

        spell.impacts = List.of(damage, debuff);

        SpellBuilder.Cost.cooldown(spell, 8);
        SpellBuilder.Cost.exhaust(spell, 0.3F);

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

        // Set directly: `Casting.charge` replaces `active.cast`, and `Casting.visuals` drops the sound.
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:one_handed_throw_charge");
        spell.active.cast.sound = Sound.of(RogueSounds.NET_CASTING.id());

        SpellBuilder.Release.visuals(spell,
                "spell_engine:one_handed_throw_release_instant",
                null,
                Sound.of(RogueSounds.THROW.id()));

        SpellBuilder.Target.aim(spell);

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

        // full-charge values; a net tangles, it doesn't shove
        var damage = SpellBuilder.Impacts.damage(0.1F, 0.1F);

        // Rooted, not stunned — the netted target can still fight back.
        var root = limitByHealth(SpellBuilder.Impacts.effectSet(effect.id.toString(), 4, 0), 100, 2F);
        root.sound = Sound.of(RogueSounds.NET_IMPACT.id());

        spell.impacts = List.of(damage, root);

        SpellBuilder.Cost.cooldown(spell, 12);
        SpellBuilder.Cost.exhaust(spell, 0.3F);

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
        spell.tier = 3;

        SpellBuilder.Casting.instant(spell);
        SpellBuilder.Release.visuals(spell,
                "spell_engine:one_handed_area_release",
                new ParticleBatch[]{
                        SpellBuilder.Particles.popUpSign(SpellEngineParticles.sign_speed.id(), Color.RAGE),
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
                },
                Sound.of(RogueSounds.CHARGE_ACTIVATE.id()));

        var buff = SpellBuilder.Impacts.effectSet(effect.id.toString(), 2F, 0);
        spell.impacts = List.of(buff);

        SpellBuilder.Cost.cooldown(spell, 12);
        SpellBuilder.Cost.exhaust(spell, 0.4F);

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
        spell.order = 2;

        SpellBuilder.Casting.instant(spell);
        SpellBuilder.Release.visuals(spell,
                "spell_engine:one_handed_shout_release",
                new ParticleBatch[]{
                        SpellBuilder.Particles.area(SpellEngineParticles.area_effect_609.id())
                                .scale(radius * 0.25F)
                                .color(Color.RAGE.alpha(0.5F).toRGBA()),
                },
                Sound.of(RogueSounds.SHOUT_RELEASE.id()));

        areaTarget(spell, 0.5F);

        var debuff = limitByHealth(SpellBuilder.Impacts.effectAdd(effect.id.toString(), 8, 1, 5), 50, 2F);
        debuff.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.smoke_medium.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        25, 0.2F, 0.2F)
                        .color(Color.RAGE.toRGBA())
        };
        debuff.sound = Sound.of(RogueSounds.DEMORALIZE_IMPACT.id());

        var damage = SpellBuilder.Impacts.damage(0.05F, 0F);

        spell.impacts = List.of(debuff, damage);

        SpellBuilder.Cost.cooldown(spell, 12);
        SpellBuilder.Cost.exhaust(spell, 0.3F);

        return new Entry(id, spell, title, description);
    }

    public static final Entry LAST_STAND = add(last_stand().book(Book.WARRIOR));
    private static Entry last_stand() {
        var id = Identifier.of(RoguesMod.NAMESPACE, "last_stand");
        var title = "Last Stand";
        var description = "Brace yourself, rooted in place while channeling. Each of the {effect_amplifier_cap} stacks increases your maximum health and reduces damage taken, up to +100% health and -50% damage taken at a full channel. Lasts {effect_duration} sec.";
        var effect = RogueEffects.LAST_STAND;
        var stacks = 5;

        // HEALTH school by design: this is a survival cooldown, tinted and grouped with health effects.
        var spell = SpellBuilder.createSpellActive();
        spell.school = ExternalSpellSchools.HEALTH;
        spell.range = 0;
        spell.tier = 4;
        spell.order = 2;

        // One stack per channel tick. `movement_speed = 0` roots the caster for the channel's duration.
        SpellBuilder.Casting.channel(spell, 2.5F, stacks);
        spell.active.cast.movement_speed = 0F;
        spell.active.cast.animation = PlayerAnimation.of("spell_engine:one_handed_ground_charge");
        spell.active.cast.start_sound = new Sound(SpellEngineSounds.GENERIC_HEALING_CASTING.id());
        spell.active.cast.sound = new Sound(SpellEngineSounds.GENERIC_HEALING_CASTING.id(), 0);
        spell.active.cast.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.MagicParticles.get(
                        SpellEngineParticles.MagicParticles.Shape.SPARK,
                        SpellEngineParticles.MagicParticles.Motion.DECELERATE).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        8, 0.2F, 0.3F)
                        .preSpawnTravel(6)
                        .invert()
                        .color(Color.BLOOD.toRGBA()),
                new ParticleBatch(SpellEngineParticles.smoke_medium.id().toString(),
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.FEET,
                        6, 0.05F, 0.1F)
                        .color(Color.BLOOD.alpha(0.5F).toRGBA())
        };

        SpellBuilder.Release.visuals(spell,
                null,
                null,
                new Sound(SpellEngineSounds.GENERIC_HEALING_RELEASE.id()));

        spell.target.type = Spell.Target.Type.CASTER;

        // Each channel tick adds a stack; the effect's per-stack modifiers do the scaling.
        var buff = SpellBuilder.Impacts.effectAdd(effect.id.toString(), 10, 1, stacks - 1);
        spell.impacts = List.of(buff);

        // Proportional: releasing early (fewer stacks) shortens the cooldown, like Evocation.
        SpellBuilder.Cost.cooldown(spell, 60);
        spell.cost.cooldown.proportional = true;
        SpellBuilder.Cost.exhaust(spell, 0.3F);

        return new Entry(id, spell, title, description);
    }

    public static final Entry MORTAL_STRIKE = add(mortal_strike().book(Book.WARRIOR));
    private static Entry mortal_strike() {
        var id = Identifier.of(RoguesMod.NAMESPACE, "mortal_strike");
        var title = "Mortal Strike";
        var description = "Perform an overhead strike for {weapon_damage_bonus} bonus weapon damage, causing the target to Bleed for {effect_duration} sec.";
        var spell = activeSpellBase(); // PHYSICAL_MELEE
        spell.range = 0;
        spell.range_mechanic = Spell.RangeMechanic.MELEE;
        spell.tier = 4;

        // Wind up on the cast (the jump), slam down on the melee attack — GROUND_SLAM's two clips.
        SpellBuilder.Casting.cast(spell, 1F);
        spell.active.cast.animation = PlayerAnimation.of("rogues:mortal_strike_windup");
        spell.active.cast.animation_pitch = false;
        spell.active.cast.start_sound = new Sound(SpellEngineSounds.WEAPON_HAMMER_SWING.id());
        // spell.release.sound = new Sound(SpellEngineSounds.WEAPON_GROUND_SLAM.id());

        SpellBuilder.Target.none(spell);

        var slam = new Spell.Delivery.Melee.Attack();
        slam.damage_bonus = 0.5F;
        slam.attack_speed_multiplier = 1F;
        slam.delay = 0.3F;
        slam.hitbox = new Spell.Delivery.Melee.HitBox();
        // Vertical overhead chop: `roll = 90` tips the swept arc onto its side so it sweeps
        // top-to-bottom, and the tall/narrow box matches the downward slam.
        slam.hitbox.arc = 120;
        slam.hitbox.roll = 90F;
        slam.hitbox.height = 1.5F;
        slam.hitbox.width = 0.5F;
        slam.animation = PlayerAnimation.of("rogues:mortal_strike_slash");
        // slam.swing_sound = Sound.of(SpellEngineSounds.WEAPON_HAMMER_SWING.id());
        slam.impact_sound = Sound.of(SpellEngineSounds.WEAPON_GROUND_SLAM.id());

        SpellBuilder.Deliver.melee(spell, List.of(slam));
        spell.deliver.melee.allow_airborne = false;

        // The swing's weapon damage lands via player.attack(); this bleed rides on top, on the target.
        var bleed = SpellBuilder.Impacts.effectSet(SpellEngineEffects.BLEED.id.toString(), 6, 1);
        bleed.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.dripping_blood.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        10, 0.05F, 0.3F)
        };
        spell.impacts = List.of(bleed);

        SpellBuilder.Cost.cooldown(spell, 15);
        SpellBuilder.Cost.exhaust(spell, 0.4F);

        return new Entry(id, spell, title, description);
    }
}
