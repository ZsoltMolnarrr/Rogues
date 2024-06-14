package net.rogues.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.rogues.RoguesMod;
import net.rogues.block.CustomBlocks;
import net.rogues.client.effect.ChargeParticles;
import net.rogues.client.effect.DemoralizeParticles;
import net.rogues.client.effect.ShatterParticles;
import net.rogues.effect.Effects;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.render.StunParticleSpawner;
import net.spell_engine.client.gui.SpellTooltip;

public class RoguesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(CustomBlocks.WORKBENCH.block(), RenderLayer.getCutout());
        CustomParticleStatusEffect.register(Effects.SHOCK, new StunParticleSpawner());
        CustomParticleStatusEffect.register(Effects.SHATTER, new ShatterParticles(1));
        CustomParticleStatusEffect.register(Effects.DEMORALIZE, new DemoralizeParticles(1));
        CustomParticleStatusEffect.register(Effects.CHARGE, new ChargeParticles(1));

        SpellTooltip.addDescriptionMutator(new Identifier(RoguesMod.NAMESPACE, "slice_and_dice"), (args) -> {
            var description = args.description();
            description = description.replace(SpellTooltip.placeholder("max_stack"), "" + Effects.sliceAndDiceMaxStacks());
            return description;
        });

        var armorReduction = ((int)(-1F * RoguesMod.tweaksConfig.value.shattered_armor_multiplier * 100)) + "%";
        SpellTooltip.addDescriptionMutator(new Identifier(RoguesMod.NAMESPACE, "throw"), (args) -> {
            var description = args.description();
            description = description.replace(SpellTooltip.placeholder("armor_reduction"), armorReduction);
            return description;
        });

        var damageReduction = ((int)(-1F * RoguesMod.tweaksConfig.value.shout_damage_multiplier * 100)) + "%";
        SpellTooltip.addDescriptionMutator(new Identifier(RoguesMod.NAMESPACE, "shout"), (args) -> {
            var description = args.description();
            description = description.replace(SpellTooltip.placeholder("damage_reduction"), damageReduction);
            return description;
        });
    }
}
