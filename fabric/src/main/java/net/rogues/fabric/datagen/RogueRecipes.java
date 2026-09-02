package net.rogues.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.rogues.block.CustomBlocks;
import net.rogues.item.RogueWeapons;
import net.rogues.item.armor.RogueArmors;
import net.spell_engine.rpg_series.item.Armor;
import net.spell_engine.rpg_series.item.Weapon;

import java.util.concurrent.CompletableFuture;

/**
 * Generates all crafting recipes for the Rogues mod using Fabric's built-in API.
 * Conditional recipes (ruby, aeternium, aether) are kept as hand-written JSONs.
 */
public class RogueRecipes extends FabricRecipeProvider {

    public RogueRecipes(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    /// 1.21.2+: recipe providers hand back a {@link RecipeGenerator}, which owns the builder helpers
    /// (`createShaped`, `conditionsFromItem`, `offerNetheriteUpgradeRecipe`) that used to be statics.
    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput exporter) {
        return new Generator(registries, exporter);
    }

    private static class Generator extends RecipeProvider {
        Generator(HolderLookup.Provider registries, RecipeOutput exporter) {
            super(registries, exporter);
        }

        @Override
        public void buildRecipes() {
            generateDaggerRecipes();
            generateSickleRecipes();
            generateDoubleAxeRecipes();
            generateGlaiveRecipes();
            generateArmorRecipes();
            generateOtherRecipes();
            generateNetheriteUpgrades();
        }

    // ========================================
    // DAGGER RECIPES
    // ========================================

    private void generateDaggerRecipes() {
        dagger(RogueWeapons.flint_dagger, Items.FLINT);
        dagger(RogueWeapons.iron_dagger, Items.IRON_INGOT);
        dagger(RogueWeapons.golden_dagger, Items.GOLD_INGOT);
        dagger(RogueWeapons.diamond_dagger, Items.DIAMOND);
    }

    /**
     * Generate dagger recipe with standard pattern: " M" / "S "
     */
    private void dagger(Weapon.Entry daggerEntry, Item material) {
        shaped(RecipeCategory.COMBAT, daggerEntry.item())
                .pattern(" M")
                .pattern("S ")
                .define('M', material)
                .define('S', Items.STICK)
                .unlockedBy(getHasName(material), has(material))
                .save(this.output);
    }

    // ========================================
    // SICKLE RECIPES
    // ========================================

    private void generateSickleRecipes() {
        sickle(RogueWeapons.iron_sickle, Items.IRON_INGOT);
        sickle(RogueWeapons.golden_sickle, Items.GOLD_INGOT);
        sickle(RogueWeapons.diamond_sickle, Items.DIAMOND);
    }

    /**
     * Generate sickle recipe with standard pattern: "MM" / "S "
     */
    private void sickle(Weapon.Entry sickleEntry, Item material) {
        shaped(RecipeCategory.COMBAT, sickleEntry.item())
                .pattern("MM")
                .pattern("S ")
                .define('M', material)
                .define('S', Items.STICK)
                .unlockedBy(getHasName(material), has(material))
                .save(this.output);
    }

    // ========================================
    // DOUBLE AXE RECIPES
    // ========================================

    private void generateDoubleAxeRecipes() {
        // Stone double axe uses stone tool materials tag
        shaped(RecipeCategory.COMBAT, RogueWeapons.stone_double_axe.item())
                .pattern("MSM")
                .pattern("MSM")
                .pattern(" S ")
                .define('M', ItemTags.STONE_TOOL_MATERIALS)
                .define('S', Items.STICK)
                .unlockedBy("has_cobblestone", has(Items.COBBLESTONE))
                .save(this.output);

        doubleAxe(RogueWeapons.iron_double_axe, Items.IRON_INGOT);
        doubleAxe(RogueWeapons.golden_double_axe, Items.GOLD_INGOT);
        doubleAxe(RogueWeapons.diamond_double_axe, Items.DIAMOND);
    }

