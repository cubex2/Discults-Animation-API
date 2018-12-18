package discult.animationapi.helpers;

import java.util.ArrayList;

import discult.animationapi.animation.RotationData;
import net.minecraft.entity.Entity;

public class CommonHelper 
{
	//If i dont add <> to the end of array it allows me to sensureIndex for verts, bones ect without needed multiple methods
	public static void ensureIndex(ArrayList list, int i)
	{
	      while(list.size() <= i) {
	    	  list.add(null);
	      }

	} 
	
	
	public static double getLocalMotion(Entity entity, RotationData axis) {
	      if (axis == RotationData.y) {
	         return entity.motionY;
	      } else {
	         double[] locals = VectorHelper.rotate(entity.motionX, entity.motionZ, (double)(-entity.rotationYaw * ((float)Math.PI / 180F)));
	         return axis == RotationData.x ? (double)((float)locals[0]) : (double)((float)locals[1]);
	      }
	   }
	
	
}
