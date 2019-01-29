package discult.animationapi.client.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import discult.animationapi.entities.base.EntityAnimationBase;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public abstract class RenderAnimationBase extends RenderLiving
{

	
	
	private static float xScale = -40, yScale = -40, zScale = 40;
	
	public RenderAnimationBase(float shadowSize) 
	{
		super(null, shadowSize);
	}
	

	@Override
	public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_,
			float p_76986_9_) 
	{
		this.renderMob((EntityAnimationBase)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
	}
	
	protected float interpolateRotation(float prevYawOffset, float yawOffset, float partialTicks)
    {
        float f;

        for (f = yawOffset - prevYawOffset; f < -180.0F; f += 360.0F)
        {
            ;
        }

        while (f >= 180.0F)
        {
            f -= 360.0F;
        }

        return prevYawOffset + partialTicks * f;
    }
	
	protected void applyRotations(EntityAnimationBase entityLiving, float p_77043_2_, float rotationYaw, float partialTicks)
    {
        GL11.glRotatef(180.0F - rotationYaw, 0.0F, 1.0F, 0.0F);

        if (entityLiving.deathTime > 0)
        {
            float f = ((float)entityLiving.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
            f = MathHelper.sqrt_float(f);

            if (f > 1.0F)
            {
                f = 1.0F;
            }

            GL11.glRotatef(f * this.getDeathMaxRotation(entityLiving), 0.0F, 0.0F, 1.0F);
        }

    }
	
	public void renderMob(EntityAnimationBase entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);
		
		mainModel = entity.getModel();
		this.mainModel.onGround = this.renderSwingProgress(entity, partialTicks);
		
		try
		{
			float f2 = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
            float f3 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
            float netHeadYaw = f3 - f2;
            float headPitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
            this.renderLivingAt(entity, x, y, z);
            float ageInTicks = this.handleRotationFloat(entity, partialTicks);
            this.applyRotations(entity, ageInTicks, f2, partialTicks);

            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_LIGHTING);
            
            //ModelSize Goes here
            modelScale();

            float limbSwingAmount = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
            float limbSwing = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);

            if (limbSwingAmount > 1.0F)
            {
                limbSwingAmount = 1.0F;
            }

            GL11.glEnable(GL11.GL_ALPHA_TEST);
            this.mainModel.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
            this.mainModel.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, 0.0625F, entity);

            this.renderModel(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, 0.0625F);
        
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
		}
		
		GL11.glPopMatrix();
	}
	
	public static void setModelScale(float x, float y, float z)
	{
		xScale = -x;
		yScale = -y;
		zScale = z;
	}
	
	private static void modelScale()
	{
		GL11.glScalef(xScale, yScale, zScale);
	}
	
}
