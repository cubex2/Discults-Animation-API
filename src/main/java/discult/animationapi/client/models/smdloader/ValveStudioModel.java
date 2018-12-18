package discult.animationapi.client.models.smdloader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.Function;

import org.lwjgl.opengl.GL11;

import discult.animationapi.AnimationAPI;
import discult.animationapi.helpers.RegexPatterns;
import net.minecraft.client.renderer.OpenGlHelper;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;

/**
 * Loader based on the ValveStudio Model (SMD)
 * https://developer.valvesoftware.com/wiki/Studiomdl_Data
 *
 * @author Discult/Jordan Ellison
 *
 */
public class ValveStudioModel implements IModelCustom
{
   public SMDModel body;
   public HashMap<String, SMDAnimation> anims;
   protected Bone root;
   ArrayList<Bone> allBones;
   public SMDAnimation currentAnimation;
   public ResourceLocation resource;
   protected String materialPath;
   public static boolean debugModel = false;
   private boolean hasAnimations;
   protected boolean usesMaterials;
   public boolean overrideSmoothShading;
   public boolean hasChanged;
   
   public int animations = 6;

   public ValveStudioModel(ValveStudioModel model)
   {
      this.anims = new HashMap<String, SMDAnimation>(animations);
      this.hasAnimations = false;
      this.usesMaterials = false;
      this.overrideSmoothShading = false;
      this.hasChanged = true;
      this.body = new SMDModel(model.body, this);

      for(Entry entry : model.anims.entrySet())
      {
         this.anims.put((String) entry.getKey(), new SMDAnimation((SMDAnimation)entry.getValue(), this));
      }

      this.hasAnimations = model.hasAnimations;
      this.usesMaterials = model.usesMaterials;
      this.resource = model.resource;
      this.currentAnimation = (SMDAnimation)this.anims.get("idle");
      this.overrideSmoothShading = model.overrideSmoothShading;
   }

   public ValveStudioModel(ResourceLocation resource, boolean overrideSmoothShading) throws GabeNewellException
   {
      this.anims = new HashMap<String, SMDAnimation>(animations);
      this.hasAnimations = false;
      this.usesMaterials = false;
      this.overrideSmoothShading = false;
      this.hasChanged = true;
      this.overrideSmoothShading = overrideSmoothShading;
      this.resource = resource;
      this.loadQC(resource);
      this.reformBones();
      this.precalculateAnims();
   }

   public ValveStudioModel(ResourceLocation resource) throws GabeNewellException
   {
      this(resource, false);
   }

   private void loadQC(ResourceLocation resloc) throws GabeNewellException
   {
	  InputStream inputStream = new BufferedInputStream(AnimationAPI.class.getResourceAsStream("/assets/" + resloc.getResourceDomain() + "/" + resloc.getResourcePath()));
	  new BufferedReader(new InputStreamReader(inputStream));
      String currentLine = null;
      int lineCount = 0;
      String[] bodyParams = null;
      ArrayList<String[]> animParams = new ArrayList<String[]>();

      try
      {
         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

         while((currentLine = reader.readLine()) != null)
         {
            ++lineCount;
            String[] params = RegexPatterns.MULTIPLE_WHITESPACE.split(currentLine);
            if (params[0].equalsIgnoreCase("$body"))
            {
               bodyParams = params;
            } else if (params[0].equalsIgnoreCase("$anim"))
            {
               if (this.anims == null)
               {
                  this.anims = new HashMap<String, SMDAnimation>(animations);
               }

               this.hasAnimations = true;
               animParams.add(params);
            } else if (params[0].equalsIgnoreCase("$cdmaterials"))
            {
               this.usesMaterials = true;
               this.materialPath = params[1];
            }
         }

         ResourceLocation modelPath = this.getResource(bodyParams[1]);
         this.body = new SMDModel(this, modelPath);

         for(String[] animPars : animParams)
         {
            String animName = animPars[1];
            ResourceLocation animPath = this.getResource(animPars[2]);
            this.anims.put(animName, new SMDAnimation(this, animName, animPath));
            if (animName.equalsIgnoreCase("idle"))
            {
               this.currentAnimation = (SMDAnimation)this.anims.get(animName);
            }
         }

      } catch (GabeNewellException var13)
      {
         throw var13;
      } catch (Exception var14)
      {
         throw new GabeNewellException("An error occurred reading the QC file on line #" + lineCount, var14);
      }
   }

   public ResourceLocation getResource(String fileName)
   {
      String urlAsString = this.resource.getResourcePath();
      int lastIndex = urlAsString.lastIndexOf(47);
      String startString = urlAsString.substring(0, lastIndex);
      return new ResourceLocation(AnimationAPI.MODID, startString + "/" + fileName);
   }

   private void precalculateAnims()
   {
      for(SMDAnimation anim : this.anims.values())
      {
         anim.precalculateAnimation(this.body);
      }

   }

   public void renderAll()
   {
	   GL11.glShadeModel(7425); 
	   this.body.render();
	   GL11.glShadeModel(7424);
   }

   void sendBoneData(SMDModel model)
   {
      this.allBones = model.bones;
      if (!model.isBodyGroupPart)
      {
         this.root = model.root;
      }

   }

   private void reformBones()
   {
      this.root.reformChildren();
      this.allBones.forEach(Bone::invertRestMatrix);
   }

   public void animate()
   {
      this.resetVerts(this.body);
      if (this.body.currentAnim == null)
      {
         this.setAnimation("idle");
      }

      this.root.setModified();
      this.allBones.forEach(Bone::applyModified);
      this.applyVertChange(this.body);
      this.hasChanged = true;
   }

   private void resetVerts(SMDModel model)
   {
      if (model != null)
      {
         model.verts.forEach(DeformVertex::reset);
      }
   }

   private void applyVertChange(SMDModel model)
   {
      if (model != null)
      {
         model.verts.forEach(DeformVertex::applyChange);
      }
   }

   public void setAnimation(String animname)
   {
      if (this.anims.containsKey(animname))
      {
         this.currentAnimation = (SMDAnimation)this.anims.get(animname);
      } else {
         this.currentAnimation = (SMDAnimation)this.anims.get("idle");
      }

      this.body.setAnimation(this.currentAnimation);
   }

   public static void print(Object o)
   {
      if (debugModel)
      {
         System.out.println(o);
      }

   }

   public void renderOnly(String... groupNames) {}
   
   public void renderAllExcept(String... excludedGroupNames) {}
   
   public boolean hasAnimations()
   {
      return this.hasAnimations;
   }

   //TODO possible remove
   protected String getMaterialPath(String subFile)
   {
      String result = "/assets/" + AnimationAPI.MODID;
      if (!this.materialPath.startsWith("/"))
      {
         result = result + "/";
      }

      result = result + this.materialPath;
      if (!subFile.startsWith("/"))
      {
         result = result + "/";
      }

      result = result + subFile;
      int lastDot = result.lastIndexOf(".");
      result = lastDot == -1 ? result + ".mat" : result.substring(0, lastDot) + ".mat";
      return result;
   }

@Override
public String getType() 
{
	return "qc";
}

public void renderPart(String partName) {}

}
