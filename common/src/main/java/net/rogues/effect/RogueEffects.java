package net.rogues.effect;

import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
    public static final List<Effects.Entry> entries = new ArrayList<>();
    private static Effects.Entry add(Effects.Entry entry) {
        entries.add(entry);
        return entry;
    }

    public static Effects.Entry SLICE_AND_DICE = add(new Effects.Entry(
            Identifier.fromNamespaceAndPath(RoguesMod.NAMESPACE, "slice_and_dice"),
            "Slice & Dice",
            "Increases attack damage",
            new CustomStatusEffect(MobEffectCategory.BENEFICIAL, 0x993333),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            Attributes.ATTACK_DAMAGE.getRegisteredName(),
                            0.1F,
                            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            ))
    ));

    public static Effects.Entry SHOCK = add(new Effects.Entry(
            Identifier.fromNamespaceAndPath(RoguesMod.NAMESPACE, "shock"),
            "Stunned",
            "Prevents movement and actions",
            new CustomStatusEffect(MobEffectCategory.HARMFUL, 0xffffcc),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            Attributes.MOVEMENT_SPEED.getRegisteredName(),
                            -1F,
                            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    ),
                    new AttributeModifier(
                            Attributes.JUMP_STRENGTH.getRegisteredName(),
                            -1F,
                            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            ))
    ));

    public static Effects.Entry SHADOW_STEP = add(new Effects.Entry(
            Identifier.fromNamespaceAndPath(RoguesMod.NAMESPACE, "shadow_step"),
            "Shadowstep",
            "Untraceable",
            new CustomStatusEffect(MobEffectCategory.BENEFICIAL, 0xAAAAAA)
    ));

    public static Effects.Entry STEALTH = add(new Effects.Entry(
            Identifier.fromNamespaceAndPath(RoguesMod.NAMESPACE, "stealth"),
            "Stealth",
            "Invisible to enemies",
            new StealthEffect(MobEffectCategory.BENEFICIAL, 0xAAAAAA),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            Attributes.MOVEMENT_SPEED.getRegisteredName(),
                            -0.5F,
                            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            ))
    ));
    public static Effects.Entry STEALTH_SPEED = add(new Effects.Entry(
            Identifier.fromNamespaceAndPath(RoguesMod.NAMESPACE, "stealth_speed"),
            "Stealth Speed",
            "Faster movement in stealth",
            new StealthEffect(MobEffectCategory.BENEFICIAL, 0xAAAAAA),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            Attributes.MOVEMENT_SPEED.getRegisteredName(),
                            0.5F,
                            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            ))
    ));

    public static Effects.Entry SHATTER = add(new Effects.Entry(
            Identifier.fromNamespaceAndPath(RoguesMod.NAMESPACE, "shatter"),
            "Shattered Armor",
            "Reduces armor",
            new CustomStatusEffect(MobEffectCategory.HARMFUL, 0x800000),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            Attributes.ARMOR.getRegisteredName(),
                            -0.3F,
                            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            ))
    ));

    public static final Effects.Entry DEMORALIZE = add(new Effects.Entry(
            Identifier.fromNamespaceAndPath(RoguesMod.NAMESPACE, "demoralize"),
            "Demoralized",
            "Reduces attack damage",
            new CustomStatusEffect(MobEffectCategory.HARMFUL, 0x800000),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            Attributes.ATTACK_DAMAGE.getRegisteredName(),
                            -0.2F,
                            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            ))
    ));

    public static final Effects.Entry BEAR_TRAP = add(new Effects.Entry(
            Identifier.fromNamespaceAndPath(RoguesMod.NAMESPACE, "bear_trap"),
            "Trapped",
            "Prevents movement and jumping",
            new CustomStatusEffect(MobEffectCategory.HARMFUL, 0x6E6E6E),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            Attributes.MOVEMENT_SPEED.getRegisteredName(),
                            -2F,
                            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    ),
                    new AttributeModifier(
                            Attributes.JUMP_STRENGTH.getRegisteredName(),
                            -2F,
                            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            ))
    ));

    public static final Effects.Entry NET_TRAP = add(new Effects.Entry(
            Identifier.fromNamespaceAndPath(RoguesMod.NAMESPACE, "net_trap"),
            "Netted",
            "Prevents movement and jumping",
            new CustomStatusEffect(MobEffectCategory.HARMFUL, 0x8B7355),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            Attributes.MOVEMENT_SPEED.getRegisteredName(),
                            -2F,
                            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    ),
                    new AttributeModifier(
                            Attributes.JUMP_STRENGTH.getRegisteredName(),
                            -2F,
                            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    ),
                    new AttributeModifier(
                            Attributes.KNOCKBACK_RESISTANCE.getRegisteredName(),
                            100F,
                            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            ))
    ));

    public static final Effects.Entry CHARGE = add(new Effects.Entry(
            Identifier.fromNamespaceAndPath(RoguesMod.NAMESPACE, "charge"),
            "Charge",
            "Increases movement speed",
            // Ticking so spells can hook it via an EFFECT_TICK trigger — the freedom from movement
            // impairing effects now comes from the Warrior tree's Improved Charge node rather than
            // being baked into the effect. 5-tick interval keeps that dispel responsive (0.25s).
            new TickingStatusEffect(MobEffectCategory.BENEFICIAL, 0xAAAAAA).interval(5),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            Attributes.MOVEMENT_SPEED.getRegisteredName(),
                            0.5F,
                            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    ),
                    new AttributeModifier(
                            Attributes.KNOCKBACK_RESISTANCE.getRegisteredName(),
                            0.5F,
                            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            ))
    ));

    /// Per-stack fortification for Last Stand. Channelled up to 5 stacks (one per channel tick):
    /// +20% max health and -10% damage taken each, reaching +100% / -50% at a full channel.
    /// `damage_taken` has a base of 100, so `ADD_MULTIPLIED_BASE` of -0.1 lowers the multiplier to
    /// 0.9 per stack.
    public static final Effects.Entry LAST_STAND = add(new Effects.Entry(
            Identifier.fromNamespaceAndPath(RoguesMod.NAMESPACE, "last_stand"),
            "Last Stand",
            "Increases maximum health and reduces damage taken",
            new CustomStatusEffect(MobEffectCategory.BENEFICIAL, 0xcc0000),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            Attributes.MAX_HEALTH.getRegisteredName(),
                            0.2F,
                            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    ),
                    new AttributeModifier(
                            Attributes.KNOCKBACK_RESISTANCE.getRegisteredName(),
                            0.2F,
                            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE
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
            if (attacker.hasEffect(STEALTH.entry)) {
                attacker.removeEffect(STEALTH.entry);
            }
        });
        var vanishId = Identifier.fromNamespaceAndPath(RoguesMod.NAMESPACE, "vanish");
        SpellEvents.SPELL_CAST.register((args) -> {
            var caster = args.caster();
            var spellId = args.spell().unwrapKey().get().identifier();
            if (caster.hasEffect(STEALTH.entry) && !spellId.equals(vanishId)) {
                caster.removeEffect(STEALTH.entry);
            }
        });
        CombatEvents.ITEM_USE.register((args) -> {
            var user = args.user();
            if (user.hasEffect(STEALTH.entry)) {
                user.removeEffect(STEALTH.entry);
            }
        });
        OnRemoval.configure(STEALTH.effect, (context) -> {
            StealthEffect.onRemove(context.entity());
            if (context.entity().hasEffect(STEALTH_SPEED.entry)) {
                context.entity().removeEffect(STEALTH_SPEED.entry);
            }
        });

        Effects.register(entries, config.effects);
    }
}
