package discult.animationapi.client.models.smdloader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import discult.animationapi.AnimationAPI;
import discult.animationapi.helpers.CommonHelper;
import discult.animationapi.helpers.RegexPatterns;
import discult.animationapi.helpers.VectorHelper;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.TextureCoordinate;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBBufferObject;
import org.lwjgl.opengl.GL11;

/**
 * Loader based on the ValveStudio Model (SMD)
 * https://developer.valvesoftware.com/wiki/Studiomdl_Data
 *
 * @author Discult/Jordan Ellison
 *
 */
public class SMDModel
{
   public final ValveStudioModel owner;
   public ArrayList<NormalizedFace> faces = new ArrayList<NormalizedFace>(0);
   public ArrayList<DeformVertex> verts = new ArrayList<DeformVertex>(0);
   public ArrayList<Bone> bones = new ArrayList<Bone>(0);
   public HashMap<String, Bone> nameToBoneMapping = new HashMap<String, Bone>();
   //public HashMap<String, Material> materialsByName;
   //public HashMap<Material, ArrayList<NormalizedFace>> facesByMaterial;
   public SMDAnimation currentAnim;
   public String fileName;
   private int vertexIDBank = 0;
   protected boolean isBodyGroupPart;
   int lineCount = -1;
   public Bone root;
   public int vertexVbo = -1;
   public int textureVbo = -1;
   public int normalsVbo = -1;
   private FloatBuffer vertexBuffer;
   private FloatBuffer normalBuffer;
   
   //TODO adds a config to ingame menu to allow users to change this
   public static boolean isSmoothModel = false;

   public SMDModel(SMDModel model, ValveStudioModel owner)
   {
      this.owner = owner;
      this.isBodyGroupPart = model.isBodyGroupPart;

      for(NormalizedFace face : model.faces)
      {
         DeformVertex[] vertices = new DeformVertex[face.vertices.length];

         for(int i = 0; i < vertices.length; ++i)
         {
            DeformVertex d = new DeformVertex(face.vertices[i]);
            CommonHelper.ensureIndex(this.verts, d.ID);
            this.verts.set(d.ID, d);
         }
      }

      this.faces.addAll(model.faces.stream().map((face) -> { return new NormalizedFace(face, this.verts); }).collect(Collectors.toList()));

      for(int i = 0; i < model.bones.size(); ++i)
      {
         Bone b = (Bone)model.bones.get(i);
         this.bones.add(new Bone(b, (Bone)null, this));
      }

      for(int i = 0; i < model.bones.size(); ++i)
      {
         Bone b = (Bone)model.bones.get(i);
         b.copy.setChildren(b, this.bones);
      }

      this.root = model.root.copy;
      owner.sendBoneData(this);
   }

   public SMDModel(ValveStudioModel owner, ResourceLocation resloc) throws GabeNewellException
   {
      this.owner = owner;
      this.isBodyGroupPart = false;
      this.loadSmdModel(resloc, (SMDModel)null);
      this.setBoneChildren();
      this.determineRoot();
      owner.sendBoneData(this);
      ValveStudioModel.print("Number of vertices = " + this.verts.size());
   }

   public SMDModel(ValveStudioModel owner, ResourceLocation resloc, SMDModel body) throws GabeNewellException
   {
      this.owner = owner;
      this.isBodyGroupPart = true;
      this.loadSmdModel(resloc, body);
      this.setBoneChildren();
      this.determineRoot();
      owner.sendBoneData(this);
   }

