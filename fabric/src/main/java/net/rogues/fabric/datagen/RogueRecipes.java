package net.rogues.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
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

    public RogueRecipes(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    /// 1.21.2+: recipe providers hand back a {@link RecipeGenerator}, which owns the builder helpers
    /// (`createShaped`, `conditionsFromItem`, `offerNetheriteUpgradeRecipe`) that used to be statics.
    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registries, RecipeExporter exporter) {
        return new Generator(registries, exporter);
    }

    private static class Generator extends RecipeGenerator {
        Generator(RegistryWrapper.WrapperLookup registries, RecipeExporter exporter) {
            super(registries, exporter);
        }

        @Override
        public void generate() {
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
        createShaped(RecipeCategory.COMBAT, daggerEntry.item())
                .pattern(" M")
                .pattern("S ")
                .input('M', material)
                .input('S', Items.STICK)
                .criterion(hasItem(material), conditionsFromItem(material))
                .offerTo(this.exporter);
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
        createShaped(RecipeCategory.COMBAT, sickleEntry.item())
                .pattern("MM")
                .pattern("S ")
                .input('M', material)
                .input('S', Items.STICK)
                .criterion(hasItem(material), conditionsFromItem(material))
                .offerTo(this.exporter);
    }

    // ========================================
    // DOUBLE AXE RECIPES
    // ========================================

    private void generateDoubleAxeRecipes() {
        // Stone double axe uses stone tool materials tag
        createShaped(RecipeCategory.COMBAT, RogueWeapons.stone_double_axe.item())
                .pattern("MSM")
                .pattern("MSM")
                .pattern(" S ")
                .input('M', ItemTags.STONE_TOOL_MATERIALS)
                .input('S', Items.STICK)
                .criterion("has_cobblestone", conditionsFromItem(Items.COBBLESTONE))
                .offerTo(this.exporter);

        doubleAxe(RogueWeapons.iron_double_axe, Items.IRON_INGOT);
        doubleAxe(RogueWeapons.golden_double_axe, Items.GOLD_INGOT);
        doubleAxe(RogueWeapons.diamond_double_axe, Items.DIAMOND);
    }

    /**
     * Generate double axe recipe with standard pattern: "MSM" / "MSM" / " S "
     */
    private void doubleAxe(Weapon.Entry axeEntry, Item material) {
        createShaped(RecipeCategory.COMBAT, axeEntry.item())
                .pattern("MSM")
                .pattern("MSM")
                .pattern(" S ")
                .input('M', material)
                .input('S', Items.STICK)
                .criterion(hasItem(material), conditionsFromItem(material))
                .offerTo(this.exporter);
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
        createShaped(RecipeCategory.COMBAT, glaiveEntry.item())
                .pattern(" MM")
                .pattern("MS ")
                .pattern("S  ")
                .input('M', material)
                .input('S', Items.STICK)
                .criterion(hasItem(material), conditionsFromItem(material))
                .offerTo(this.exporter);
    }

    // ========================================
    // ARMOR RECIPES
    // ========================================

    private void generateArmorRecipes() {
        // Rogue Armor (T1) - leather + wool + red dye
        generateRogueArmorSet(RogueArmors.RogueArmorSet_t1, Items.LEATHER, Items.RED_DYE);

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
        createShaped(RecipeCategory.COMBAT, armorSet.head)
                .pattern("WDW")
                .pattern(" W ")
                .input('D', redDye)
                .input('W', ItemTags.WOOL)
                .criterion(hasItem(leather), conditionsFromItem(leather))
                .offerTo(this.exporter);

        // Chestplate - pattern: "W W" / "LWL" / "LLL"
        createShaped(RecipeCategory.COMBAT, armorSet.chest)
                .pattern("W W")
                .pattern("LWL")
                .pattern("LLL")
                .input('L', leather)
                .input('W', ItemTags.WOOL)
                .criterion(hasItem(leather), conditionsFromItem(leather))
                .offerTo(this.exporter);

        // Leggings - pattern: "WWW" / "L L" / "W W"
        createShaped(RecipeCategory.COMBAT, armorSet.legs)
                .pattern("WWW")
                .pattern("L L")
                .pattern("W W")
                .input('L', leather)
                .input('W', ItemTags.WOOL)
                .criterion(hasItem(leather), conditionsFromItem(leather))
                .offerTo(this.exporter);

        // Boots - pattern: "W W" / "L L"
        createShaped(RecipeCategory.COMBAT, armorSet.feet)
                .pattern("W W")
                .pattern("L L")
                .input('L', leather)
                .input('W', ItemTags.WOOL)
                .criterion(hasItem(leather), conditionsFromItem(leather))
                .offerTo(this.exporter);
    }

    /**
     * Generate Assassin armor set (T2) - rabbit hide + ink sac + gold
     */
    private void generateAssassinArmorSet(Armor.Set armorSet, Item rabbitHide, Item inkSac, Item gold) {
        // Helmet - pattern: "SGS" / "R R"
        createShaped(RecipeCategory.COMBAT, armorSet.head)
                .pattern("SGS")
                .pattern("R R")
                .input('S', inkSac)
                .input('G', gold)
                .input('R', rabbitHide)
                .criterion(hasItem(rabbitHide), conditionsFromItem(rabbitHide))
                .offerTo(this.exporter);

        // Chestplate - pattern: "S S" / "RGR" / "RRR"
        createShaped(RecipeCategory.COMBAT, armorSet.chest)
                .pattern("S S")
                .pattern("RGR")
                .pattern("RRR")
                .input('S', inkSac)
                .input('G', gold)
                .input('R', rabbitHide)
                .criterion(hasItem(rabbitHide), conditionsFromItem(rabbitHide))
                .offerTo(this.exporter);

        // Leggings - pattern: "SGS" / "R R" / "R R"
        createShaped(RecipeCategory.COMBAT, armorSet.legs)
                .pattern("SGS")
                .pattern("R R")
                .pattern("R R")
                .input('S', inkSac)
                .input('G', gold)
                .input('R', rabbitHide)
                .criterion(hasItem(rabbitHide), conditionsFromItem(rabbitHide))
                .offerTo(this.exporter);

        // Boots - pattern: "GSG" / "R R"
        createShaped(RecipeCategory.COMBAT, armorSet.feet)
                .pattern("GSG")
                .pattern("R R")
                .input('S', inkSac)
                .input('G', gold)
                .input('R', rabbitHide)
                .criterion(hasItem(rabbitHide), conditionsFromItem(rabbitHide))
                .offerTo(this.exporter);
    }

    /**
     * Generate Warrior armor set (T1) - iron + leather + string
     */
    private void generateWarriorArmorSet(Armor.Set armorSet, Item iron, Item leather, Item string) {
        // Helmet - pattern: "ILI" / "I I"
        createShaped(RecipeCategory.COMBAT, armorSet.head)
                .pattern("ILI")
                .pattern("I I")
                .input('I', iron)
                .input('L', leather)
                .criterion(hasItem(iron), conditionsFromItem(iron))
                .offerTo(this.exporter);

        // Chestplate - pattern: "C C" / "III" / "LLL"
        createShaped(RecipeCategory.COMBAT, armorSet.chest)
                .pattern("C C")
                .pattern("III")
                .pattern("LLL")
                .input('I', iron)
                .input('L', leather)
                .input('C', string)
                .criterion(hasItem(iron), conditionsFromItem(iron))
                .offerTo(this.exporter);

        // Leggings - pattern: "III" / "L L" / "I I"
        createShaped(RecipeCategory.COMBAT, armorSet.legs)
                .pattern("III")
                .pattern("L L")
                .pattern("I I")
                .input('I', iron)
                .input('L', leather)
                .criterion(hasItem(iron), conditionsFromItem(iron))
                .offerTo(this.exporter);

        // Boots - pattern: "I I" / "L L"
        createShaped(RecipeCategory.COMBAT, armorSet.feet)
                .pattern("I I")
                .pattern("L L")
                .input('I', iron)
                .input('L', leather)
                .criterion(hasItem(iron), conditionsFromItem(iron))
                .offerTo(this.exporter);
    }

    /**
     * Generate Berserker armor set (T2) - chain + netherite_scrap + goat_horn + leather
     */
    private void generateBerserkerArmorSet(Armor.Set armorSet, Item chain, Item netheriteScrap, Item goatHorn, Item leather) {
        // Helmet - pattern: "GTG" / "I I"
        createShaped(RecipeCategory.COMBAT, armorSet.head)
                .pattern("GTG")
                .pattern("I I")
                .input('I', chain)
                .input('G', goatHorn)
                .input('T', netheriteScrap)
                .criterion(hasItem(netheriteScrap), conditionsFromItem(netheriteScrap))
                .offerTo(this.exporter);

        // Chestplate - pattern: "T T" / "III" / "LLL"
        createShaped(RecipeCategory.COMBAT, armorSet.chest)
                .pattern("T T")
                .pattern("III")
                .pattern("LLL")
                .input('I', chain)
                .input('T', netheriteScrap)
                .input('L', leather)
                .criterion(hasItem(netheriteScrap), conditionsFromItem(netheriteScrap))
                .offerTo(this.exporter);

        // Leggings - pattern: "III" / "T T" / "I I"
        createShaped(RecipeCategory.COMBAT, armorSet.legs)
                .pattern("III")
                .pattern("T T")
                .pattern("I I")
                .input('I', chain)
                .input('T', netheriteScrap)
                .criterion(hasItem(netheriteScrap), conditionsFromItem(netheriteScrap))
                .offerTo(this.exporter);

        // Boots - pattern: "T T" / "I I"
        createShaped(RecipeCategory.COMBAT, armorSet.feet)
                .pattern("T T")
                .pattern("I I")
                .input('I', chain)
                .input('T', netheriteScrap)
                .criterion(hasItem(netheriteScrap), conditionsFromItem(netheriteScrap))
                .offerTo(this.exporter);
    }

    // ========================================
    // OTHER RECIPES
    // ========================================

    private void generateOtherRecipes() {
        // Arms Workbench - pattern: "PIW" / "###"
        createShaped(RecipeCategory.MISC, CustomBlocks.WORKBENCH.block())
                .pattern("PIW")
                .pattern("###")
                .input('P', Items.PAPER)
                .input('I', Items.IRON_INGOT)
                .input('W', ItemTags.WOOL)
                .input('#', ItemTags.PLANKS)
                .criterion(hasItem(Items.PAPER), conditionsFromItem(Items.PAPER))
                .showNotification(false)
                .offerTo(this.exporter);
    }

    // ========================================
    // NETHERITE UPGRADE RECIPES
    // ========================================

    private void generateNetheriteUpgrades() {
        // Weapon upgrades - diamond to netherite
        offerNetheriteUpgradeRecipe(RogueWeapons.diamond_dagger.item(), RecipeCategory.COMBAT, RogueWeapons.netherite_dagger.item());
        offerNetheriteUpgradeRecipe(RogueWeapons.diamond_sickle.item(), RecipeCategory.COMBAT, RogueWeapons.netherite_sickle.item());
        offerNetheriteUpgradeRecipe(RogueWeapons.diamond_double_axe.item(), RecipeCategory.COMBAT, RogueWeapons.netherite_double_axe.item());
        offerNetheriteUpgradeRecipe(RogueWeapons.diamond_glaive.item(), RecipeCategory.COMBAT, RogueWeapons.netherite_glaive.item());

        // Assassin armor upgrades (T2 -> T3)
        offerNetheriteUpgradeRecipe(RogueArmors.RogueArmorSet_t2.head, RecipeCategory.COMBAT, RogueArmors.RogueArmorSet_t3.head);
        offerNetheriteUpgradeRecipe(RogueArmors.RogueArmorSet_t2.chest, RecipeCategory.COMBAT, RogueArmors.RogueArmorSet_t3.chest);
        offerNetheriteUpgradeRecipe(RogueArmors.RogueArmorSet_t2.legs, RecipeCategory.COMBAT, RogueArmors.RogueArmorSet_t3.legs);
        offerNetheriteUpgradeRecipe(RogueArmors.RogueArmorSet_t2.feet, RecipeCategory.COMBAT, RogueArmors.RogueArmorSet_t3.feet);

        // Berserker armor upgrades (T2 -> T3)
        offerNetheriteUpgradeRecipe(RogueArmors.WarriorArmorSet_t2.head, RecipeCategory.COMBAT, RogueArmors.WarriorArmorSet_t3.head);
        offerNetheriteUpgradeRecipe(RogueArmors.WarriorArmorSet_t2.chest, RecipeCategory.COMBAT, RogueArmors.WarriorArmorSet_t3.chest);
        offerNetheriteUpgradeRecipe(RogueArmors.WarriorArmorSet_t2.legs, RecipeCategory.COMBAT, RogueArmors.WarriorArmorSet_t3.legs);
        offerNetheriteUpgradeRecipe(RogueArmors.WarriorArmorSet_t2.feet, RecipeCategory.COMBAT, RogueArmors.WarriorArmorSet_t3.feet);
    }

    }

    @Override
    public String getName() {
        return "Rogue Crafting Recipes";
    }
}
