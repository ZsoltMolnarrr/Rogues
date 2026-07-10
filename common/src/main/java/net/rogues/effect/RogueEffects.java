package net.rogues.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;
import net.rogues.RoguesMod;
import net.spell_engine.api.config.AttributeModifier;
import net.spell_engine.api.config.ConfigFile;
import net.spell_engine.api.config.EffectConfig;
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
            Identifier.of(RoguesMod.NAMESPACE, "slice_and_dice"),
            "Slice & Dice",
            "Increases attack damage",
            new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0x993333),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            EntityAttributes.GENERIC_ATTACK_DAMAGE.getIdAsString(),
                            0.1F,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            ))
    ));

    public static Effects.Entry SHOCK = add(new Effects.Entry(
            Identifier.of(RoguesMod.NAMESPACE, "shock"),
            "Stunned",
            "Prevents movement and actions",
            new CustomStatusEffect(StatusEffectCategory.HARMFUL, 0xffffcc),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            EntityAttributes.GENERIC_MOVEMENT_SPEED.getIdAsString(),
                            -1F,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    ),
                    new AttributeModifier(
                            EntityAttributes.GENERIC_JUMP_STRENGTH.getIdAsString(),
                            -1F,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            ))
    ));

    public static Effects.Entry SHADOW_STEP = add(new Effects.Entry(
            Identifier.of(RoguesMod.NAMESPACE, "shadow_step"),
            "Shadowstep",
            "Untraceable",
            new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0xAAAAAA)
    ));

    public static Effects.Entry STEALTH = add(new Effects.Entry(
            Identifier.of(RoguesMod.NAMESPACE, "stealth"),
            "Stealth",
            "Invisible to enemies",
            new StealthEffect(StatusEffectCategory.BENEFICIAL, 0xAAAAAA),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            EntityAttributes.GENERIC_MOVEMENT_SPEED.getIdAsString(),
                            -0.5F,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            ))
    ));
    public static Effects.Entry STEALTH_SPEED = add(new Effects.Entry(
            Identifier.of(RoguesMod.NAMESPACE, "stealth_speed"),
            "Stealth Speed",
            "Faster movement in stealth",
            new StealthEffect(StatusEffectCategory.BENEFICIAL, 0xAAAAAA),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            EntityAttributes.GENERIC_MOVEMENT_SPEED.getIdAsString(),
                            0.5F,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            ))
    ));

    public static Effects.Entry SHATTER = add(new Effects.Entry(
            Identifier.of(RoguesMod.NAMESPACE, "shatter"),
            "Shattered Armor",
            "Reduces armor",
            new CustomStatusEffect(StatusEffectCategory.HARMFUL, 0x800000),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            EntityAttributes.GENERIC_ARMOR.getIdAsString(),
                            -0.3F,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            ))
    ));

    public static final Effects.Entry DEMORALIZE = add(new Effects.Entry(
            Identifier.of(RoguesMod.NAMESPACE, "demoralize"),
            "Demoralized",
            "Reduces attack damage",
            new CustomStatusEffect(StatusEffectCategory.HARMFUL, 0x800000),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            EntityAttributes.GENERIC_ATTACK_DAMAGE.getIdAsString(),
                            -0.2F,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            ))
    ));

    public static final Effects.Entry BEAR_TRAP = add(new Effects.Entry(
            Identifier.of(RoguesMod.NAMESPACE, "bear_trap"),
            "Trapped",
            "Prevents movement and jumping",
            new CustomStatusEffect(StatusEffectCategory.HARMFUL, 0x6E6E6E),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            EntityAttributes.GENERIC_MOVEMENT_SPEED.getIdAsString(),
                            -2F,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    ),
                    new AttributeModifier(
                            EntityAttributes.GENERIC_JUMP_STRENGTH.getIdAsString(),
                            -2F,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            ))
    ));

    public static final Effects.Entry NET_TRAP = add(new Effects.Entry(
            Identifier.of(RoguesMod.NAMESPACE, "net_trap"),
            "Netted",
            "Prevents movement and jumping",
            new CustomStatusEffect(StatusEffectCategory.HARMFUL, 0x8B7355),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            EntityAttributes.GENERIC_MOVEMENT_SPEED.getIdAsString(),
                            -2F,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    ),
                    new AttributeModifier(
                            EntityAttributes.GENERIC_JUMP_STRENGTH.getIdAsString(),
                            -2F,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    ),
                    new AttributeModifier(
                            EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE.getIdAsString(),
                            100F,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            ))
    ));

    public static final Effects.Entry CHARGE = add(new Effects.Entry(
            Identifier.of(RoguesMod.NAMESPACE, "charge"),
            "Charge",
            "Increases movement speed",
            new ChargeEffect(StatusEffectCategory.BENEFICIAL, 0xAAAAAA),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            EntityAttributes.GENERIC_MOVEMENT_SPEED.getIdAsString(),
                            0.5F,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    ),
                    new AttributeModifier(
                            EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE.getIdAsString(),
                            0.5F,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            ))
    ));

    /// Per-stack fortification for Last Stand. Channelled up to 5 stacks (one per channel tick):
    /// +20% max health and -10% damage taken each, reaching +100% / -50% at a full channel.
    /// `damage_taken` has a base of 100, so `ADD_MULTIPLIED_BASE` of -0.1 lowers the multiplier to
    /// 0.9 per stack.
    public static final Effects.Entry LAST_STAND = add(new Effects.Entry(
            Identifier.of(RoguesMod.NAMESPACE, "last_stand"),
            "Last Stand",
            "Increases maximum health and reduces damage taken",
            new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0xcc0000),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            EntityAttributes.GENERIC_MAX_HEALTH.getIdAsString(),
                            0.2F,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    ),
                    new AttributeModifier(
                            "spell_engine:damage_taken",
                            -0.1F,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            ))
    ));

    /// Reckless frenzy applied to the caster by Mortal Strike: +100% critical strike chance at the
    /// cost of +100% damage taken. `critical_chance` and `damage_taken` both have a base of 100, so
    /// `ADD_MULTIPLIED_BASE` of +1.0 doubles each — guaranteed crits, but every hit lands twice as hard.
    public static final Effects.Entry RECKLESSNESS = add(new Effects.Entry(
            Identifier.of(RoguesMod.NAMESPACE, "recklessness"),
            "Recklessness",
            "Increases critical strike chance, but also damage taken",
            new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0xcc0000),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            "critical_strike:chance",
                            1.0F,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    ),
                    new AttributeModifier(
                            "spell_engine:damage_taken",
                            1.0F,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
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
        Synchronized.configure(STEALTH.effect, true);
        RemoveOnHit.configure(STEALTH.effect, true);

        Synchronized.configure(SHATTER.effect, true);
        Synchronized.configure(DEMORALIZE.effect, true);
        Synchronized.configure(CHARGE.effect, true);
        Synchronized.configure(LAST_STAND.effect, true);
        Synchronized.configure(RECKLESSNESS.effect, true);

        CombatEvents.ENTITY_ANY_ATTACK.register((args) -> {
            var attacker = args.attacker();
            if (attacker.hasStatusEffect(STEALTH.entry)) {
                attacker.removeStatusEffect(STEALTH.entry);
            }
        });
        var vanishId = Identifier.of(RoguesMod.NAMESPACE, "vanish");
        SpellEvents.SPELL_CAST.register((args) -> {
            var caster = args.caster();
            var spellId = args.spell().getKey().get().getValue();
            if (caster.hasStatusEffect(STEALTH.entry) && !spellId.equals(vanishId)) {
                caster.removeStatusEffect(STEALTH.entry);
            }
        });
        CombatEvents.ITEM_USE.register((args) -> {
            var user = args.user();
            if (user.hasStatusEffect(STEALTH.entry)) {
                user.removeStatusEffect(STEALTH.entry);
            }
        });
        OnRemoval.configure(STEALTH.effect, (context) -> {
            StealthEffect.onRemove(context.entity());
            if (context.entity().hasStatusEffect(STEALTH_SPEED.entry)) {
                context.entity().removeStatusEffect(STEALTH_SPEED.entry);
            }
        });

        Effects.register(entries, config.effects);
    }
}
