package net.rogues.client;

import mod.azure.azurelibarmor.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelibarmor.rewrite.render.armor.AzArmorRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.rogues.RoguesMod;
import net.rogues.block.CustomBlocks;
import net.rogues.client.armor.RogueArmorRenderer;
import net.rogues.client.armor.WarriorArmorRenderer;
import net.rogues.client.effect.ChargeParticles;
import net.rogues.client.effect.DemoralizeParticles;
import net.rogues.client.effect.ShatterParticles;
import net.rogues.effect.RogueEffects;
import net.rogues.item.armor.Armors;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.item.armor.Armor;
import net.spell_engine.api.render.StunParticleSpawner;
import net.spell_engine.client.gui.SpellTooltip;

import java.util.function.Supplier;

public class RoguesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(CustomBlocks.WORKBENCH.block(), RenderLayer.getCutout());
        CustomParticleStatusEffect.register(RogueEffects.SHOCK.effect, new StunParticleSpawner());
        CustomParticleStatusEffect.register(RogueEffects.SHATTER.effect, new ShatterParticles(1));
        CustomParticleStatusEffect.register(RogueEffects.DEMORALIZE.effect, new DemoralizeParticles(1));
        CustomParticleStatusEffect.register(RogueEffects.CHARGE.effect, new ChargeParticles(1));

        SpellTooltip.addDescriptionMutator(Identifier.of(RoguesMod.NAMESPACE, "slice_and_dice"), (args) -> {
            var description = args.description();
            description = description.replace(SpellTooltip.placeholder("max_stack"), "" + RogueEffects.sliceAndDiceMaxStacks());
            return description;
        });

        SpellTooltip.addDescriptionMutator(Identifier.of(RoguesMod.NAMESPACE, "throw"), (args) -> {
            var description = args.description();
            var percent = SpellTooltip.percent(-1F * RogueEffects.SHATTER.config().firstModifier().value);
            description = description.replace(SpellTooltip.placeholder("armor_reduction"), percent);
            return description;
        });

        SpellTooltip.addDescriptionMutator(Identifier.of(RoguesMod.NAMESPACE, "shout"), (args) -> {
            var description = args.description();
            var percent = SpellTooltip.percent(-1F * RogueEffects.DEMORALIZE.config().firstModifier().value);
            description = description.replace(SpellTooltip.placeholder("damage_reduction"), percent);
            return description;
        });

        registerArmorRenderer(Armors.RogueArmorSet_t1, RogueArmorRenderer::rogue);
        registerArmorRenderer(Armors.RogueArmorSet_t2, RogueArmorRenderer::assassin);
        registerArmorRenderer(Armors.RogueArmorSet_t3, RogueArmorRenderer::netheriteAssassin);
        registerArmorRenderer(Armors.WarriorArmorSet_t1, WarriorArmorRenderer::warrior);
        registerArmorRenderer(Armors.WarriorArmorSet_t2, WarriorArmorRenderer::berserker);
        registerArmorRenderer(Armors.WarriorArmorSet_t3, WarriorArmorRenderer::netheriteBerserker);
    }

    private static void registerArmorRenderer(Armor.Set set, Supplier<AzArmorRenderer> armorRendererSupplier) {
        AzArmorRendererRegistry.register(armorRendererSupplier, set.head, set.chest, set.legs, set.feet);
    }
}