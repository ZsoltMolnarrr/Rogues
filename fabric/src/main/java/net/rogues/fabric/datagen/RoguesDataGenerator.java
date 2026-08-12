package net.rogues.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.rogues.RoguesMod;
import net.rogues.effect.RogueEffects;
import net.rogues.entity.RogueEntities;
import net.rogues.item.RogueWeapons;
import net.rogues.item.armor.RogueArmors;
import net.rogues.util.RogueSounds;
import net.rogues.util.RogueSpells;
import net.spell_engine.api.datagen.NamespacedLangGenerator;
import net.spell_engine.api.datagen.SimpleSoundGeneratorV2;
import net.spell_engine.api.datagen.SpellGenerator;
import net.spell_engine.api.datagen.WeaponAttributeGenerator;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.api.tags.SpellTags;
import net.spell_engine.rpg_series.datagen.RPGSeriesDataGen;
import net.spell_engine.rpg_series.item.Armor;
import net.spell_engine.rpg_series.tags.RPGSeriesItemTags;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RoguesDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(SoundGen::new);
        pack.addProvider(SpellGen::new);
        pack.addProvider(SpellTagGenerator::new);
        pack.addProvider(ItemTagGenerator::new);
        pack.addProvider(RogueRecipes::new);
        pack.addProvider(UnsmeltGenerator::new);
        pack.addProvider(WeaponGen::new);
        pack.addProvider(RoguesAdvancements::new);
        pack.addProvider(LangGen::new);
    }

    public static class SpellTagGenerator extends FabricTagProvider<Spell> {
        public SpellTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, SpellRegistry.KEY, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            var namespace = RoguesMod.NAMESPACE;
            var treasureTagBuilder = getOrCreateTagBuilder(SpellTags.TREASURE);
            var processedBooks = new HashSet<RogueSpells.Book>();
            RogueSpells.entries.forEach(entry -> {
                if (entry.book() != null) {
                    var bookTagKey = SpellTags.spellBook(namespace, entry.book().toString().toLowerCase());
                    getOrCreateTagBuilder(bookTagKey).addOptional(entry.id());
                    var scrollTagKey = SpellTags.spellScroll(namespace, entry.book().toString().toLowerCase());
                    getOrCreateTagBuilder(scrollTagKey).addOptional(entry.id());
                    if (processedBooks.add(entry.book())) {
                        treasureTagBuilder.addOptionalTag(scrollTagKey);
                    }
                }
            });
        }
    }

    public static class ItemTagGenerator extends RPGSeriesDataGen.ItemTagGenerator {
        public ItemTagGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
            super(dataOutput, registryLookup);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            generateWeaponTags(RogueWeapons.entries);
            generateArmorTags(RogueArmors.entries, RPGSeriesItemTags.ArmorMetaType.MELEE);
        }
    }

    public static class SpellGen extends SpellGenerator {
        public SpellGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
            super(dataOutput, registryLookup);
        }

        @Override
        public void generateSpells(Builder builder) {
            for (var entry: RogueSpells.entries) {
                builder.add(entry.id(), entry.spell());
            }
        }
    }

    public static class SoundGen extends SimpleSoundGeneratorV2 {
        public SoundGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
            super(dataOutput, registryLookup);
        }

        @Override
        public void generateSounds(Builder builder) {
            builder.entries.add(new Entry(RoguesMod.NAMESPACE,
                    RogueSounds.entries.stream()
                            .map(entry -> SoundEntry.withVariants(entry.id().getPath(), entry.variants()))
                            .toList()
                    )
            );
        }
    }

    public static class UnsmeltGenerator extends FabricRecipeProvider {
        public UnsmeltGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        public static int UNSMELT_TIME = 300;

        @Override
        public void generate(RecipeExporter exporter) {
            disassembleArmor(exporter, RogueArmors.RogueArmorSet_t1, Items.LEATHER);
            disassembleArmor(exporter, RogueArmors.RogueArmorSet_t2, Items.RABBIT_HIDE);
            disassembleArmor(exporter, RogueArmors.RogueArmorSet_t3, Items.NETHERITE_SCRAP);
            disassembleArmor(exporter, RogueArmors.WarriorArmorSet_t1, Items.IRON_NUGGET);
            disassembleArmor(exporter, RogueArmors.WarriorArmorSet_t2, Items.CHAIN);
            disassembleArmor(exporter, RogueArmors.WarriorArmorSet_t3, Items.NETHERITE_SCRAP);

            disassemble(exporter,
                    RogueWeapons.entries.stream()
                            .filter(entry -> entry.id().getPath().contains("flint"))
                            .map(entry -> (ItemConvertible) entry.item()).toList(),
                    Items.FLINT);
            disassemble(exporter,
                    RogueWeapons.entries.stream()
                            .filter(entry -> entry.id().getPath().contains("gold"))
                            .map(entry -> (ItemConvertible) entry.item()).toList(),
                    Items.GOLD_NUGGET);
            disassemble(exporter,
                    RogueWeapons.entries.stream()
                            .filter(entry -> entry.id().getPath().contains("iron"))
                            .map(entry -> (ItemConvertible) entry.item()).toList(),
                    Items.IRON_NUGGET);
//            disassemble(exporter,
//                    Weapons.entries.stream()
//                            .filter(entry -> entry.id().getPath().contains("diamond"))
//                            .map(entry -> (ItemConvertible) entry.item()).toList(),
//                    Items.DIAM);
            disassemble(exporter,
                    RogueWeapons.entries.stream()
                            .filter(entry -> entry.id().getPath().contains("netherite"))
                            .map(entry -> (ItemConvertible) entry.item()).toList(),
                    Items.NETHERITE_SCRAP);
        }

        private static void disassembleArmor(RecipeExporter exporter, Armor.Set armorSet, Item output) {
            FabricRecipeProvider.offerSmelting(exporter,
                    armorSet.pieces(),
                    RecipeCategory.MISC,
                    output,
                    0.1f,
                    UNSMELT_TIME,
                    "disassemble"
            );
            FabricRecipeProvider.offerBlasting(exporter,
                    armorSet.pieces(),
                    RecipeCategory.MISC,
                    output,
                    0.1f,
                    UNSMELT_TIME / 2,
                    "disassemble"
            );
        }

        private static void disassemble(RecipeExporter exporter, List<ItemConvertible> items, Item output) {
            FabricRecipeProvider.offerSmelting(exporter,
                    items,
                    RecipeCategory.MISC,
                    output,
                    0.1f,
                    UNSMELT_TIME,
                    "disassemble"
            );
            FabricRecipeProvider.offerBlasting(exporter,
                    items,
                    RecipeCategory.MISC,
                    output,
                    0.1f,
                    UNSMELT_TIME / 2,
                    "disassemble"
            );
        }
    }

    public static class WeaponGen extends WeaponAttributeGenerator {
        public WeaponGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
            super(dataOutput, registryLookup);
        }

        @Override
        public void generateWeaponAttributes(Builder builder) {
            RogueWeapons.entries.forEach(entry -> {
                if (entry.weaponAttributesPreset != null && !entry.weaponAttributesPreset.isEmpty()) {
                    builder.entries.add(new Entry(entry.id(), entry.weaponAttributesPreset));
                }
            });
        }
    }

    /**
     * Generates the {@code en_us.json} language file from the in-code content definitions
     * (spells, status effects, weapons, armor, spell books) plus the advancement tree and a few ad-hoc
     * strings (creative tab, villager, workbench) that have no dedicated content entry.
     */
    public static class LangGen extends NamespacedLangGenerator {
        public LangGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
            super(dataOutput, registryLookup, RoguesMod.NAMESPACE);
        }

        @Override
        public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, FabricLanguageProvider.TranslationBuilder builder) {
            var namespace = RoguesMod.NAMESPACE;

            // Creative tab
            builder.add("itemGroup." + namespace + ".general", "Rogues & Warriors");

            // Spell books & scrolls (one generated item per book)
            for (var book : RogueSpells.Book.values()) {
                var key = book.name().toLowerCase();
                builder.add("item." + namespace + ".spell_book/" + key, book.bookName);
                builder.add("item." + namespace + ".spell_scroll/" + key, book.scrollName);
                builder.add("item." + namespace + ".spell_book/" + key + ".spell_binding.description", book.bindingDescription);
            }

            // Spells (only those given a display name in code)
            for (var entry : RogueSpells.entries) {
                if (entry.title() == null || entry.title().isEmpty()) {
                    continue;
                }
                var path = entry.id().getPath();
                builder.add("spell." + namespace + "." + path + ".name", entry.title());
                builder.add("spell." + namespace + "." + path + ".description", entry.description());
            }

            // Status effects
            for (var entry : RogueEffects.entries) {
                var path = entry.id.getPath();
                builder.add("effect." + namespace + "." + path, entry.title);
                builder.add("effect." + namespace + "." + path + ".description", entry.description);
            }

            // Weapons — code-sourced display names
            RogueWeapons.entries.forEach(entry -> addItemName(builder, entry.id(), entry.translatedName()));
            // Conditional weapons are only registered when their host mod is present, so they are absent
            // from the weapon list at data-gen time. Their names are provided directly.
            builder.add("item." + namespace + ".ruby_dagger", "Ruby Dagger");
            builder.add("item." + namespace + ".aeternium_dagger", "Aeternium Dagger");
            builder.add("item." + namespace + ".aether_dagger", "Valkyrie Shiv");
            builder.add("item." + namespace + ".ruby_sickle", "Ruby Sickle");
            builder.add("item." + namespace + ".aeternium_sickle", "Aeternium Sickle");
            builder.add("item." + namespace + ".aether_sickle", "Heavenly Harvester");
            builder.add("item." + namespace + ".ruby_double_axe", "Ruby Double Axe");
            builder.add("item." + namespace + ".aeternium_double_axe", "Aeternium Double Axe");
            builder.add("item." + namespace + ".aether_double_axe", "Holy Double Axe");
            builder.add("item." + namespace + ".ruby_glaive", "Ruby Glaive");
            builder.add("item." + namespace + ".aeternium_glaive", "Aeternium Glaive");
            builder.add("item." + namespace + ".aether_glaive", "Gilded Battle Glaive");

            // Armor sets (per piece)
            for (var entry : RogueArmors.entries) {
                var set = entry.armorSet();
                addItemName(builder, set.idOf(set.head), set.headTranslation);
                addItemName(builder, set.idOf(set.chest), set.chestTranslation);
                addItemName(builder, set.idOf(set.legs), set.legsTranslation);
                addItemName(builder, set.idOf(set.feet), set.feetTranslation);
            }

            // Custom entities — code-sourced display names (paired with the type in RogueEntities.Entry)
            for (var entry : RogueEntities.entries) {
                builder.add("entity." + namespace + "." + entry.id.getPath(), entry.name);
            }

            // Arms Dealer villager (several key formats are referenced across versions) + workbench
            builder.add("entity.minecraft.villager.arms_merchant", "Arms Dealer");
            builder.add("entity.minecraft.villager." + namespace + ".arms_merchant", "Arms Dealer");
            builder.add("entity.minecraft.villager." + namespace + ":arms_merchant", "Arms Dealer");
            builder.add("block." + namespace + ".arms_workbench", "Arms Station");
            builder.add("block." + namespace + ".arms_workbench.hint", "Workbench for Arms Merchant Villagers.");

            // Advancements (generated alongside the rpg_series advancement JSONs)
            for (var advancement : RoguesAdvancements.entries()) {
                builder.add(advancement.titleKey(), advancement.title());
                builder.add(advancement.descriptionKey(), advancement.description());
            }
        }

        private static void addItemName(FabricLanguageProvider.TranslationBuilder builder, Identifier id, String name) {
            if (name == null || name.isEmpty()) {
                return;
            }
            builder.add("item." + id.getNamespace() + "." + id.getPath(), name);
        }
    }
}
