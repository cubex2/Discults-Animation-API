package discult.animationapi.proxy;

import cpw.mods.fml.client.registry.RenderingRegistry;
import discult.animationapi.testing.EntityTest;
import discult.animationapi.testing.RenderTest;

public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit() 
	{
		super.preInit();
		RenderingRegistry.registerEntityRenderingHandler(EntityTest.class, new RenderTest());
	}
	
	@Override
	public void init() 
	{
		super.init();
	}
	
	@Override
	public void postInit()
	{
		super.postInit();
	}
}
