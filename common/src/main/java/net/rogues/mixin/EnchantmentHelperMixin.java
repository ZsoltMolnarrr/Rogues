package net.rogues.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.rogues.RoguesMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/// Percentage-based damage enchantments.
///
/// On 1.21.1 this is a data-driven enchantment override
/// (`data/minecraft/enchantment/sharpness.json`): the vanilla `minecraft:damage` effect is replaced by a
/// `minecraft:attributes` effect that puts an `add_multiplied_base` modifier of `0.08 * level` on
/// `generic.attack_damage`. 1.20.1 has no enchantment registry to override, so the same rebalance is done
/// in code here — this is the legacy `1.20.1` branch's `EnchantmentHelperMixin`, restored and retuned to
/// the 1.21.1 numbers.
///
/// **Scaling base.** 1.21.1's `ADD_MULTIPLIED_BASE` multiplies *(attribute base value + every
/// `ADD_VALUE` modifier)* — see `EntityAttributeInstance#computeValue`. For a player swinging a weapon
/// that is the player's own base attack damage (1.0, from `PlayerEntity#createPlayerAttributes`) plus the
/// weapon's attack-damage modifier, so both terms are reproduced below. `EnchantmentHelper#getAttackDamage`
/// has no attacker parameter, so the attacker's base is taken as the player's 1.0; a mob attacking with an
/// enchanted weapon (`MobEntity#tryAttack`) is off by its own base attack damage, which is a rounding
/// error next to the weapon term and not worth an extra injection point.
///
/// **Stack- vs item-level modifiers.** `ItemStack#getAttributeModifiers(EquipmentSlot)` is the right
/// accessor: it already folds in NBT `AttributeModifiers` and everything SpellEngine's
/// `ItemStackAttributeModifiersMixin` serves stack-side, which the item-level
/// `Item#getAttributeModifiers` would miss.
///
/// Vanilla and Forge 47 agree on this method's signature. Forge routes the per-enchantment call through
/// its own `IForgeEnchantment#getDamageBonus(int, EntityGroup, ItemStack)`, whose default implementation
/// is `getAttackDamage(level, group)` — the same value subtracted below — so the delta is correct on both
/// loaders.
@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    /// `PlayerEntity#createPlayerAttributes` sets `generic.attack_damage` to 1.0. It is the base value
    /// 1.21.1's `add_multiplied_base` scales together with the weapon's own modifier.
    @Unique private static final double ROGUES_ATTACKER_BASE_ATTACK_DAMAGE = 1.0;

    @Inject(method = "getAttackDamage", at = @At("RETURN"), cancellable = true)
    private static void rogues_rebalanceDamageEnchantments(ItemStack weapon, EntityGroup group, CallbackInfoReturnable<Float> cir) {
        var config = RoguesMod.tweaksConfig.value;
        if (!config.enable_rebalance_enchantment_sharpness
                && !config.enable_rebalance_enchantment_smite
                && !config.enable_rebalance_enchantment_arthropods) {
            return;
        }
        if (weapon.isEmpty() || !weapon.hasEnchantments()) {
            return;
        }

        var scalingBase = rogues_scalingBase(weapon);
        if (scalingBase <= 0) {
            // Not a weapon that contributes attack damage — nothing sensible to take a percentage of.
            return;
        }

        float delta = 0;
        if (config.enable_rebalance_enchantment_sharpness) {
            delta += rogues_delta(Enchantments.SHARPNESS, config.enchantment_sharpness_multiplier_per_level, weapon, group, scalingBase);
        }
        if (config.enable_rebalance_enchantment_smite) {
            delta += rogues_delta(Enchantments.SMITE, config.enchantment_smite_multiplier_per_level, weapon, group, scalingBase);
        }
        if (config.enable_rebalance_enchantment_arthropods) {
            delta += rogues_delta(Enchantments.BANE_OF_ARTHROPODS, config.enchantment_arthropods_multiplier_per_level, weapon, group, scalingBase);
        }
        if (delta != 0) {
            cir.setReturnValue(cir.getReturnValue() + delta);
        }
    }

    /// Sum of the weapon's `ADDITION` attack-damage modifiers, plus the attacker's base attack damage.
    @Unique
    private static float rogues_scalingBase(ItemStack weapon) {
        var modifiers = weapon.getAttributeModifiers(EquipmentSlot.MAINHAND).get(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (modifiers == null || modifiers.isEmpty()) {
            return 0;
        }
        double weaponDamage = 0;
        for (var modifier : modifiers) {
            if (modifier.getOperation() == EntityAttributeModifier.Operation.ADDITION) {
                weaponDamage += modifier.getValue();
            }
        }
        return weaponDamage > 0 ? (float) (ROGUES_ATTACKER_BASE_ATTACK_DAMAGE + weaponDamage) : 0;
    }

    /// The correction to add on top of vanilla's already-computed bonus: percentage value minus the flat
    /// value vanilla contributed for this enchantment. Returns 0 when vanilla contributed nothing (Smite
    /// against a non-undead target, for instance), so a rebalanced enchantment never starts applying to
    /// targets vanilla exempted.
    @Unique
    private static float rogues_delta(Enchantment enchantment, float multiplierPerLevel, ItemStack weapon, EntityGroup group, float scalingBase) {
        if (multiplierPerLevel <= 0) {
            return 0;
        }
        int level = EnchantmentHelper.getLevel(enchantment, weapon);
        if (level <= 0) {
            return 0;
        }
        @SuppressWarnings("deprecation")
        float vanilla = enchantment.getAttackDamage(level, group);
        if (vanilla <= 0) {
            return 0;
        }
        return scalingBase * level * multiplierPerLevel - vanilla;
    }
}
