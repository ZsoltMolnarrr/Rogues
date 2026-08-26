package net.rogues.client.entity;

import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.Identifier;
import net.rogues.RoguesMod;
import net.rogues.entity.BearTrapEntity;

// Made with Blockbench 5.1.4
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class BearTrapEntityModel extends EntityModel<BearTrapEntityRenderer.State> {
	private final KeyframeAnimation spawnAnimation;
	private final KeyframeAnimation idleAnimation;
	private final KeyframeAnimation attackAnimation;
	private final KeyframeAnimation despawnAnimation;

	public BearTrapEntityModel(ModelPart layerRoot) {
		// NOTE: the model root must be the inner "root" part, not `layerRoot`. `ModelPart.createPartGetter()`
		// (which `AnimationDefinition.createAnimation` uses to bind bones) maps the name "root" to the part
		// it is called on, so binding a bone literally called "root" only works when that part IS the root.
		super(layerRoot.getChild("root"));
		this.spawnAnimation = BearTrapEntityAnimations.spawn.bake(this.root);
		this.idleAnimation = BearTrapEntityAnimations.idle.bake(this.root);
		this.attackAnimation = BearTrapEntityAnimations.attack.bake(this.root);
		this.despawnAnimation = BearTrapEntityAnimations.despawn.bake(this.root);
	}
	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		PartDefinition root = modelPartData.addOrReplaceChild("root", CubeListBuilder.create().texOffs(28, 28).addBox(-8.0F, -2.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 22).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition beartrap_part_1 = root.addOrReplaceChild("beartrap_part_1", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -3.2F, -6.0F, 15.0F, 4.0F, 7.0F, new CubeDeformation(0.01F)), PartPose.offset(1.5F, -1.0F, -1.0F));

		PartDefinition beartrap_part_2 = root.addOrReplaceChild("beartrap_part_2", CubeListBuilder.create().texOffs(0, 11).mirror().addBox(-7.0F, -3.2F, -1.0F, 15.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-0.5F, -1.0F, 1.0F));
		return LayerDefinition.create(modelData, 64, 32);
	}

	// HAND-WRITTEN CODE

	public static final ModelLayerLocation LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(RoguesMod.NAMESPACE, "bear_trap"), "main");

	@Override
	public void setupAnim(BearTrapEntityRenderer.State state) {
		super.setupAnim(state); // resets every part's transform
		// Lifecycle-phase driven: SPAWNING plays `spawn` (trap drops in, jaws open to armed),
		// ACTIVE loops `idle`, DESPAWNING plays either `attack` or `despawn` — see animateDespawn.
		this.spawnAnimation.apply(state.spawnAnimationState, state.ageInTicks, 1F);
		this.idleAnimation.apply(state.idleAnimationState, state.ageInTicks, 1F);
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
		if (!animationState.isStarted()) {
			return;
		}
		KeyframeAnimation clip;
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
		long remainingMs = -animationState.getTimeInMillis(state.ageInTicks);
		long elapsedMs = windDownTicks * 50L - remainingMs;
		clip.apply(elapsedMs, 1F);
	}
}
