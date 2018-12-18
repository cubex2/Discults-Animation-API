package discult.animationapi;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import discult.animationapi.proxy.CommonProxy;

@Mod(modid = "animationapi", name = "Animation API", version = "0.1")
public class AnimationAPI 
{
	//The MODID for the animatons api so other MODs need to call in there preInit and set it to thier modid
	//TODO figure a way to do it without it
	public static String MODID = "animationapi";
	
	@SidedProxy(clientSide = "discult.animationapi.proxy.ClientProxy", serverSide = "discult.animationapi.proxy.CommonProxy")
	public static CommonProxy PROXY;
	
	@Mod.Instance("animationapi")
    public static AnimationAPI INSTANCE;
	
	@Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        PROXY.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {

        INSTANCE = this;
        PROXY.init();


    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        PROXY.postInit();
    }
	
}
