package net.rogues.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.rogues.RoguesMod;
import net.spell_engine.api.effect.ActionImpairing;
import net.spell_engine.api.effect.EntityActionsAllowed;
import net.spell_engine.api.effect.RemoveOnHit;
import net.spell_engine.api.effect.Synchronized;
import net.spell_engine.api.event.CombatEvents;

public class Effects {
    public static int sliceAndDiceMaxStacks() {
        return RoguesMod.tweaksConfig.value.slice_and_dice_max_stacks;
    }
    public static final StatusEffect SLICE_AND_DICE = new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0x993333);
    public static final StatusEffect SHOCK = new CustomStatusEffect(StatusEffectCategory.HARMFUL, 0xffffcc)
            .addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                    "112f3133-8a44-11ed-a1eb-0242ac120002",
                    -1F,
                    EntityAttributeModifier.Operation.MULTIPLY_BASE);
    public static final StatusEffect STEALTH = new StealthEffect(StatusEffectCategory.BENEFICIAL, 0xAAAAAA);

    public static final StatusEffect SHATTER = new CustomStatusEffect(StatusEffectCategory.HARMFUL, 0x800000);
    public static final StatusEffect DEMORALIZE = new CustomStatusEffect(StatusEffectCategory.HARMFUL, 0x800000);
    public static final StatusEffect CHARGE = new ChargeEffect(StatusEffectCategory.BENEFICIAL, 0xAAAAAA);

    public static void register() {
        Synchronized.configure(SLICE_AND_DICE, true);
        Synchronized.configure(SHOCK, true);
        ActionImpairing.configure(SHOCK, EntityActionsAllowed.STUN);
        Synchronized.configure(STEALTH, true);
        RemoveOnHit.configure(STEALTH, true);
        Synchronized.configure(SHATTER, true);
        Synchronized.configure(DEMORALIZE, true);

        var config = RoguesMod.tweaksConfig.value;
        SLICE_AND_DICE.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                "112f3133-8a44-11ed-a1eb-0242ac220002",
                config.slice_and_dice_damage_multiplier,
                EntityAttributeModifier.Operation.MULTIPLY_BASE);
        STEALTH.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                "112f3133-8a44-11ed-a1eb-0242ac320003",
                config.stealth_movement_speed_multiplier,
                EntityAttributeModifier.Operation.MULTIPLY_BASE);
        CHARGE.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                "112f3133-8a44-11ed-a1eb-0242ac420004",
                config.charge_speed_multiplier,
                EntityAttributeModifier.Operation.MULTIPLY_BASE);
        SHATTER.addAttributeModifier(EntityAttributes.GENERIC_ARMOR,
                "112f3133-8a44-11ed-a1eb-0242ac520055",
                config.shattered_armor_multiplier,
                EntityAttributeModifier.Operation.MULTIPLY_BASE);
        DEMORALIZE.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                "112f3133-8a44-11ed-a1eb-0242ac620006",
                config.shout_damage_multiplier,
                EntityAttributeModifier.Operation.MULTIPLY_BASE);

        CombatEvents.ENTITY_ATTACK.register((args) -> {
            var attacker = args.attacker();
            if (attacker.hasStatusEffect(STEALTH)) {
                attacker.removeStatusEffect(STEALTH);
            }
        });
        var vanishId = new Identifier(RoguesMod.NAMESPACE, "vanish");
        CombatEvents.SPELL_CAST.register((args) -> {
            var caster = args.caster();
            if (caster.hasStatusEffect(STEALTH) && !args.spell().id().equals(vanishId)) {
                caster.removeStatusEffect(STEALTH);
            }
        });
        CombatEvents.ITEM_USE.register((args) -> {
            var user = args.user();
            if (user.hasStatusEffect(STEALTH)) {
                user.removeStatusEffect(STEALTH);
            }
        });

        int rawId = 750;
        Registry.register(Registries.STATUS_EFFECT, rawId++, new Identifier(RoguesMod.NAMESPACE, "slice_and_dice").toString(), SLICE_AND_DICE);
        Registry.register(Registries.STATUS_EFFECT, rawId++, new Identifier(RoguesMod.NAMESPACE, "shock").toString(), SHOCK);
        Registry.register(Registries.STATUS_EFFECT, rawId++, new Identifier(RoguesMod.NAMESPACE, "stealth").toString(), STEALTH);
        Registry.register(Registries.STATUS_EFFECT, rawId++, new Identifier(RoguesMod.NAMESPACE, "shatter").toString(), SHATTER);
        Registry.register(Registries.STATUS_EFFECT, rawId++, new Identifier(RoguesMod.NAMESPACE, "demoralize").toString(), DEMORALIZE);
        Registry.register(Registries.STATUS_EFFECT, rawId++, new Identifier(RoguesMod.NAMESPACE, "charge").toString(), CHARGE);
    }
}
