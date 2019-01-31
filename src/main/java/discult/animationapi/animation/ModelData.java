package discult.animationapi.animation;

public enum ModelData {
	xLoc, yLoc, ZLoc, xRot, yRot, zRot;

	public boolean isRotation() {
		return this == xRot || this == yRot || this == zRot;
	}

}
