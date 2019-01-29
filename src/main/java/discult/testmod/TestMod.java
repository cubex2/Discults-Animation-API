package discult.testmod;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import discult.animationapi.AnimationAPI;
import discult.animationapi.testing.ItemRegister;
import discult.testmod.proxy.CommonProxy;
import scala.reflect.internal.Trees.This;

@Mod(modid = "test", name = "Testing", version = "test1")
public class TestMod 
{
	@Mod.Instance("test")
    public static TestMod INSTANCE;
	
	@SidedProxy(clientSide = "discult.testmod.proxy.ClientProxy", serverSide = "discult.testmod.proxy.CommonProxy")
	public static CommonProxy PROXY;
	
	@Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        PROXY.preInit();
        
        //ItemRegister.registerItems();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {

        PROXY.init();


    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        PROXY.postInit();
    }
}
