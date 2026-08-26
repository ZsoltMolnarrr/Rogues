package net.rogues.mixin;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.rogues.RoguesMod;
import net.rogues.effect.RogueEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TargetGoal.class)
public class TrackTargetGoalStealth {
    @Shadow @Final protected Mob mob;

    /**
     * Both of these mixins achieve kinda the same thing.
     * The one with range is theoretically more elegant.
     */

//    @Inject(method = "shouldContinue", at = @At("HEAD"), cancellable = true)
//    private void shouldContinue_HEAD(CallbackInfoReturnable<Boolean> cir) {
//        var target = mob.getTarget();
//        if (target != null && target.hasStatusEffect(Effects.STEALTH)) {
//            cir.setReturnValue(false);
//        }
//    }

    @Inject(method = "getFollowDistance", at = @At("HEAD"), cancellable = true)
    private void getFollowRange_HEAD(CallbackInfoReturnable<Double> cir) {
        var target = mob.getTarget();
        if (target != null
                && (target.hasEffect(RogueEffects.STEALTH.entry) || target.hasEffect(RogueEffects.SHADOW_STEP.entry))) {
            cir.setReturnValue(RoguesMod.tweaksConfig.value.stealth_follow_range);
            cir.cancel();
        }
    }
}
