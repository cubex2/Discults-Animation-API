package discult.animationapi.entities.base;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import discult.animationapi.animation.AnimationVariables;
import discult.animationapi.client.models.base.AnimationModelBase;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

public class EntityAnimationBase extends EntityCreature {

	private ModelBase model;
	private AnimationVariables animationVariables;
	public boolean animationCounting = false;
	public boolean animationSwap = false;
	private int animationDelayCounter = 0;

	public EntityAnimationBase(World worldIn) {
		super(worldIn);
	}

	@SideOnly(Side.CLIENT)
	public ModelBase getModel() {
		return model;
	}

	public void setModel(ModelBase modelIn) {
		model = modelIn;
	}

	public AnimationVariables getAnimationVariables() {
		if (this.animationVariables == null) {
			this.animationVariables = new AnimationVariables();
		}

		return this.animationVariables;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		final int animationFlyingDelayLimit = 10;
		final int animationDelayLimit = 3;
		final int flyingDelayLimit = 10;
		boolean animationFlyingCounting = false;
		boolean animationFlyingSwap = false;

		if (worldObj.isRemote) {
			if (this.animationVariables != null) {
				this.animationVariables.tick();
			}
			if (this.animationCounting) {
				if (this.animationDelayCounter < animationDelayLimit) {
					this.animationDelayCounter += 1;
					this.animationSwap = false;
				}

				if (this.animationDelayCounter >= animationDelayLimit) {
					this.animationSwap = true;
					this.animationDelayCounter = 0;
				}
			} else {
				this.animationDelayCounter = 0;
				this.animationSwap = false;
			}

			if (getModel() instanceof AnimationModelBase && this instanceof EntityAnimationBase) {

				((AnimationModelBase) this.getModel()).doAnimation(this);
			}
		}

	}
}
