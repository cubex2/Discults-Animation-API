package discult.animationapi.entities.base;

import discult.animationapi.helpers.CommonHelper;

import java.lang.reflect.Field;

import discult.animationapi.animation.RotationData;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityLookHelper;

public enum EnumEntityValue {
	x(RotationData.x, false), y(RotationData.y, false), z(RotationData.z, false), pitch(RotationData.x, true),
	yaw(RotationData.y, true), headPitch(RotationData.x, true), headYaw(RotationData.y, true);

	public final RotationData theAxis;
	public final boolean isRotation;
	public static Field lookHelperDeltaYaw;
	public static Field lookHelperDeltaPitch;

	private EnumEntityValue(RotationData theAxis, boolean isRotation) {
		this.theAxis = theAxis;
		this.isRotation = isRotation;
	}

	public double getDelta(EntityLivingBase entity) {
		try {
			switch (this) {
			case headPitch:
				return (double) lookHelperDeltaPitch.getFloat(((EntityLiving) entity).getLookHelper());
			case headYaw:
				return (double) lookHelperDeltaYaw.getFloat(((EntityLiving) entity).getLookHelper());
			case yaw:
				return (double) (entity.rotationYaw - entity.prevRotationYaw);
			case pitch:
				return (double) (entity.rotationPitch - entity.prevRotationPitch);
			case x:
			case y:
			case z:
				return CommonHelper.getLocalMotion(entity, this.theAxis);
			}
		} catch (Exception var3) {
			;
		}

		return Double.NaN;
	}

	static {
		Field[] lookHelperFields = EntityLookHelper.class.getDeclaredFields();
		lookHelperDeltaYaw = lookHelperFields[1];
		lookHelperDeltaPitch = lookHelperFields[2];
		lookHelperDeltaYaw.setAccessible(true);
		lookHelperDeltaPitch.setAccessible(true);
	}
}
