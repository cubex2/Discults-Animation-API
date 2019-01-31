package discult.animationapi.client.models.base;

import org.lwjgl.opengl.GL11;

import net.minecraftforge.client.model.IModelCustom;

public class ModelCustomWrapper {
	public IModelCustom model;
	int frame = 0;
	float offsetX = 0.0F;
	float offsetY = 0.0F;
	float offsetZ = 0.0F;

	public ModelCustomWrapper(IModelCustom modelIn) {
		this.model = modelIn;
	}

	public ModelCustomWrapper(IModelCustom modelIn, float x, float y, float z) {
		this.model = modelIn;
		this.offsetX = x;
		this.offsetY = y;
		this.offsetZ = z;
	}

	public ModelCustomWrapper setOffsets(float x, float y, float z) {
		this.offsetX = x;
		this.offsetY = y;
		this.offsetZ = z;
		return this;
	}

	public void render(float scale) {
		GL11.glPushMatrix();
		GL11.glScalef(scale, scale, scale);
		this.model.renderAll();
		GL11.glPopMatrix();
	}

	public void renderOffset(float scale) {
		GL11.glPushMatrix();
		GL11.glScalef(scale, scale, scale);
		GL11.glTranslatef(this.offsetX, this.offsetZ, this.offsetY);
		this.model.renderAll();
	}
}