   private void loadSmdModel(ResourceLocation resloc, SMDModel body) throws GabeNewellException 
   {
	  InputStream inputStream = new BufferedInputStream(AnimationAPI.class.getResourceAsStream("/assets/" + resloc.getResourceDomain() + "/" + resloc.getResourcePath()));
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      String currentLine = null;

      try
      {
         this.lineCount = 0;

         while((currentLine = reader.readLine()) != null)
         {
            ++this.lineCount;
            if (!currentLine.startsWith("version"))
            {
               if (currentLine.startsWith("nodes"))
               {
                  ++this.lineCount;

                  while(!(currentLine = reader.readLine()).startsWith("end"))
                  {
                     ++this.lineCount;
                     this.parseBone(currentLine, this.lineCount, body);
                  }

                  ValveStudioModel.print("Number of model bones = " + this.bones.size());
               } else if (currentLine.startsWith("skeleton"))
               {
                  ++this.lineCount;
                  reader.readLine();
                  ++this.lineCount;

                  while(!(currentLine = reader.readLine()).startsWith("end"))
                  {
                     ++this.lineCount;
                     if (!this.isBodyGroupPart) {
                        this.parseBoneValues(currentLine, this.lineCount);
                     }
                  }
               } else if (currentLine.startsWith("triangles"))
               {
                  ++this.lineCount;

                  while(!(currentLine = reader.readLine()).startsWith("end"))
                  {
                     String[] params = new String[3];

                     for(int i = 0; i < 3; ++i)
                     {
                        ++this.lineCount;
                        params[i] = reader.readLine();
                     }

                     this.parseFace(params, this.lineCount);
                  }
               }
            }
         }
      } catch (Exception var9)
      {
         if (this.lineCount == -1)
         {
            throw new GabeNewellException("there was a problem opening the model file : " + resloc, var9);
         }

         throw new GabeNewellException("an error occurred reading the SMD file \"" + resloc + "\" on line #" + this.lineCount, var9);
      }

      ValveStudioModel.print("Number of faces = " + this.faces.size());
   }
/**
   public Material requestMaterial(String materialName) throws GabeNewellException {
      if (!this.owner.usesMaterials) {
         return null;
      } else {
         if (this.materialsByName == null) {
            this.materialsByName = new HashMap<String, Material>();
         }

         Material result = (Material)this.materialsByName.get(materialName);
         if (result != null) {
            return result;
         } else {
            String materialPath = this.owner.getMaterialPath(materialName);
            URL materialURL = SMDModel.class.getResource(materialPath);

            try {
               File materialFile = new File(materialURL.toURI());
               result = new Material(materialFile);
               this.materialsByName.put(materialName, result);
               return result;
            } catch (Exception var6) {
               throw new GabeNewellException(var6);
            }
         }
      }
   }
   */

   private void parseBone(String line, int lineCount, SMDModel body)
   {
      String[] params = line.split("\"");
      int id = Integer.parseInt(RegexPatterns.SPACE_SYMBOL.matcher(params[0]).replaceAll(""));
      String boneName = params[1];
      Bone theBone = body != null ? body.getBoneByName(boneName) : null;
      if (theBone == null)
      {
         int parentID = Integer.parseInt(RegexPatterns.SPACE_SYMBOL.matcher(params[2]).replaceAll(""));
         Bone parent = parentID >= 0 ? (Bone)this.bones.get(parentID) : null;
         theBone = new Bone(boneName, id, parent, this);
      }

      CommonHelper.ensureIndex(this.bones, id);
      this.bones.set(id, theBone);
      this.nameToBoneMapping.put(boneName, theBone);
      ValveStudioModel.print(boneName);
   }

   private void parseBoneValues(String line, int lineCount)
   {
      String[] params = RegexPatterns.MULTIPLE_WHITESPACE.split(line);
      int id = Integer.parseInt(params[0]);
      float[] locRots = new float[6];

      for(int i = 1; i < 7; ++i)
      {
         locRots[i - 1] = Float.parseFloat(params[i]);
      }

      Bone theBone = (Bone)this.bones.get(id);
      theBone.setRest(VectorHelper.matrix4FromLocRot(locRots[0], -locRots[1], -locRots[2], locRots[3], -locRots[4], -locRots[5]));
   }

