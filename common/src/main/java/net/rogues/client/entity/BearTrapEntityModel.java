package net.rogues.client.entity;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import net.rogues.RoguesMod;
import net.rogues.entity.BearTrapEntity;

// Made with Blockbench 5.1.4
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class BearTrapEntityModel extends EntityModel<BearTrapEntityRenderer.State> {
	private final Animation spawnAnimation;
	private final Animation idleAnimation;
	private final Animation attackAnimation;
	private final Animation despawnAnimation;

	public BearTrapEntityModel(ModelPart layerRoot) {
		// NOTE: the model root must be the inner "root" part, not `layerRoot`. `ModelPart.createPartGetter()`
		// (which `AnimationDefinition.createAnimation` uses to bind bones) maps the name "root" to the part
		// it is called on, so binding a bone literally called "root" only works when that part IS the root.
		super(layerRoot.getChild("root"));
		this.spawnAnimation = BearTrapEntityAnimations.spawn.createAnimation(this.root);
		this.idleAnimation = BearTrapEntityAnimations.idle.createAnimation(this.root);
		this.attackAnimation = BearTrapEntityAnimations.attack.createAnimation(this.root);
		this.despawnAnimation = BearTrapEntityAnimations.despawn.createAnimation(this.root);
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create().uv(28, 28).cuboid(-8.0F, -2.0F, -1.0F, 16.0F, 2.0F, 2.0F, new Dilation(0.0F))
		.uv(0, 22).cuboid(-3.0F, -3.0F, -3.0F, 6.0F, 2.0F, 6.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 24.0F, 0.0F));

		ModelPartData beartrap_part_1 = root.addChild("beartrap_part_1", ModelPartBuilder.create().uv(0, 0).cuboid(-9.0F, -3.2F, -6.0F, 15.0F, 4.0F, 7.0F, new Dilation(0.01F)), ModelTransform.origin(1.5F, -1.0F, -1.0F));

		ModelPartData beartrap_part_2 = root.addChild("beartrap_part_2", ModelPartBuilder.create().uv(0, 11).mirrored().cuboid(-7.0F, -3.2F, -1.0F, 15.0F, 4.0F, 7.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.origin(-0.5F, -1.0F, 1.0F));
		return TexturedModelData.of(modelData, 64, 32);
	}

	// HAND-WRITTEN CODE

	public static final EntityModelLayer LAYER = new EntityModelLayer(Identifier.of(RoguesMod.NAMESPACE, "bear_trap"), "main");

	@Override
	public void setAngles(BearTrapEntityRenderer.State state) {
		super.setAngles(state); // resets every part's transform
		// Lifecycle-phase driven: SPAWNING plays `spawn` (trap drops in, jaws open to armed),
		// ACTIVE loops `idle`, DESPAWNING plays either `attack` or `despawn` — see animateDespawn.
		this.spawnAnimation.apply(state.spawnAnimationState, state.age, 1F);
		this.idleAnimation.apply(state.idleAnimationState, state.age, 1F);
		animateDespawn(state);
	}

	/// Plays the wind-down clip head-to-tail. A trap that caught something plays `attack` (jaws snap
	/// shut, then it sinks); one that timed out plays `despawn` (jaws close, it sinks). The two phases
	/// are different lengths, so the clip and the duration have to be chosen together.
	///
	/// `SpellCloud` starts `despawnAnimationState` at the entity's *removal* age, so the state clock
	/// runs negative up to zero. Sibling models (banner, summons) exploit that by reverse-playing
	/// their spawn clip at speed -1F. We instead have purpose-made forward clips, so ask the state for
	/// the milliseconds remaining (negating the countdown makes it positive) and subtract it from the
	/// phase length to recover elapsed time.
	private void animateDespawn(BearTrapEntityRenderer.State state) {
		var animationState = state.despawnAnimationState;
		if (!animationState.isRunning()) {
			return;
		}
		Animation clip;
		int windDownTicks;
		if (state.sprung) {
			clip = this.attackAnimation;
			windDownTicks = BearTrapEntity.ATTACK_TICKS;
		} else {
			if (state.despawnTicks <= 0) {
				return;
			}
			clip = this.despawnAnimation;
			windDownTicks = state.despawnTicks;
		}
		long remainingMs = -animationState.getTimeInMilliseconds(state.age);
		long elapsedMs = windDownTicks * 50L - remainingMs;
		clip.apply(elapsedMs, 1F);
	}
}
