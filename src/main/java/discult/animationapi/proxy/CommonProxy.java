package discult.animationapi.proxy;

import java.io.BufferedInputStream;

import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import discult.animationapi.AnimationAPI;
import discult.animationapi.testing.EntityTest;
import net.minecraft.util.ResourceLocation;

public class CommonProxy 
{
	public void preInit() 
	{
		int id = 201;
		EntityRegistry.registerGlobalEntityID(EntityTest.class, "test", id);
		EntityRegistry.registerModEntity(EntityTest.class, "Test", id, AnimationAPI.INSTANCE, 64, 1, false);
	}
	
	public void init() 
	{
	}
	
	public void postInit()
	{
		
	}
	
	public BufferedInputStream getStreamForResourceLocation(ResourceLocation resourceLocation)
    {
        return new BufferedInputStream(AnimationAPI.class.getResourceAsStream("/assets/" + resourceLocation.getResourceDomain() + "/" + resourceLocation.getResourcePath()));
    }
	
}
