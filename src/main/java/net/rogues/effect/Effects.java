package net.rogues.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.rogues.RoguesMod;
import net.spell_engine.api.effect.ActionImpairing;
import net.spell_engine.api.effect.EntityActionsAllowed;
import net.spell_engine.api.effect.RemoveOnHit;
import net.spell_engine.api.effect.Synchronized;
import net.spell_engine.api.event.CombatEvents;

import java.util.ArrayList;

public class Effects {
    private static final ArrayList<Entry> entries = new ArrayList<>();
    public static class Entry {
        public final Identifier id;
        public final StatusEffect effect;
        public RegistryEntry<StatusEffect> registryEntry;

        public Entry(String name, StatusEffect effect) {
            this.id = Identifier.of(RoguesMod.NAMESPACE, name);
            this.effect = effect;
            entries.add(this);
        }

        public void register() {
            registryEntry = Registry.registerReference(Registries.STATUS_EFFECT, id, effect);
        }

        public Identifier modifierId() {
            return Identifier.of(RoguesMod.NAMESPACE, "effect." + id.getPath());
        }
    }

    public static int sliceAndDiceMaxStacks() {
        return RoguesMod.tweaksConfig.value.slice_and_dice_max_stacks;
    }
    
    public static final Entry SLICE_AND_DICE = new Entry("slice_and_dice", 
            new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0x993333));
    public static final Entry SHOCK = new Entry("shock",
            new CustomStatusEffect(StatusEffectCategory.HARMFUL, 0xffffcc));
    public static final Entry SHADOW_STEP = new Entry("shadow_step",
            new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0xAAAAAA));
    public static final Entry STEALTH = new Entry("stealth",
            new StealthEffect(StatusEffectCategory.BENEFICIAL, 0xAAAAAA));

    public static final Entry SHATTER = new Entry("shatter",
            new CustomStatusEffect(StatusEffectCategory.HARMFUL, 0x800000));
    public static final Entry DEMORALIZE = new Entry("demoralize",
            new CustomStatusEffect(StatusEffectCategory.HARMFUL, 0x800000));
    public static final Entry CHARGE = new Entry("charge",
            new ChargeEffect(StatusEffectCategory.BENEFICIAL, 0xAAAAAA));

    public static void register() {
        Synchronized.configure(SLICE_AND_DICE.effect, true);
        Synchronized.configure(SHOCK.effect, true);
        ActionImpairing.configure(SHOCK.effect, EntityActionsAllowed.STUN);
        Synchronized.configure(STEALTH.effect, true);
        RemoveOnHit.configure(STEALTH.effect, true);

        Synchronized.configure(SHATTER.effect, true);
        Synchronized.configure(DEMORALIZE.effect, true);
        Synchronized.configure(CHARGE.effect, true);

        var config = RoguesMod.tweaksConfig.value;
        SHOCK.effect.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                SHOCK.modifierId(),
                -1F,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        SLICE_AND_DICE.effect.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                SLICE_AND_DICE.modifierId(),
                config.slice_and_dice_damage_multiplier,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        STEALTH.effect.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                STEALTH.modifierId(),
                config.stealth_movement_speed_multiplier,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        SHATTER.effect.addAttributeModifier(EntityAttributes.GENERIC_ARMOR,
                SHATTER.modifierId(),
                config.shattered_armor_multiplier,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        DEMORALIZE.effect.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                DEMORALIZE.modifierId(),
                config.shout_damage_multiplier,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        CHARGE.effect
            .addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                CHARGE.modifierId(),
                config.charge_speed_multiplier,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE)
            .addAttributeModifier(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,
                CHARGE.modifierId(),
                config.charge_knockback_resistance_bonus,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);

        CombatEvents.ENTITY_ATTACK.register((args) -> {
            var attacker = args.attacker();
            if (attacker.hasStatusEffect(STEALTH.registryEntry)) {
                attacker.removeStatusEffect(STEALTH.registryEntry);
            }
        });
        var vanishId = Identifier.of(RoguesMod.NAMESPACE, "vanish");
        CombatEvents.SPELL_CAST.register((args) -> {
            var caster = args.caster();
            if (caster.hasStatusEffect(STEALTH.registryEntry) && !args.spell().id().equals(vanishId)) {
                caster.removeStatusEffect(STEALTH.registryEntry);
            }
        });
        CombatEvents.ITEM_USE.register((args) -> {
            var user = args.user();
            if (user.hasStatusEffect(STEALTH.registryEntry)) {
                user.removeStatusEffect(STEALTH.registryEntry);
            }
        });

        for (var entry: entries) {
            entry.register();
        }
    }
}
