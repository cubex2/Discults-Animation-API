package discult.testmod.testing;

import discult.animationapi.client.renderer.RenderAnimationBase;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderTest extends RenderAnimationBase
{

	public RenderTest() 
	{
		super(0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) 
	{
		return new ResourceLocation("test", "textures/mobs/test.png");
	}
	
}
