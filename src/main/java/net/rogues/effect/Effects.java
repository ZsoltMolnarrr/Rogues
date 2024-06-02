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
    public static int sliceAndDiceMaxStacks = 10;
    public static final StatusEffect SLICE_AND_DICE = new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0x993333)
            .addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                    "112f3133-8a44-11ed-a1eb-0242ac120002",
                    0.1F,
                    EntityAttributeModifier.Operation.MULTIPLY_BASE);
    public static final StatusEffect SHOCK = new CustomStatusEffect(StatusEffectCategory.HARMFUL, 0xffffcc)
            .addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                    "112f3133-8a44-11ed-a1eb-0242ac120002",
                    -1F,
                    EntityAttributeModifier.Operation.MULTIPLY_BASE);
    public static final StatusEffect STEALTH = new StealthEffect(StatusEffectCategory.BENEFICIAL, 0xAAAAAA)
            .addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                    "112f3133-8a44-11ed-a1eb-0242ac120003",
                    -0.5F,
                    EntityAttributeModifier.Operation.MULTIPLY_BASE);

    public static void register() {
        Synchronized.configure(SLICE_AND_DICE, true);

        Synchronized.configure(SHOCK, true);
        ActionImpairing.configure(SHOCK, EntityActionsAllowed.STUN);

        Synchronized.configure(STEALTH, true);
        RemoveOnHit.configure(STEALTH, true);
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
    }
}
