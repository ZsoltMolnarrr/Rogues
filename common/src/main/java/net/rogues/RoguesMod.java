package net.rogues;

import net.fabric_extras.structure_pool.api.StructurePoolConfig;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.rogues.block.CustomBlocks;
import net.rogues.config.Default;
import net.rogues.config.TweaksConfig;
import net.rogues.effect.RogueEffects;
import net.rogues.item.Books;
import net.rogues.item.Group;
import net.rogues.item.Weapons;
import net.rogues.item.armor.Armors;
import net.rogues.util.RogueSounds;
import net.rogues.village.RogueVillagers;
import net.spell_engine.api.config.ConfigFile;
import net.tiny_config.ConfigManager;

public class RoguesMod {

    public static final String NAMESPACE = "rogues";
    public static final String ID = NAMESPACE;

    public static ConfigManager<ConfigFile.Equipment> itemConfig = new ConfigManager<>
            ("equipment_v2", Default.itemConfig)
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
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            tweaksConfig.value.ignore_items_required_mods = true;
        }
        itemConfig.refresh();
        effectsConfig.refresh();
        villagesConfig.refresh();
        RogueSounds.register();
        Group.ROGUES = FabricItemGroup.builder()
                .icon(() -> new ItemStack(Armors.RogueArmorSet_t2.head))
                .displayName(Text.translatable("itemGroup." + NAMESPACE + ".general"))
                .build();
        CustomBlocks.register();
        Books.register();
        Registry.register(Registries.ITEM_GROUP, Group.KEY, Group.ROGUES);
        Weapons.register(itemConfig.value.weapons);
        Armors.register(itemConfig.value.armor_sets);
        itemConfig.save();

        RogueVillagers.register();
        RogueEffects.register(effectsConfig.value);
        effectsConfig.save();
    }
}

