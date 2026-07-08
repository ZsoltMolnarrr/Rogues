package net.rogues.client.entity;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.Identifier;
import net.rogues.RoguesMod;
import net.rogues.entity.BearTrapEntity;
import org.joml.Vector3f;

// Made with Blockbench 5.1.4
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class BearTrapEntityModel extends SinglePartEntityModel<BearTrapEntity> {
	private final ModelPart root;
	public BearTrapEntityModel(ModelPart layerRoot) {
		// NOTE: `getPart()` must return this inner "root" part rather than `layerRoot`, because
		// SinglePartEntityModel.getChild("root") is special-cased to return getPart() outright — it
		// never looks up a child by that name. The clips animate a bone literally called "root".
		this.root = layerRoot.getChild("root");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create().uv(28, 28).cuboid(-8.0F, -2.0F, -1.0F, 16.0F, 2.0F, 2.0F, new Dilation(0.0F))
		.uv(0, 22).cuboid(-3.0F, -3.0F, -3.0F, 6.0F, 2.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData beartrap_part_1 = root.addChild("beartrap_part_1", ModelPartBuilder.create().uv(0, 0).cuboid(-9.0F, -3.0F, -6.0F, 15.0F, 4.0F, 7.0F, new Dilation(0.01F)), ModelTransform.pivot(1.5F, -1.0F, -1.0F));

		ModelPartData beartrap_part_2 = root.addChild("beartrap_part_2", ModelPartBuilder.create().uv(0, 11).mirrored().cuboid(-7.0F, -3.0F, -1.0F, 15.0F, 4.0F, 7.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-0.5F, -1.0F, 1.0F));
		return TexturedModelData.of(modelData, 64, 32);
	}

	// HAND-WRITTEN CODE

	public static final EntityModelLayer LAYER = new EntityModelLayer(Identifier.of(RoguesMod.NAMESPACE, "bear_trap"), "main");

	private static final Vector3f TEMP = new Vector3f();

	@Override
	public ModelPart getPart() {
		return root;
	}

	@Override
	public void setAngles(BearTrapEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);
		// Lifecycle-phase driven: SPAWNING plays `spawn` (trap drops in, jaws open to armed),
		// ACTIVE loops `idle`, DESPAWNING plays either `attack` or `despawn` — see animateDespawn.
		this.updateAnimation(entity.spawnAnimationState, BearTrapEntityAnimations.spawn, ageInTicks, 1F);
		this.updateAnimation(entity.idleAnimationState, BearTrapEntityAnimations.idle, ageInTicks, 1F);
		animateDespawn(entity, ageInTicks);
	}

	/// Plays the wind-down clip head-to-tail. A trap that caught something plays `attack` (jaws snap
	/// shut, then it sinks); one that timed out plays `despawn` (jaws close, it sinks). The two phases
	/// are different lengths, so the clip and the duration have to be chosen together.
	///
	/// `SpellCloud` starts `despawnAnimationState` at the entity's *removal* age, so the state clock
	/// runs negative up to zero. Sibling models (banner, summons) exploit that by reverse-playing
	/// their spawn clip at speed -1F. We instead have purpose-made forward clips, so ask the state for
	/// the milliseconds remaining (speed -1F flips the countdown positive) and subtract it from the
	/// phase length to recover elapsed time.
	private void animateDespawn(BearTrapEntity entity, float ageInTicks) {
		var state = entity.despawnAnimationState;
		if (!state.isRunning()) {
			return;
		}
		Animation clip;
		int windDownTicks;
		if (entity.isSprung()) {
			clip = BearTrapEntityAnimations.attack;
			windDownTicks = BearTrapEntity.ATTACK_TICKS;
		} else {
			var cloudData = entity.getCloudData();
			if (cloudData == null || cloudData.despawn_ticks <= 0) {
				return;
			}
			clip = BearTrapEntityAnimations.despawn;
			windDownTicks = cloudData.despawn_ticks;
		}
		state.update(ageInTicks, -1F);
		long remainingMs = state.getTimeRunning();
		long elapsedMs = windDownTicks * 50L - remainingMs;
		AnimationHelper.animate(this, clip, elapsedMs, 1F, TEMP);
	}
}