    /**
     * Generate double axe recipe with standard pattern: "MSM" / "MSM" / " S "
     */
    private void doubleAxe(Weapon.Entry axeEntry, Item material) {
        shaped(RecipeCategory.COMBAT, axeEntry.item())
                .pattern("MSM")
                .pattern("MSM")
                .pattern(" S ")
                .define('M', material)
                .define('S', Items.STICK)
                .unlockedBy(getHasName(material), has(material))
                .save(this.output);
    }

    // ========================================
    // GLAIVE RECIPES
    // ========================================

    private void generateGlaiveRecipes() {
        glaive(RogueWeapons.iron_glaive, Items.IRON_INGOT);
        glaive(RogueWeapons.golden_glaive, Items.GOLD_INGOT);
        glaive(RogueWeapons.diamond_glaive, Items.DIAMOND);
    }

    /**
     * Generate glaive recipe with standard pattern: " MM" / "MS " / "S  "
     */
    private void glaive(Weapon.Entry glaiveEntry, Item material) {
        shaped(RecipeCategory.COMBAT, glaiveEntry.item())
                .pattern(" MM")
                .pattern("MS ")
                .pattern("S  ")
                .define('M', material)
                .define('S', Items.STICK)
                .unlockedBy(getHasName(material), has(material))
                .save(this.output);
    }

    // ========================================
    // ARMOR RECIPES
    // ========================================

    private void generateArmorRecipes() {
        // Rogue Armor (T1) - leather + wool + red dye
        generateRogueArmorSet(RogueArmors.RogueArmorSet_t1, Items.LEATHER, Items.DYE.red());

        // Assassin Armor (T2) - rabbit hide + ink sac + gold
        generateAssassinArmorSet(RogueArmors.RogueArmorSet_t2, Items.RABBIT_HIDE, Items.INK_SAC, Items.GOLD_INGOT);

        // Warrior Armor (T1) - iron + leather + string
        generateWarriorArmorSet(RogueArmors.WarriorArmorSet_t1, Items.IRON_INGOT, Items.LEATHER, Items.STRING);

        // Berserker Armor (T2) - chain + netherite_scrap + goat_horn + leather
        generateBerserkerArmorSet(RogueArmors.WarriorArmorSet_t2, Items.IRON_CHAIN, Items.NETHERITE_SCRAP, Items.GOAT_HORN, Items.LEATHER);
    }

    /**
     * Generate Rogue armor set (T1) - leather + wool + red dye
     */
    private void generateRogueArmorSet(Armor.Set armorSet, Item leather, Item redDye) {
        // Helmet - pattern: "WDW" / " W "
        shaped(RecipeCategory.COMBAT, armorSet.head)
                .pattern("WDW")
                .pattern(" W ")
                .define('D', redDye)
                .define('W', ItemTags.WOOL)
                .unlockedBy(getHasName(leather), has(leather))
                .save(this.output);

        // Chestplate - pattern: "W W" / "LWL" / "LLL"
        shaped(RecipeCategory.COMBAT, armorSet.chest)
                .pattern("W W")
                .pattern("LWL")
                .pattern("LLL")
                .define('L', leather)
                .define('W', ItemTags.WOOL)
                .unlockedBy(getHasName(leather), has(leather))
                .save(this.output);

        // Leggings - pattern: "WWW" / "L L" / "W W"
        shaped(RecipeCategory.COMBAT, armorSet.legs)
                .pattern("WWW")
                .pattern("L L")
                .pattern("W W")
                .define('L', leather)
                .define('W', ItemTags.WOOL)
                .unlockedBy(getHasName(leather), has(leather))
                .save(this.output);

        // Boots - pattern: "W W" / "L L"
        shaped(RecipeCategory.COMBAT, armorSet.feet)
                .pattern("W W")
                .pattern("L L")
                .define('L', leather)
                .define('W', ItemTags.WOOL)
                .unlockedBy(getHasName(leather), has(leather))
                .save(this.output);
    }

