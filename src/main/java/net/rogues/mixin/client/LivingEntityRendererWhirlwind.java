package net.rogues.mixin.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.rogues.RoguesMod;
import net.spell_engine.internals.casting.SpellCasterEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererWhirlwind {
    private static final Identifier BLADESTORM_SPELL_ID = new Identifier(RoguesMod.NAMESPACE, "whirlwind");
    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    private void render_HEAD_SpellEngine(LivingEntity livingEntity, float f, float delta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci) {
        if (livingEntity instanceof SpellCasterEntity caster) {
            var process = caster.getSpellCastProcess();
            if (process != null && process.id().equals(BLADESTORM_SPELL_ID)) {
                var ticks = process.spellCastTicksSoFar(livingEntity.getWorld().getTime());

                var turn = (-18F) / (process.spell().cast.channel_ticks / 20F);
                var degress = turn * ticks + delta * turn;
                matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(degress));
            }
        }
    }
}
