package discult.animationapi.client.models.base;

import discult.animationapi.animation.IncrementingVariable;
import discult.animationapi.client.models.smdloader.GabeNewellException;
import discult.animationapi.client.models.smdloader.SMDAnimation;
import discult.animationapi.client.models.smdloader.ValveStudioModel;
import discult.animationapi.entities.base.EntityAnimationBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class AnimationModelBase extends ModelBase {
	public EntityAnimationBase mob;
	protected float animationIncrement = 1.0F;
	public ValveStudioModel theModel;
	protected float movementThreshold = 0.2F;

	public AnimationModelBase() {
		this.registerAnimationCounters();
	}

	public boolean entityMoving(Entity entity) {
		mob = (EntityAnimationBase) entity;
		if (mob.limbSwingAmount > movementThreshold) {
			return true;
		}
		return false;
	}

	private static ResourceLocation modelLocation(String path) {
		return new ResourceLocation(path + ".qc");
	}

	public static ValveStudioModel setModel(String modid, String path) {
		return setModel(modid + ":" + path);
	}

	public static ValveStudioModel setModel(String path) {
		ValveStudioModel model = null;

		try {
			model = new ValveStudioModel(modelLocation(path));
		} catch (GabeNewellException e) {
			e.printStackTrace();
		}
		return model;
	}

	public void render(Entity var1, float f, float f1, float f2, float f3, float f4, float f5) {
	}

	protected void setAnimationIncrement(float f) {
		this.animationIncrement = f;
	}

	public void doAnimation(Entity entity) {
		mob = (EntityAnimationBase) entity;

		IncrementingVariable inc = this.getCounter(-1, mob);
		if (inc == null) {
			this.setCounter(-1, 2.14748365E9F, this.animationIncrement, mob);
		} else {
			inc.increment = this.animationIncrement;
		}

		if (!mob.animationCounting) {
			this.setAnimation("idle");
			mob.animationCounting = true;
		}

		if (this.theModel.hasAnimations()) {
			this.tickAnimation(mob);
		}
	}

	private void tickAnimation(EntityAnimationBase mob) {
		SMDAnimation theAnim = this.theModel.currentAnimation;
		int frame = (int) Math.floor((double) (this.getCounter(-1, mob).value % (float) theAnim.totalFrames));
		theAnim.setCurrentFrame(frame);
		this.theModel.animate();
	}

	protected void setAnimation(String string) {
		this.theModel.setAnimation(string);
	}

	public static boolean isMinecraftPaused() {
		Minecraft m = Minecraft.getMinecraft();
		return m.isSingleplayer() && m.currentScreen != null && m.currentScreen.doesGuiPauseGame()
				&& !m.getIntegratedServer().getPublic();
	}

	protected void setInt(int id, int value, EntityAnimationBase mob) {
		mob.getAnimationVariables().setInt(id, value);
	}

	protected int getInt(int id, EntityAnimationBase mob) {
		return mob.getAnimationVariables().getInt(id);
	}

	private IncrementingVariable setCounter(int id, float limit, float increment, EntityAnimationBase mob) {
		mob.getAnimationVariables().setCounter(id, limit, increment);
		return this.getCounter(id, mob);
	}

	private IncrementingVariable getCounter(int id, EntityAnimationBase mob) {
		return mob.getAnimationVariables().getCounter(id);
	}

	private void registerAnimationCounters() {
	}
}