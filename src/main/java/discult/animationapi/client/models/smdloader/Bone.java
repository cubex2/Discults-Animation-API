package discult.animationapi.client.models.smdloader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import discult.animationapi.animation.IModulized;
import discult.animationapi.animation.ModelData;

import org.lwjgl.util.vector.Matrix4f;

/**
 * Loader based on the ValveStudio Model (SMD)
 * https://developer.valvesoftware.com/wiki/Studiomdl_Data
 *
 * @author Discult/Jordan Ellison
 *
 */
public class Bone implements IModulized {
	public Bone copy = null;
	public String name;
	public int ID;
	public Bone parent;
	public SMDModel owner;
	private Boolean isDummy;
	public Matrix4f rest;
	public Matrix4f restInverted;
	public Matrix4f modified = new Matrix4f();
	public Matrix4f difference = new Matrix4f();
	public Matrix4f prevInverted = new Matrix4f();
	public ArrayList<Bone> children = new ArrayList<Bone>();
	public HashMap<DeformVertex, Float> verts = new HashMap<DeformVertex, Float>();
	public HashMap<String, HashMap<Integer, Matrix4f>> animatedTransforms = new HashMap<String, HashMap<Integer, Matrix4f>>();

	public Bone(String name, int ID, Bone parent, SMDModel owner) {
		this.name = name;
		this.ID = ID;
		this.parent = parent;
		this.owner = owner;
	}

	public Bone(Bone bone, Bone parent, SMDModel owner) {
		this.name = bone.name;
		this.ID = bone.ID;
		this.owner = owner;
		this.parent = parent;

		for (Map.Entry<DeformVertex, Float> entry : bone.verts.entrySet()) {
			this.verts.put((DeformVertex) owner.verts.get((entry.getKey()).ID), entry.getValue());
		}

		this.animatedTransforms = new HashMap<String, HashMap<Integer, Matrix4f>>(bone.animatedTransforms);
		this.restInverted = bone.restInverted;
		this.rest = bone.rest;
		bone.copy = this;
	}

	public String getBoneByName(String name) {
		if (this.name == name) {
			return this.name;
		} else {
			System.out.println(name + " Does not excest");
			return null;
		}

	}

	public void setChildren(Bone bone, ArrayList<Bone> bones) {
		for (int i = 0; i < bone.children.size(); ++i) {
			Bone child = bone.children.get(i);
			this.children.add((Bone) bones.get(child.ID));
			((Bone) bones.get(child.ID)).parent = this;
		}

	}

	public boolean isDummy() {
		return this.isDummy == null ? (this.isDummy = this.parent == null && this.children.isEmpty()) : this.isDummy;
	}

	public void setRest(Matrix4f resting) {
		this.rest = resting;
	}

	public void addChild(Bone child) {
		this.children.add(child);
	}

	public void addVertex(DeformVertex vertex, float weight) {
		if (this.name.equals("blender_implicit")) {
			throw new UnsupportedOperationException("NO.");
		} else {
			this.verts.put(vertex, weight);
		}
	}

	private void reform(Matrix4f parentMatrix) {
		this.rest = Matrix4f.mul(parentMatrix, this.rest, null);
		if (ValveStudioModel.debugModel) {
			System.out.println(this.name + ' ' + this.parent);
		}

		this.reformChildren();
	}

	public void reformChildren() {
		for (Bone child : this.children) {
			child.reform(this.rest);
		}

	}

	public void invertRestMatrix() {
		this.restInverted = Matrix4f.invert(this.rest, null);
	}

	public void reset() {
		this.modified.setIdentity();
	}

	public void preloadAnimation(Frame key, Matrix4f animated) {
		HashMap<Integer, Matrix4f> precalcArray;
		if (this.animatedTransforms.containsKey(key.owner.animationName)) {
			precalcArray = (HashMap<Integer, Matrix4f>) this.animatedTransforms.get(key.owner.animationName);
		} else {
			precalcArray = new HashMap<Integer, Matrix4f>();
		}

		precalcArray.put(key.ID, animated);
		this.animatedTransforms.put(key.owner.animationName, precalcArray);
	}

	public void setModified() {
		Matrix4f realInverted;
		Matrix4f real;
		if (this.owner.owner.hasAnimations() && this.owner.currentAnim != null) {
			Frame currentFrame = (Frame) this.owner.currentAnim.frames.get(this.owner.currentAnim.currentFrameIndex);
			realInverted = (Matrix4f) currentFrame.transforms.get(this.ID);
			real = (Matrix4f) currentFrame.invertTransforms.get(this.ID);
		} else {
			realInverted = this.rest;
			real = this.restInverted;
		}

		Matrix4f delta = new Matrix4f();
		Matrix4f absolute = new Matrix4f();
		Matrix4f.mul(realInverted, real, delta);
		this.modified = this.parent != null ? Matrix4f.mul(this.parent.modified, delta, this.initModified()) : delta;
		Matrix4f.mul(real, this.modified, absolute);
		Matrix4f.invert(absolute, this.prevInverted);
		this.children.forEach(Bone::setModified);
	}

	protected Matrix4f initModified() {
		return this.modified == null ? (this.modified = new Matrix4f()) : this.modified;
	}

	public void applyModified() {
		Frame currentFrame = this.owner.currentFrame();
		if (currentFrame != null) {
			HashMap<Integer, Matrix4f> precalcArray = (HashMap<Integer, Matrix4f>) this.animatedTransforms
					.get(currentFrame.owner.animationName);
			Matrix4f animated = (Matrix4f) precalcArray.get(currentFrame.ID);
			Matrix4f animatedChange = new Matrix4f();
			Matrix4f.mul(animated, this.restInverted, animatedChange);
			this.modified = this.modified == null ? animatedChange
					: Matrix4f.mul(this.modified, animatedChange, this.modified);
		}

		this.verts.entrySet().forEach((entry) -> {
			((DeformVertex) entry.getKey()).applyModified(this, entry.getValue());
		});
		this.reset();
	}

	public float setValue(float value, ModelData d) {
		return value;
	}

	public float getValue(ModelData d) {
		return 0.0F;
	}
}