package discult.animationapi.client.models.base;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;

public class AnimationModelRenderer extends ModelRenderer
{
	private int displayList;
    private boolean compiled = false;
    public ArrayList<ModelCustomWrapper> model = new ArrayList<ModelCustomWrapper>();
    private boolean isTransparent = false;
    private float transparency;

    public AnimationModelRenderer(ModelBase modelBase, String name) {
        super(modelBase, name);
    }

    public AnimationModelRenderer(ModelBase modelBase) {
        super(modelBase);
    }

    public AnimationModelRenderer(ModelBase modelBase, int i, int j) {
        super(modelBase, i, j);
    }

    public void addCustomModel(ModelCustomWrapper model) {
        this.model.add(model);
    }

    public void setTransparent(float transparency) {
        this.isTransparent = true;
        this.transparency = transparency;
    }

    @SideOnly(Side.CLIENT)
    public void render(float f) {
        if (!this.isHidden && this.showModel) {
            if (!this.compiled) {
                this.compileDisplayList(f);
            }

            GL11.glTranslatef(this.offsetX, this.offsetY, this.offsetZ);
            if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
                if (this.rotationPointX == 0.0F && this.rotationPointY == 0.0F && this.rotationPointZ == 0.0F) {
                	GL11.glCallList(this.displayList);
                    this.renderCustomModels(f);
                    if (this.childModels != null) {
                        for(int var2 = 0; var2 < this.childModels.size(); ++var2) {
                            ((ModelRenderer)this.childModels.get(var2)).render(f);
                        }
                    }
                } else {
                	GL11.glTranslatef(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
                    GL11.glCallList(this.displayList);
                    this.renderCustomModels(f);
                    if (this.childModels != null) {
                        for(int var2 = 0; var2 < this.childModels.size(); ++var2) {
                            ((ModelRenderer)this.childModels.get(var2)).render(f);
                        }
                    }

                    GL11.glTranslatef(-this.rotationPointX * f, -this.rotationPointY * f, -this.rotationPointZ * f);
                }
            } else {
                GL11.glPushMatrix();
                GL11.glTranslatef(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
                if (this.rotateAngleY != 0.0F) {
                	 GL11.glRotatef(this.rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
                }

                if (this.rotateAngleZ != 0.0F) {
                	 GL11.glRotatef(this.rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
                }

                if (this.rotateAngleX != 0.0F) {
                	 GL11.glRotatef(this.rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
                }

                GL11.glCallList(this.displayList);
                this.renderCustomModels(f);
                if (this.childModels != null) {
                    for(int var2 = 0; var2 < this.childModels.size(); ++var2) {
                        ((ModelRenderer)this.childModels.get(var2)).render(f);
                    }
                }

                GL11.glPopMatrix();
            }

            GL11.glTranslatef(-this.offsetX, -this.offsetY, -this.offsetZ);
        }

    }

    @SideOnly(Side.CLIENT)
    private void compileDisplayList(float f)
    {
      this.displayList = GLAllocation.generateDisplayLists(1);
      GL11.glNewList(this.displayList, 4864);
      Tessellator tess = Tessellator.instance;
      for (int i = 0; i < this.cubeList.size(); i++) {
        ((ModelBox)this.cubeList.get(i)).render(tess, f);
      }
      GL11.glEndList();
      this.compiled = true;
    }

    protected void renderCustomModels(float scale)
    {
      if (this.isTransparent)
      {
        GL11.glEnable(3042);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, this.transparency);
      }
      for (int i = 0; i < this.model.size(); i++) {
        ((ModelCustomWrapper)this.model.get(i)).render(scale);
      }
      if (this.isTransparent)
      {
        GL11.glDisable(3042);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      }
    }
}
