package discult.animationapi.client.models.smdloader;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.IModelCustomLoader;

/**
 * Loader based on the ValveStudio Model (SMD)
 * https://developer.valvesoftware.com/wiki/Studiomdl_Data
 *
 * @author Discult/Jordan Ellison
 *
 */
public class ValveStudioModelLoader
implements IModelCustomLoader
{
private static final String[] types = { "qc" };
public static final ValveStudioModelLoader instance = new ValveStudioModelLoader();

public String getType()
{
  return "Valve Studio Model";
}

public String[] getSuffixes()
{
  return types;
}

public IModelCustom loadInstance(ResourceLocation resource)
  throws GabeNewellException
{
  return new ValveStudioModel(resource);
}
}