    /**
     * Generate Assassin armor set (T2) - rabbit hide + ink sac + gold
     */
    private void generateAssassinArmorSet(Armor.Set armorSet, Item rabbitHide, Item inkSac, Item gold) {
        // Helmet - pattern: "SGS" / "R R"
        shaped(RecipeCategory.COMBAT, armorSet.head)
                .pattern("SGS")
                .pattern("R R")
                .define('S', inkSac)
                .define('G', gold)
                .define('R', rabbitHide)
                .unlockedBy(getHasName(rabbitHide), has(rabbitHide))
                .save(this.output);

        // Chestplate - pattern: "S S" / "RGR" / "RRR"
        shaped(RecipeCategory.COMBAT, armorSet.chest)
                .pattern("S S")
                .pattern("RGR")
                .pattern("RRR")
                .define('S', inkSac)
                .define('G', gold)
                .define('R', rabbitHide)
                .unlockedBy(getHasName(rabbitHide), has(rabbitHide))
                .save(this.output);

        // Leggings - pattern: "SGS" / "R R" / "R R"
        shaped(RecipeCategory.COMBAT, armorSet.legs)
                .pattern("SGS")
                .pattern("R R")
                .pattern("R R")
                .define('S', inkSac)
                .define('G', gold)
                .define('R', rabbitHide)
                .unlockedBy(getHasName(rabbitHide), has(rabbitHide))
                .save(this.output);

        // Boots - pattern: "GSG" / "R R"
        shaped(RecipeCategory.COMBAT, armorSet.feet)
                .pattern("GSG")
                .pattern("R R")
                .define('S', inkSac)
                .define('G', gold)
                .define('R', rabbitHide)
                .unlockedBy(getHasName(rabbitHide), has(rabbitHide))
                .save(this.output);
    }

    /**
     * Generate Warrior armor set (T1) - iron + leather + string
     */
    private void generateWarriorArmorSet(Armor.Set armorSet, Item iron, Item leather, Item string) {
        // Helmet - pattern: "ILI" / "I I"
        shaped(RecipeCategory.COMBAT, armorSet.head)
                .pattern("ILI")
                .pattern("I I")
                .define('I', iron)
                .define('L', leather)
                .unlockedBy(getHasName(iron), has(iron))
                .save(this.output);

        // Chestplate - pattern: "C C" / "III" / "LLL"
        shaped(RecipeCategory.COMBAT, armorSet.chest)
                .pattern("C C")
                .pattern("III")
                .pattern("LLL")
                .define('I', iron)
                .define('L', leather)
                .define('C', string)
                .unlockedBy(getHasName(iron), has(iron))
                .save(this.output);

        // Leggings - pattern: "III" / "L L" / "I I"
        shaped(RecipeCategory.COMBAT, armorSet.legs)
                .pattern("III")
                .pattern("L L")
                .pattern("I I")
                .define('I', iron)
                .define('L', leather)
                .unlockedBy(getHasName(iron), has(iron))
                .save(this.output);

        // Boots - pattern: "I I" / "L L"
        shaped(RecipeCategory.COMBAT, armorSet.feet)
                .pattern("I I")
                .pattern("L L")
                .define('I', iron)
                .define('L', leather)
                .unlockedBy(getHasName(iron), has(iron))
                .save(this.output);
    }

