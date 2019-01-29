package discult.testmod.testing;

import discult.animationapi.entities.base.EntityAnimationBase;
import net.minecraft.world.World;

public class EntityTest extends EntityAnimationBase
{

	public EntityTest(World worldIn) 
	{
		super(worldIn);
		this.setModel(new ModelTest());
		this.setSize(1, 1);
	}
	
}
