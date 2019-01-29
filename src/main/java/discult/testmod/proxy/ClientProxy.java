package discult.testmod.proxy;

import cpw.mods.fml.client.registry.RenderingRegistry;
import discult.testmod.testing.EntityTest;
import discult.testmod.testing.RenderTest;

public class ClientProxy extends CommonProxy 
{
	
	@Override
	public void preInit() 
	{
		super.preInit();
		RenderingRegistry.registerEntityRenderingHandler(EntityTest.class, new RenderTest());
	}
	
}
