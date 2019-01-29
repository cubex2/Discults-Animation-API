package discult.testmod.proxy;

import cpw.mods.fml.common.registry.EntityRegistry;
import discult.animationapi.AnimationAPI;
import discult.testmod.TestMod;
import discult.testmod.testing.EntityTest;

public class CommonProxy 
{
	public void preInit() 
	{
		int id = 201;
		EntityRegistry.registerGlobalEntityID(EntityTest.class, "testmod", id);
		EntityRegistry.registerModEntity(EntityTest.class, "TestMod", id, TestMod.INSTANCE, 64, 1, false);
	}
	
	public void init() 
	{
	}
	
	public void postInit()
	{
		
	}
}
