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
            "Slice and Dice",
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
            "Shadow Step",
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

    public static final Effects.Entry CHARGE = add(new Effects.Entry(
            Identifier.of(RoguesMod.NAMESPACE, "charge"),
            "Charge",
            "Increased movement",
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


    public static void register(ConfigFile.Effects config) {
        Synchronized.configure(SLICE_AND_DICE.effect, true);
        Synchronized.configure(SHOCK.effect, true);
        ActionImpairing.configure(SHOCK.effect, EntityActionsAllowed.STUN);
        Synchronized.configure(STEALTH.effect, true);
        RemoveOnHit.configure(STEALTH.effect, true);

        Synchronized.configure(SHATTER.effect, true);
        Synchronized.configure(DEMORALIZE.effect, true);
        Synchronized.configure(CHARGE.effect, true);

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
