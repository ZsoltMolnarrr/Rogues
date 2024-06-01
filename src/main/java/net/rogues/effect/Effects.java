package net.rogues.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.rogues.RoguesMod;
import net.spell_engine.api.effect.Synchronized;

public class Effects {
    public static final StatusEffect sliceAndDice = new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0x993333)
            .addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                    "112f3133-8a44-11ed-a1eb-0242ac120002",
                    0.1F,
                    EntityAttributeModifier.Operation.MULTIPLY_BASE);
    public static int sliceAndDiceMaxStacks = 10;

    public static void register() {
        Synchronized.configure(sliceAndDice, true);

        int rawId = 753;
        Registry.register(Registries.STATUS_EFFECT, rawId++, new Identifier(RoguesMod.NAMESPACE, "slice_and_dice").toString(), sliceAndDice);
    }
}