   private void parseFace(String[] params, int lineCount)
   {
      DeformVertex[] faceVerts = new DeformVertex[3];
      TextureCoordinate[] uvs = new TextureCoordinate[3];

      for(int i = 0; i < 3; ++i)
      {
         String[] values = RegexPatterns.MULTIPLE_WHITESPACE.split(params[i]);
         float x = Float.parseFloat(values[1]);
         float y = -Float.parseFloat(values[2]);
         float z = -Float.parseFloat(values[3]);
         float xn = Float.parseFloat(values[4]);
         float yn = -Float.parseFloat(values[5]);
         float zn = -Float.parseFloat(values[6]);
         DeformVertex v = this.getExisting(x, y, z);
         if (v == null)
         {
            faceVerts[i] = new DeformVertex(x, y, z, xn, yn, zn, this.vertexIDBank);
            CommonHelper.ensureIndex(this.verts, this.vertexIDBank);
            this.verts.set(this.vertexIDBank, faceVerts[i]);
            ++this.vertexIDBank;
         } else {
            faceVerts[i] = v;
         }

         uvs[i] = new TextureCoordinate(Float.parseFloat(values[7]), 1.0F - Float.parseFloat(values[8]));
         if (values.length > 10)
         {
            this.doBoneWeights(values, faceVerts[i]);
         }
      }

      NormalizedFace face = new NormalizedFace(faceVerts, uvs);
      face.vertices = faceVerts;
      face.textureCoordinates = uvs;
      this.faces.add(face);
      //if (mat != null) {
      //   if (this.facesByMaterial == null) {
       //     this.facesByMaterial = new HashMap<Material, ArrayList<NormalizedFace>>();
      //   }

        // ArrayList<NormalizedFace> list = this.facesByMaterial.get(mat);
        // if (list == null) {
        //    this.facesByMaterial.put(mat, list = new ArrayList<NormalizedFace>());
       //  }

        // list.add(face);
      //}

   }

   private DeformVertex getExisting(float x, float y, float z)
   {
      for(DeformVertex v : this.verts)
      {
         if (v.equals(x, y, z)) {
            return v;
         }
      }

      return null;
   }

   private void doBoneWeights(String[] values, DeformVertex vert)
   {
      int links = Integer.parseInt(values[9]);
      float[] weights = new float[links];
      float sum = 0.0F;

      for(int i = 0; i < links; ++i)
      {
         weights[i] = Float.parseFloat(values[i * 2 + 11]);
         sum += weights[i];
      }

      for(int i = 0; i < links; ++i)
      {
         int boneID = Integer.parseInt(values[i * 2 + 10]);
         float weight = weights[i] / sum;
         ((Bone)this.bones.get(boneID)).addVertex(vert, weight);
      }

   }

   private void setBoneChildren()
   {
      for(int i = 0; i < this.bones.size(); ++i)
      {
         Bone theBone = (Bone)this.bones.get(i);
         this.bones.stream().filter((child) -> { return child.parent == theBone; }).forEach(theBone::addChild);
      }

   }

   private void determineRoot()
   {
      for(Bone b : this.bones)
      {
         if (b.parent == null && !b.children.isEmpty())
         {
            this.root = b;
            break;
         }
      }

      if (this.root == null)
      {
         for(Bone b : this.bones)
         {
            if (!b.name.equals("blender_implicit"))
            {
               this.root = b;
               break;
            }
         }
      }

   }

   public void setAnimation(SMDAnimation anim)
   {
      this.currentAnim = anim;
     // System.out.println("Doing anim Stuff");
   }

   public Bone getBoneByID(int id)
   {
      try {
         return (Bone)this.bones.get(id);
      } catch (IndexOutOfBoundsException var3)
      {
         return null;
      }
   }

   public Bone getBoneByName(String name)
   {
      for(Bone b : this.bones)
      {
         if (b.name.equals(name))
         {
            return b;
         }
      }

      return null;
   }

   public Frame currentFrame()
   {
      return this.currentAnim == null ? null : (this.currentAnim.frames == null ? null : (this.currentAnim.frames.isEmpty() ? null : (Frame)this.currentAnim.frames.get(this.currentAnim.currentFrameIndex)));
   }

   public void resetVerts()
   {
      this.verts.forEach(DeformVertex::reset);
   }
   public void render()
   {
     GL11.glPushMatrix();
     GL11.glBegin(4);
     boolean isPokeball = false;
     if (this.owner.resource.getResourcePath().contains("pokeballs")) {
       isPokeball = true;
     }
     if (!this.owner.usesMaterials) {
       for (NormalizedFace f : this.faces) {
         f.addFaceForRender(isSmoothModel);
       }
     
     }
     GL11.glEnd();
     GL11.glPopMatrix();
   }
}
