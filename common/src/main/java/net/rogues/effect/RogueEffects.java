package net.rogues.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.Registries;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;
import net.rogues.RoguesMod;
import net.spell_engine.rpg_series.config.AttributeModifier;
import net.spell_engine.rpg_series.config.ConfigFile;
import net.spell_engine.rpg_series.config.EffectConfig;
import net.spell_engine.api.effect.*;
import net.spell_engine.api.event.CombatEvents;
import net.spell_engine.api.spell.event.SpellEvents;

import java.util.ArrayList;
import java.util.List;

public class RogueEffects {
    /// 1.20.1 `EntityAttribute` has no `getIdAsString()` — resolve through the registry instead.
    private static String attributeId(EntityAttribute attribute) {
        return Registries.ATTRIBUTE.getId(attribute).toString();
    }

    public static final List<Effects.Entry> entries = new ArrayList<>();
    private static Effects.Entry add(Effects.Entry entry) {
        entries.add(entry);
        return entry;
    }

    public static Effects.Entry SLICE_AND_DICE = add(new Effects.Entry(
            new Identifier(RoguesMod.NAMESPACE, "slice_and_dice"),
            "Slice & Dice",
            "Increases attack damage",
            new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0x993333),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            attributeId(EntityAttributes.GENERIC_ATTACK_DAMAGE),
                            0.1F,
                            EntityAttributeModifier.Operation.MULTIPLY_BASE
                    )
            ))
    ));

    public static Effects.Entry SHOCK = add(new Effects.Entry(
            new Identifier(RoguesMod.NAMESPACE, "shock"),
            "Stunned",
            "Prevents movement and actions",
            new CustomStatusEffect(StatusEffectCategory.HARMFUL, 0xffffcc),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            attributeId(EntityAttributes.GENERIC_MOVEMENT_SPEED),
                            -1F,
                            EntityAttributeModifier.Operation.MULTIPLY_BASE
                    )
            ))
    ));

    public static Effects.Entry SHADOW_STEP = add(new Effects.Entry(
            new Identifier(RoguesMod.NAMESPACE, "shadow_step"),
            "Shadowstep",
            "Untraceable",
            new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0xAAAAAA)
    ));

    public static Effects.Entry STEALTH = add(new Effects.Entry(
            new Identifier(RoguesMod.NAMESPACE, "stealth"),
            "Stealth",
            "Invisible to enemies",
            new StealthEffect(StatusEffectCategory.BENEFICIAL, 0xAAAAAA),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            attributeId(EntityAttributes.GENERIC_MOVEMENT_SPEED),
                            -0.5F,
                            EntityAttributeModifier.Operation.MULTIPLY_BASE
                    )
            ))
    ));
    public static Effects.Entry STEALTH_SPEED = add(new Effects.Entry(
            new Identifier(RoguesMod.NAMESPACE, "stealth_speed"),
            "Stealth Speed",
            "Faster movement in stealth",
            new StealthEffect(StatusEffectCategory.BENEFICIAL, 0xAAAAAA),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            attributeId(EntityAttributes.GENERIC_MOVEMENT_SPEED),
                            0.5F,
                            EntityAttributeModifier.Operation.MULTIPLY_BASE
                    )
            ))
    ));

    public static Effects.Entry SHATTER = add(new Effects.Entry(
            new Identifier(RoguesMod.NAMESPACE, "shatter"),
            "Shattered Armor",
            "Reduces armor",
            new CustomStatusEffect(StatusEffectCategory.HARMFUL, 0x800000),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            attributeId(EntityAttributes.GENERIC_ARMOR),
                            -0.3F,
                            EntityAttributeModifier.Operation.MULTIPLY_BASE
                    )
            ))
    ));

    public static final Effects.Entry DEMORALIZE = add(new Effects.Entry(
            new Identifier(RoguesMod.NAMESPACE, "demoralize"),
            "Demoralized",
            "Reduces attack damage",
            new CustomStatusEffect(StatusEffectCategory.HARMFUL, 0x800000),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            attributeId(EntityAttributes.GENERIC_ATTACK_DAMAGE),
                            -0.2F,
                            EntityAttributeModifier.Operation.MULTIPLY_BASE
                    )
            ))
    ));

    public static final Effects.Entry BEAR_TRAP = add(new Effects.Entry(
            new Identifier(RoguesMod.NAMESPACE, "bear_trap"),
            "Trapped",
            "Prevents movement and jumping",
            new CustomStatusEffect(StatusEffectCategory.HARMFUL, 0x6E6E6E),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            attributeId(EntityAttributes.GENERIC_MOVEMENT_SPEED),
                            -2F,
                            EntityAttributeModifier.Operation.MULTIPLY_BASE
                    )
            ))
    ));

    public static final Effects.Entry NET_TRAP = add(new Effects.Entry(
            new Identifier(RoguesMod.NAMESPACE, "net_trap"),
            "Netted",
            "Prevents movement and jumping",
            new CustomStatusEffect(StatusEffectCategory.HARMFUL, 0x8B7355),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            attributeId(EntityAttributes.GENERIC_MOVEMENT_SPEED),
                            -2F,
                            EntityAttributeModifier.Operation.MULTIPLY_BASE
                    ),
                    new AttributeModifier(
                            attributeId(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE),
                            100F,
                            EntityAttributeModifier.Operation.MULTIPLY_BASE
                    )
            ))
    ));

    public static final Effects.Entry CHARGE = add(new Effects.Entry(
            new Identifier(RoguesMod.NAMESPACE, "charge"),
            "Charge",
            "Increases movement speed",
            // Ticking so spells can hook it via an EFFECT_TICK trigger — the freedom from movement
            // impairing effects now comes from the Warrior tree's Improved Charge node rather than
            // being baked into the effect. 5-tick interval keeps that dispel responsive (0.25s).
            new TickingStatusEffect(StatusEffectCategory.BENEFICIAL, 0xAAAAAA).interval(5),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            attributeId(EntityAttributes.GENERIC_MOVEMENT_SPEED),
                            0.5F,
                            EntityAttributeModifier.Operation.MULTIPLY_BASE
                    ),
                    new AttributeModifier(
                            attributeId(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE),
                            0.5F,
                            EntityAttributeModifier.Operation.MULTIPLY_BASE
                    )
            ))
    ));

    /// Per-stack fortification for Last Stand. Channelled up to 5 stacks (one per channel tick):
    /// +20% max health and -10% damage taken each, reaching +100% / -50% at a full channel.
    /// `damage_taken` has a base of 100, so `ADD_MULTIPLIED_BASE` of -0.1 lowers the multiplier to
    /// 0.9 per stack.
    public static final Effects.Entry LAST_STAND = add(new Effects.Entry(
            new Identifier(RoguesMod.NAMESPACE, "last_stand"),
            "Last Stand",
            "Increases maximum health and reduces damage taken",
            new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0xcc0000),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            attributeId(EntityAttributes.GENERIC_MAX_HEALTH),
                            0.2F,
                            EntityAttributeModifier.Operation.MULTIPLY_BASE
                    ),
                    new AttributeModifier(
                            attributeId(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE),
                            0.2F,
                            EntityAttributeModifier.Operation.MULTIPLY_BASE
                    )
            ))
    ));

    /// A root: pins the target in place without silencing them. `canMove = false` makes
    /// `LivingEntity.isImmobile()` report true, which zeroes movement input for mobs and players
    /// alike, and `canJump = false` cancels `jump()`. Everything else stays allowed — a leg in a bear
    /// trap doesn't stop you swinging, nor does a net over your head. SpellEngine ships no preset for
    /// this (`STUN` also blocks attacking, item use and casting), hence the literal.
    ///
    /// `SemanticType.NONE` because no *action* is blocked, so there is no HUD message to raise; it is
    /// also the lowest ordinal, so it never overrides a real stun's reason when both are applied.
    private static final EntityActionsAllowed ROOT = new EntityActionsAllowed(
            false, false,
            new EntityActionsAllowed.PlayersAllowed(true, true, true),
            new EntityActionsAllowed.MobsAllowed(true),
            EntityActionsAllowed.SemanticType.NONE);

    public static void register(ConfigFile.Effects config) {
        Synchronized.configure(SLICE_AND_DICE.effect, true);
        Synchronized.configure(SHOCK.effect, true);
        ActionImpairing.configure(SHOCK.effect, EntityActionsAllowed.STUN);
        // Synchronized so the client player's own input is blocked (ClientPlayerActionImpairing),
        // not just the server-side movement.
        Synchronized.configure(BEAR_TRAP.effect, true);
        ActionImpairing.configure(BEAR_TRAP.effect, ROOT);
        Synchronized.configure(NET_TRAP.effect, true);
        ActionImpairing.configure(NET_TRAP.effect, ROOT);
        KnockbackImmunity.configure(NET_TRAP.effect);
        Synchronized.configure(STEALTH.effect, true);
        RemoveOnHit.configure(STEALTH.effect, RemoveOnHit.Trigger.ANY_HIT);
        // 15% alpha white — fades the whole appearance (armor included) wherever the entity
        // is rendered at all; vanilla invisibility (see LivingEntityStealth) still hides the
        // body from entities the stealth is meant to hide from.
        EntityTints.register(STEALTH.effect, 0x26FFFFFF);

        Synchronized.configure(SHATTER.effect, true);
        Synchronized.configure(DEMORALIZE.effect, true);
        Synchronized.configure(CHARGE.effect, true);
        Synchronized.configure(LAST_STAND.effect, true);

        CombatEvents.ENTITY_ANY_ATTACK.register((args) -> {
            var attacker = args.attacker();
            if (attacker.hasStatusEffect(STEALTH.effect)) {
                attacker.removeStatusEffect(STEALTH.effect);
            }
        });
        var vanishId = new Identifier(RoguesMod.NAMESPACE, "vanish");
        SpellEvents.SPELL_CAST.register((args) -> {
            var caster = args.caster();
            var spellId = args.spell().getKey().get().getValue();
            if (caster.hasStatusEffect(STEALTH.effect) && !spellId.equals(vanishId)) {
                caster.removeStatusEffect(STEALTH.effect);
            }
        });
        CombatEvents.ITEM_USE.register((args) -> {
            var user = args.user();
            if (user.hasStatusEffect(STEALTH.effect)) {
                user.removeStatusEffect(STEALTH.effect);
            }
        });
        OnRemoval.configure(STEALTH.effect, (context) -> {
            StealthEffect.onRemove(context.entity());
            if (context.entity().hasStatusEffect(STEALTH_SPEED.effect)) {
                context.entity().removeStatusEffect(STEALTH_SPEED.effect);
            }
        });

        Effects.register(entries, config.effects);
    }
}
