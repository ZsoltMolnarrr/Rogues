package net.rogues;

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
import net.fabric_extras.structure_pool.api.StructurePoolAPI;
import net.fabric_extras.structure_pool.api.StructurePoolConfig;
import net.rogues.village.RogueVillagers;
import net.spell_engine.Platform;
import net.spell_engine.PlatformEvents;
import net.spell_power.api.ModifierDefinitions;
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
        if (!Platform.util().isModLoaded("lithostitched")) {
            // Only inject the barracks if Lithostitched is not present - otherwise the data-driven
            // paths in `resources/data/rogues` already do it.
            //
            // `injectAll` only *queues* the entries; StructurePoolAPI's own entrypoint applies them
            // when the server starts (Fabric SERVER_STARTING / Forge ServerAboutToStartEvent, both
            // before the spawn region generates). The queue is deliberately never cleared, so this
            // must be called exactly once, here at mod init - never per world load.
            StructurePoolAPI.injectAll(villagesConfig.value);
        }

        if (tweaksConfig.value.rebalance_strength_attack_damage_multiplier > 0) {
            // 1.20.1: `StatusEffects.STRENGTH` is a raw `StatusEffect` and modifiers are UUID-keyed.
            // The UUID is derived from the id the 1.21 line used, via Spell Power's stable mapping.
            var modifierId = new Identifier("minecraft", "strength");
            StatusEffects.STRENGTH.addAttributeModifier(
                    EntityAttributes.GENERIC_ATTACK_DAMAGE,
                    ModifierDefinitions.uuid(modifierId).toString(),
                    tweaksConfig.value.rebalance_strength_attack_damage_multiplier,
                    EntityAttributeModifier.Operation.MULTIPLY_BASE
            );
        }
    }

    public static void registerSounds() {
        RogueSounds.register();
    }

    public static void registerBlocks() {
        CustomBlocks.registerBlocks();
    }

    public static void registerItems() {
        Group.ROGUES = new ItemGroup.Builder(ItemGroup.Row.TOP, 0)
                .icon(() -> new ItemStack(RogueArmors.RogueArmorSet_t2.head))
                .displayName(Text.translatable("itemGroup." + NAMESPACE + ".general"))
                .build();
        CustomBlocks.registerItems();
        Registry.register(Registries.ITEM_GROUP, Group.KEY, Group.ROGUES);

        // Custom blocks into the Rogues creative tab. Dispatched by SpellEngine on both loaders
        // (Fabric `ItemGroupEvents` / Forge `BuildCreativeModeTabContentsEvent`).
        //
        // ORDER MATTERS: on both loaders the group modifiers run in *registration* order, so this listener
        // is installed BEFORE the weapon/armor registrations install SpellEngine's own listeners — that is
        // what puts the blocks at the front of the tab. It has to live here rather than in the loader
        // entrypoints: on Forge the tab event is posted per mod container in mod-load order, so anything a
        // Rogues-owned listener adds would always land *after* SpellEngine's contributions.
        PlatformEvents.onItemGroupModify(Group.KEY, (content, context) -> {
            for (var entry : CustomBlocks.all) {
                content.add(entry.item());
            }
        });

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

