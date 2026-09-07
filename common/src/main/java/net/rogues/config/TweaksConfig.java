package net.rogues.config;

public class TweaksConfig { public TweaksConfig() {}
    public boolean ignore_items_required_mods = false;
    public double rebalance_strength_attack_damage_multiplier = 0.1;
    public double stealth_follow_range = 1.0;
    public double stealth_visibility_multiplier = 0.1;

    // Percentage-based damage enchantments — see `mixin/EnchantmentHelperMixin`.
    // The bonus becomes `(attacker base attack damage + weapon attack damage) * level * multiplier`
    // instead of vanilla's flat amount, so it scales with modded weapon tiers.
    //
    // The 1.21.1 line expresses this as a datapack enchantment override and ships one file,
    // `sharpness.json` (0.08/level), leaving Smite and Bane of Arthropods vanilla. These defaults
    // reproduce that. The legacy 1.20.1 branch also rebalanced Smite and Bane at 0.12/level; the
    // machinery is kept here so a pack can turn them back on, but they are off by default because
    // 1.21.1 — the balance of record — does not rebalance them.
    public boolean enable_rebalance_enchantment_sharpness = true;
    public float enchantment_sharpness_multiplier_per_level = 0.08F;
    public boolean enable_rebalance_enchantment_smite = false;
    public float enchantment_smite_multiplier_per_level = 0.08F;
    public boolean enable_rebalance_enchantment_arthropods = false;
    public float enchantment_arthropods_multiplier_per_level = 0.08F;
}
