package net.rogues;

import net.rpg_foundation.structure_pool.api.StructurePoolConfig;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
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
            MobEffects.STRENGTH.value().addAttributeModifier(
                    Attributes.ATTACK_DAMAGE,
                    Identifier.withDefaultNamespace("strength"),
                    tweaksConfig.value.rebalance_strength_attack_damage_multiplier,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE
            );
        }
    }

    public static void registerSounds() {
        RogueSounds.register();
    }

    public static void registerItems() {
        Group.ROGUES = new CreativeModeTab.Builder(CreativeModeTab.Row.TOP, 0)
                .icon(() -> new ItemStack(RogueArmors.RogueArmorSet_t2.head))
                .title(Component.translatable("itemGroup." + NAMESPACE + ".general"))
                .build();
        CustomBlocks.register();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Group.KEY, Group.ROGUES);
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