    /**
     * Generate Berserker armor set (T2) - chain + netherite_scrap + goat_horn + leather
     */
    private void generateBerserkerArmorSet(Armor.Set armorSet, Item chain, Item netheriteScrap, Item goatHorn, Item leather) {
        // Helmet - pattern: "GTG" / "I I"
        shaped(RecipeCategory.COMBAT, armorSet.head)
                .pattern("GTG")
                .pattern("I I")
                .define('I', chain)
                .define('G', goatHorn)
                .define('T', netheriteScrap)
                .unlockedBy(getHasName(netheriteScrap), has(netheriteScrap))
                .save(this.output);

        // Chestplate - pattern: "T T" / "III" / "LLL"
        shaped(RecipeCategory.COMBAT, armorSet.chest)
                .pattern("T T")
                .pattern("III")
                .pattern("LLL")
                .define('I', chain)
                .define('T', netheriteScrap)
                .define('L', leather)
                .unlockedBy(getHasName(netheriteScrap), has(netheriteScrap))
                .save(this.output);

        // Leggings - pattern: "III" / "T T" / "I I"
        shaped(RecipeCategory.COMBAT, armorSet.legs)
                .pattern("III")
                .pattern("T T")
                .pattern("I I")
                .define('I', chain)
                .define('T', netheriteScrap)
                .unlockedBy(getHasName(netheriteScrap), has(netheriteScrap))
                .save(this.output);

        // Boots - pattern: "T T" / "I I"
        shaped(RecipeCategory.COMBAT, armorSet.feet)
                .pattern("T T")
                .pattern("I I")
                .define('I', chain)
                .define('T', netheriteScrap)
                .unlockedBy(getHasName(netheriteScrap), has(netheriteScrap))
                .save(this.output);
    }

    // ========================================
    // OTHER RECIPES
    // ========================================

    private void generateOtherRecipes() {
        // Arms Workbench - pattern: "PIW" / "###"
        shaped(RecipeCategory.MISC, CustomBlocks.WORKBENCH.block())
                .pattern("PIW")
                .pattern("###")
                .define('P', Items.PAPER)
                .define('I', Items.IRON_INGOT)
                .define('W', ItemTags.WOOL)
                .define('#', ItemTags.PLANKS)
                .unlockedBy(getHasName(Items.PAPER), has(Items.PAPER))
                .showNotification(false)
                .save(this.output);
    }

    // ========================================
    // NETHERITE UPGRADE RECIPES
    // ========================================

    private void generateNetheriteUpgrades() {
        // Weapon upgrades - diamond to netherite
        netheriteSmithing(RogueWeapons.diamond_dagger.item(), RecipeCategory.COMBAT, RogueWeapons.netherite_dagger.item());
        netheriteSmithing(RogueWeapons.diamond_sickle.item(), RecipeCategory.COMBAT, RogueWeapons.netherite_sickle.item());
        netheriteSmithing(RogueWeapons.diamond_double_axe.item(), RecipeCategory.COMBAT, RogueWeapons.netherite_double_axe.item());
        netheriteSmithing(RogueWeapons.diamond_glaive.item(), RecipeCategory.COMBAT, RogueWeapons.netherite_glaive.item());

        // Assassin armor upgrades (T2 -> T3)
        netheriteSmithing(RogueArmors.RogueArmorSet_t2.head, RecipeCategory.COMBAT, RogueArmors.RogueArmorSet_t3.head);
        netheriteSmithing(RogueArmors.RogueArmorSet_t2.chest, RecipeCategory.COMBAT, RogueArmors.RogueArmorSet_t3.chest);
        netheriteSmithing(RogueArmors.RogueArmorSet_t2.legs, RecipeCategory.COMBAT, RogueArmors.RogueArmorSet_t3.legs);
        netheriteSmithing(RogueArmors.RogueArmorSet_t2.feet, RecipeCategory.COMBAT, RogueArmors.RogueArmorSet_t3.feet);

        // Berserker armor upgrades (T2 -> T3)
        netheriteSmithing(RogueArmors.WarriorArmorSet_t2.head, RecipeCategory.COMBAT, RogueArmors.WarriorArmorSet_t3.head);
        netheriteSmithing(RogueArmors.WarriorArmorSet_t2.chest, RecipeCategory.COMBAT, RogueArmors.WarriorArmorSet_t3.chest);
        netheriteSmithing(RogueArmors.WarriorArmorSet_t2.legs, RecipeCategory.COMBAT, RogueArmors.WarriorArmorSet_t3.legs);
        netheriteSmithing(RogueArmors.WarriorArmorSet_t2.feet, RecipeCategory.COMBAT, RogueArmors.WarriorArmorSet_t3.feet);
    }

    }

    @Override
    public String getName() {
        return "Rogue Crafting Recipes";
    }
}
