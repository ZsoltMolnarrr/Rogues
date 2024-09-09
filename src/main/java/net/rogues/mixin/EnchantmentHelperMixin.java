package net.rogues.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.rogues.RoguesMod;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
//    @Inject(method = "getAttackDamage", at = @At("RETURN"), cancellable = true)
//    private static void getAttackDamage_rebalance_RETURN(ItemStack weapon, EntityGroup group, CallbackInfoReturnable<Float> cir) {
//        var attackModifiers = weapon.getAttributeModifiers(EquipmentSlot.MAINHAND).get(EntityAttributes.GENERIC_ATTACK_DAMAGE);
//        if (attackModifiers != null && !attackModifiers.isEmpty()) {
//            double attackDamage = 0;
//            for (var modifier : attackModifiers) {
//                if (modifier.getOperation() == EntityAttributeModifier.Operation.ADD_VALUE) {
//                    attackDamage += modifier.getValue();
//                }
//            }
//            if (attackDamage > 0) {
//                NbtList nbtList = weapon.getEnchantments();
//                MutableFloat mutableFloat = new MutableFloat();
//                float weaponDamage = (float)attackDamage;
//                for(int i = 0; i < nbtList.size(); ++i) {
//                    NbtCompound nbtCompound = nbtList.getCompound(i);
//                    Registries.ENCHANTMENT.getOrEmpty(EnchantmentHelper.getIdFromNbt(nbtCompound)).ifPresent((enchantment) -> {
//                        var level = EnchantmentHelper.getLevelFromNbt(nbtCompound);
//
//                        float vanillaDamage = 0;
//                        float rebalancedDamage = 0;
//
//                        // Sharpness
//                        if (enchantment == Enchantments.SHARPNESS
//                                && RoguesMod.tweaksConfig.value.enable_rebalance_enchantment_sharpness) {
//                            vanillaDamage = enchantment.getAttackDamage(level, group);
//                            if (vanillaDamage > 0) {
//                                rebalancedDamage = weaponDamage * level * RoguesMod.tweaksConfig.value.enchantment_sharpness_multiplier_per_level;
//                            }
//                        }
//                        // Smite
//                        if (enchantment == Enchantments.SMITE
//                                && RoguesMod.tweaksConfig.value.enable_rebalance_enchantment_smite) {
//                            vanillaDamage = enchantment.getAttackDamage(level, group);
//                            if (vanillaDamage > 0) {
//                                rebalancedDamage = weaponDamage * level * RoguesMod.tweaksConfig.value.enchantment_smite_multiplier_per_level;
//                            }
//                        }
//                        // Bane of Arthropods
//                        if (enchantment == Enchantments.BANE_OF_ARTHROPODS
//                                && RoguesMod.tweaksConfig.value.enable_rebalance_enchantment_arthropods) {
//                            vanillaDamage = enchantment.getAttackDamage(level, group);
//                            if (vanillaDamage > 0) {
//                                rebalancedDamage = weaponDamage * level * RoguesMod.tweaksConfig.value.enchantment_arthropods_multiplier_per_level;
//                            }
//                        }
//
//                        mutableFloat.add(rebalancedDamage - vanillaDamage);
//                    });
//                }
//                cir.setReturnValue(cir.getReturnValue() + mutableFloat.floatValue());
//            }
//        }
//    }
}
