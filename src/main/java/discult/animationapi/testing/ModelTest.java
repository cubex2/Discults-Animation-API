package discult.animationapi.testing;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import discult.animationapi.client.models.base.AnimationModelBase;
import discult.animationapi.client.models.base.AnimationModelRenderer;
import discult.animationapi.client.models.base.ModelCustomWrapper;
import discult.animationapi.client.models.smdloader.ValveStudioModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class ModelTest extends AnimationModelBase
{
	/*
	 * This is the ModelRenderer
	 */
	AnimationModelRenderer body = new AnimationModelRenderer(this, "body");
	
	/*
	 * I recommend that you make i class that holds all this information as it is repeated alone all models.
	 * SEE BOTTOM OF CLASS
	 */
	
	public ModelTest()
	{
		ValveStudioModel model = this.setModel("animationapi", "models/mobs/test/test");
		body.addCustomModel(new ModelCustomWrapper(model));
		body.setRotationPoint(0.0F, 0.0F, 0.0F);
		body.rotateAngleX = (-(float)Math.PI/2F);
		theModel = model;
		this.setAnimationIncrement(1.0F);
	}
	
	
	/*
	 * renders the model taking its scale
	 */
	@Override
	public void render(Entity var1, float f, float f1, float f2, float f3, float f4, float f5) 
	{
		super.render(var1, f, f1, f2, f3, f4, f5);
		body.render(f5);
	}
	
	
	/*
	 * This is where you would register your animations I recommend that you keep the super active.
	 * Uses if statements set priority by putting the highest priority one at the top.
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public void doAnimation(Entity entity) 
	{
		super.doAnimation(entity);
		
		if(!entityMoving(mob))
		{
			if(mob.animationSwap)
			{
				this.setAnimation("idle");
			}
		}
		
	}
	
	
	
	
	/**
	 * Example of a class to store all
	 */
	/*
	 * AnimationModel is only an example call it whatever
	 * 
	public class AnimationModel extends AnimationModelBase
	{
		AnimationModelRenderer body = new AnimationModelRenderer(this, "body");
		
		public AnimationModel(String location)
		{
			ValveStudioModel model = new ValveStudioModel(new ResourceLocation(modid, location + ".qc");
			body.addCustomModel(new ModelCustomWrapper(model));
			body.setRotationPoint(0.0F, 0.0F, 0.0F);
			body.rotateAngleX = (-(float)Math.PI/2F);
			theModel = model;
			this.setAnimationIncrement(1.0F);
		}
		@Override
		public void render(Entity var1, float f, float f1, float f2, float f3, float f4, float f5) 
		{
			super.render(var1, f, f1, f2, f3, f4, f5);
			body.render(f5);
		}
		
	}
	*/
	
	/**
	 * Example of a class to add the model
	 */
	/*
	public class MyModel extends AnimationModel
	{
		
		public MyModel()
		{
			super("models/mobs/MOBNAME/MOBNAME");
		}
		
		@SideOnly(Side.CLIENT)
		@Override
		public void doAnimation(Entity entity) 
		{
			super.doAnimation(entity);
			
			ANIMCODE
		}
	*/
	
	
	
	
	/*
	 * EXAMPLE of what you can add
	 * 
	@SideOnly(Side.CLIENT)
	@Override
	public void doAnimation(Entity entity) 
	{
		super.doAnimation(entity);
		
		
		
		if(CODE TO MAKE ENTITY ATTACK)
		{
			if(entityMoving(mob))
			{
				if(mob.animationSwap)
				{
					this.setAnimation("walk_attack");
				}
			}
			else
			{
				if(mob.animationSwap)
				{
					this.setAnimation("idle_attack");
				}
			}
		}
		else if(entityMoving(mob))
		{
			if(mob.animationSwap)
			{
				this.setAnimation("walk");
			}
		}
		
		else
		{
			if(mob.animationSwap)
			{
				this.setAnimation("idle");
			}
		}
	}
	*/
	
	
	
}
