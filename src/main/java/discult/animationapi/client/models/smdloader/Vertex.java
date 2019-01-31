package discult.animationapi.client.models.smdloader;

/**
 * Loader based on the ValveStudio Model (SMD)
 * https://developer.valvesoftware.com/wiki/Studiomdl_Data
 *
 * @author Discult/Jordan Ellison
 *
 */
public class Vertex {
	public float x;
	public float y;
	public float z;

	public Vertex(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
