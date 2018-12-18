package discult.animationapi.helpers;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class VectorHelper
{
    static final Vector3f X_AXIS = new Vector3f(1.0F, 0.0F, 0.0F);
    static final Vector3f Y_AXIS = new Vector3f(0.0F, 1.0F, 0.0F);
    static final Vector3f Z_AXIS = new Vector3f(0.0F, 0.0F, 1.0F);

    public static Matrix4f matrix4FromLocRot(float xl, float yl, float zl, float xr, float yr, float zr)
    {
        Vector3f loc = new Vector3f(xl, yl, zl);
        Matrix4f part1 = new Matrix4f();
        part1.translate(loc);
        part1.rotate(zr, Z_AXIS);
        part1.rotate(yr, Y_AXIS);
        part1.rotate(xr, X_AXIS);
        return part1;
    }


    public static double[] rotate(double x, double y, double radians) {
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double newX = x * cos - y * sin;
        double newY = y * cos + x * sin;
        return new double[]{newX, newY};
    }



}
