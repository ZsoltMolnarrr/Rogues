package net.rogues.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.VillagerTradeCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.spell_engine.api.item.SpellItemData;
import net.spell_engine.misc.criteria.SpellCastCriteria;
import net.spell_engine.spellbinding.SpellBindingCriteria;
import net.spell_engine.spellbinding.SpellBookCreationCriteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Generates the Rogue &amp; Warrior advancement tree using the standard {@link Advancement.Builder} API.
 * <p>
 * The advancements live in the shared {@code rpg_series} namespace (they are part of the RPG Series
 * progression UI) but their content is provided by the Rogues mod. {@link #entries()} is the single
 * source of truth: this provider exports each built {@link Advancement}, and
 * {@code RoguesDataGenerator.LangGen} reads the same list for the
 * {@code advancements.rpg_series.<path>.title/description} translation keys, which are derived from the
 * advancement id (see {@link #translationKey}). Every advancement id therefore matches its translation
 * suffix — e.g. {@code trade_with_arms_dealer} rather than a separate file name and key.
 */
public class RoguesAdvancements extends FabricAdvancementProvider {
    public static final String NAMESPACE = "rpg_series";

    /// 1.20.1 / Fabric API 0.92: `FabricAdvancementProvider` is registry-independent — a 1-arg constructor
    /// and `generateAdvancement(Consumer<Advancement>)` (there is no `AdvancementEntry` wrapper yet).
    public RoguesAdvancements(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateAdvancement(Consumer<Advancement> consumer) {
        for (var entry : entries()) {
            consumer.accept(entry.entry());
        }
    }

    /** A generated advancement paired with the plain-text strings behind its (id-derived) translation keys. */
    public record Entry(Advancement entry, String title, String description) {
        public String titleKey() { return translationKey(entry.getId(), "title"); }
        public String descriptionKey() { return translationKey(entry.getId(), "description"); }
    }

    static String translationKey(Identifier id, String suffix) {
        return "advancements." + id.getNamespace() + "." + id.getPath() + "." + suffix;
    }

    // MARK: Definitions

    private static List<Entry> entries;

    /** Lazily builds the advancement tree on first use (during data generation, when registries are ready). */
    public static List<Entry> entries() {
        if (entries != null) {
            return entries;
        }
        var list = new ArrayList<Entry>();

        // Class choice — create a spell book
        list.add(task("path_choose_rogue", "rpg_series:classes", spellBookIcon("rogue"),
                "book", spellBookCreation("rogues:spell_book/rogue"),
                "Path of Tricks", "Create a Rogue Handbook"));
        list.add(task("path_choose_warrior", "rpg_series:classes", spellBookIcon("warrior"),
                "book", spellBookCreation("rogues:spell_book/warrior"),
                "Path of Strength", "Create a Warrior's Codex"));

        // Obtain the first skill of a class — bind a single spell
        list.add(task("spell_novice_rogue", "rpg_series:path_choose_rogue", item("rogues:iron_dagger"),
                "bind", spellBinding("rogues:spell_book/rogue", false),
                "Sinister Intentions", "Obtain your first Rogue skill"));
        list.add(task("spell_novice_warrior", "rpg_series:path_choose_warrior", item("rogues:stone_double_axe"),
                "bind", spellBinding("rogues:spell_book/warrior", false),
                "Taste for Blood", "Obtain your first Warrior skill"));

        // Obtain all skills of a class — complete binding
        list.add(challenge("spell_master_rogue", "rpg_series:spell_novice_rogue", item("rogues:diamond_sickle"),
                "bind", spellBinding("rogues:spell_book/rogue", true),
                "Master of Deception", "Obtain all Rogue skills"));
        list.add(challenge("spell_master_warrior", "rpg_series:spell_novice_warrior", item("rogues:netherite_double_axe"),
                "bind", spellBinding("rogues:spell_book/warrior", true),
                "Rampage!", "Obtain all Warrior skills"));

        // Cast a skill from a spell book (silent — no toast or chat announcement)
        list.add(silent("spell_cast_rogue_book", "rpg_series:spell_novice_rogue", spellBookIcon("rogue"),
                "cast", spellCast("#rogues:spell_book/rogue"),
                "Rogue Training", "Use a skill from the Rogue Handbook"));
        list.add(silent("spell_cast_warrior_book", "rpg_series:spell_novice_warrior", spellBookIcon("warrior"),
                "cast", spellCast("#rogues:spell_book/warrior"),
                "Warrior Training", "Use a skill from the Warrior's Codex"));

        // Trade with the Arms Dealer villager
        list.add(secret("trade_with_arms_dealer", "rpg_series:misc_items", item("rogues:iron_double_axe"),
                "trade_with_arms_dealer", villagerTrade("rogues:arms_merchant"),
                "Arms Deal", "Trade with an Arms Dealer villager"));

        entries = list;
        return entries;
    }

    // MARK: Builder shorthands

    /** Visible task: toast + chat announcement on. */
    private static Entry task(String idPath, String parent, ItemStack icon,
                              String criterionName, CriterionConditions criterion, String title, String description) {
        return advancement(idPath, parent, icon, AdvancementFrame.TASK, true, true, false, criterionName, criterion, title, description);
    }

    /** Challenge-framed task. */
    private static Entry challenge(String idPath, String parent, ItemStack icon,
                                   String criterionName, CriterionConditions criterion, String title, String description) {
        return advancement(idPath, parent, icon, AdvancementFrame.CHALLENGE, true, true, false, criterionName, criterion, title, description);
    }

    /** Task without toast or chat announcement. */
    private static Entry silent(String idPath, String parent, ItemStack icon,
                                String criterionName, CriterionConditions criterion, String title, String description) {
        return advancement(idPath, parent, icon, AdvancementFrame.TASK, false, false, false, criterionName, criterion, title, description);
    }

    /** Hidden task: not shown in the tree until earned. */
    private static Entry secret(String idPath, String parent, ItemStack icon,
                                String criterionName, CriterionConditions criterion, String title, String description) {
        return advancement(idPath, parent, icon, AdvancementFrame.TASK, true, true, true, criterionName, criterion, title, description);
    }

    /// 1.20.1 `Advancement.Builder#build` refuses to build unless the parent *object* resolves
    /// (`findParent(id -> null)`), so `parent(Identifier)` alone throws "Tried to build incomplete
    /// advancement!". Cross-mod parents (the shared `rpg_series:*` tree) are therefore represented by a
    /// bare stub carrying only the id — `Advancement#createTask` serialises `parent` from that id, so the
    /// emitted JSON is identical to a hand-written `"parent": "rpg_series:..."`.
    private static Advancement parentStub(Identifier parentId) {
        return new Advancement(parentId, null, null, AdvancementRewards.NONE, Map.of(), new String[0][], false);
    }

    private static Entry advancement(String idPath, String parent, ItemStack icon, AdvancementFrame frame,
                                     boolean showToast, boolean announceToChat, boolean hidden,
                                     String criterionName, CriterionConditions criterion, String title, String description) {
        var id = new Identifier(NAMESPACE, idPath);
        // The original data-pack advancements did not send telemetry events (vanilla default is off),
        // so keep them untelemetered rather than using the telemetered Advancement.Builder.create().
        var entry = Advancement.Builder.createUntelemetered()
                .parent(parentStub(new Identifier(parent)))
                .display(
                        icon,
                        Text.translatable(translationKey(id, "title")),
                        Text.translatable(translationKey(id, "description")),
                        null,
                        frame,
                        showToast,
                        announceToChat,
                        hidden)
                .criterion(criterionName, criterion)
                .build(id);
        return new Entry(entry, title, description);
    }

    // MARK: Icon helpers

    private static ItemStack item(String itemId) {
        return new ItemStack(Registries.ITEM.get(new Identifier(itemId)));
    }

    private static ItemStack spellBookIcon(String book) {
        var stack = new ItemStack(Registries.ITEM.get(new Identifier("spell_engine", "spell_book")));
        // 1.20.1: no data components — the custom item model lives in the `spell_engine` NBT sub-compound.
        SpellItemData.setItemModel(stack, new Identifier("rogues", "item/spell_book/" + book));
        return stack;
    }

    // MARK: Criterion helpers

    /// 1.20.1 criteria are plain `AbstractCriterionConditions` instances handed to
    /// `Advancement.Builder#criterion(String, CriterionConditions)` — there is no `Criterion` wrapper.
    private static CriterionConditions spellBookCreation(String spellPool) {
        return new SpellBookCreationCriteria.Condition(Optional.of(spellPool));
    }

    private static CriterionConditions spellBinding(String spellPool, boolean complete) {
        return new SpellBindingCriteria.Condition(Optional.of(spellPool), Optional.of(complete));
    }

    private static CriterionConditions spellCast(String spell) {
        return new SpellCastCriteria.Condition(LootContextPredicate.EMPTY, spell, null);
    }

    private static CriterionConditions villagerTrade(String profession) {
        var villagerData = new NbtCompound();
        villagerData.putString("profession", profession);
        var nbt = new NbtCompound();
        nbt.put("VillagerData", villagerData);
        var villager = EntityPredicate.asLootContextPredicate(
                EntityPredicate.Builder.create().nbt(new NbtPredicate(nbt)).build());
        return new VillagerTradeCriterion.Conditions(LootContextPredicate.EMPTY, villager, ItemPredicate.ANY);
    }
}
