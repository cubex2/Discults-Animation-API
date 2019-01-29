package discult.animationapi.testing;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public class ItemRegister 
{
	
	public static Item TestSpawnEgg;
	
	public static List<Item> items = new ArrayList<Item>();
	public static void registerItems()
	{
		items.add(TestSpawnEgg = new TestSpawnEgg());
		
		for(Item item:items)
		{
			GameRegistry.registerItem(item, item.getUnlocalizedName());
		}
		
	}
}
