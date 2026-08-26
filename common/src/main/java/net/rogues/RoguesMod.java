package net.rogues;

import net.rpg_foundation.structure_pool_api.api.StructurePoolConfig;
import net.minecraft.item.ItemGroup;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.rogues.block.CustomBlocks;
import net.rogues.config.Default;
import net.rogues.config.TweaksConfig;
import net.rogues.effect.RogueEffects;
import net.rogues.entity.RogueEntities;
import net.rogues.item.Group;
import net.rogues.item.RogueWeapons;
import net.rogues.item.armor.RogueArmors;
import net.rogues.util.RogueSounds;
import net.rogues.village.RogueVillagers;
import net.spell_engine.Platform;
import net.spell_engine.rpg_series.config.ConfigFile;
import net.tiny_config.ConfigManager;

public class RoguesMod {

    public static final String NAMESPACE = "rogues";
    public static final String ID = NAMESPACE;

    public static ConfigManager<ConfigFile.Equipment> itemConfig = new ConfigManager<>
            ("equipment_v3", Default.itemConfig)
            .builder()
            .setDirectory(NAMESPACE)
            .sanitize(true)
            .build();
    public static ConfigManager<ConfigFile.Effects> effectsConfig = new ConfigManager<>
            ("effects", new ConfigFile.Effects())
            .builder()
            .setDirectory(NAMESPACE)
            .sanitize(true)
            .build();

    public static ConfigManager<StructurePoolConfig> villagesConfig = new ConfigManager<>
            ("villages", Default.villages)
            .builder()
            .setDirectory(NAMESPACE)
            .sanitize(true)
            .build();
    public static ConfigManager<TweaksConfig> tweaksConfig = new ConfigManager<>
            ("tweaks", new TweaksConfig())
            .builder()
            .setDirectory(NAMESPACE)
            .sanitize(true)
            .build();

    public static void init() {
        tweaksConfig.refresh();
        if (Platform.util().isDevelopmentEnvironment()) {
            tweaksConfig.value.ignore_items_required_mods = true;
        }
        itemConfig.refresh();
        effectsConfig.refresh();
        villagesConfig.refresh();

        if (tweaksConfig.value.rebalance_strength_attack_damage_multiplier > 0) {
            StatusEffects.STRENGTH.value().addAttributeModifier(
                    EntityAttributes.ATTACK_DAMAGE,
                    Identifier.ofVanilla("strength"),
                    tweaksConfig.value.rebalance_strength_attack_damage_multiplier,
                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
            );
        }
    }

    public static void registerSounds() {
        RogueSounds.register();
    }

    public static void registerItems() {
        Group.ROGUES = new ItemGroup.Builder(ItemGroup.Row.TOP, 0)
                .icon(() -> new ItemStack(RogueArmors.RogueArmorSet_t2.head))
                .displayName(Text.translatable("itemGroup." + NAMESPACE + ".general"))
                .build();
        CustomBlocks.register();
        Registry.register(Registries.ITEM_GROUP, Group.KEY, Group.ROGUES);
        RogueWeapons.register(itemConfig.value.weapons);
        RogueArmors.register(itemConfig.value.armor_sets);
        itemConfig.save();
    }

    public static void registerEffects() {
        RogueEffects.register(effectsConfig.value);
        effectsConfig.save();
    }

    public static void registerEntities() {
        RogueEntities.register();
    }

    public static void registerVillagers() {
        RogueVillagers.registerVillagers();
    }
}

