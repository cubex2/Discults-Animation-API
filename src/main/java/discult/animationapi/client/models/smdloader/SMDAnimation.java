package discult.animationapi.client.models.smdloader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.lwjgl.util.vector.Matrix4f;

import discult.animationapi.AnimationAPI;
import discult.animationapi.helpers.RegexPatterns;
import discult.animationapi.helpers.VectorHelper;
import net.minecraft.util.ResourceLocation;

/**
 * Loader based on the ValveStudio Model (SMD)
 * https://developer.valvesoftware.com/wiki/Studiomdl_Data
 *
 * @author Discult/Jordan Ellison
 *
 */
public class SMDAnimation {
	public final ValveStudioModel owner;
	public ArrayList<Frame> frames = new ArrayList<Frame>();
	public ArrayList<Bone> bones = new ArrayList<Bone>();
	public int currentFrameIndex = 0;
	public int lastFrameIndex;
	public int totalFrames;
	public String animationName;
	private int frameIDBank = 0;

	public SMDAnimation(ValveStudioModel owner, String animationName, ResourceLocation resloc)
			throws GabeNewellException {
		this.owner = owner;
		this.animationName = animationName;
		this.loadSmdAnim(resloc);
		this.setBoneChildren();
		this.reform();
	}

	public SMDAnimation(SMDAnimation anim, ValveStudioModel owner) {
		this.owner = owner;
		this.animationName = anim.animationName;

		for (Bone b : anim.bones) {
			this.bones.add(new Bone(b, b.parent != null ? (Bone) this.bones.get(b.parent.ID) : null, (SMDModel) null));
		}

		this.frames.addAll(anim.frames.stream().map((f) -> {
			return new Frame(f, this);
		}).collect(Collectors.toList()));
		this.totalFrames = anim.totalFrames;
	}

	private void loadSmdAnim(ResourceLocation resloc) throws GabeNewellException {
		InputStream inputStream = AnimationAPI.PROXY.getStreamForResourceLocation(resloc);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String currentLine = null;
		int lineCount = 0;

		try {
			while ((currentLine = reader.readLine()) != null) {
				++lineCount;
				if (!currentLine.startsWith("version")) {
					if (currentLine.startsWith("nodes")) {
						++lineCount;

						while (!(currentLine = reader.readLine()).startsWith("end")) {
							++lineCount;
							this.parseBone(currentLine, lineCount);
						}
					} else if (currentLine.startsWith("skeleton")) {
						this.startParsingAnimation(reader, lineCount, resloc);
					}
				}
			}

		} catch (IOException var7) {
			if (lineCount == -1) {
				throw new GabeNewellException("there was a problem opening the model file : " + resloc, var7);
			} else {
				throw new GabeNewellException(
						"an error occurred reading the SMD file \"" + resloc + "\" on line #" + lineCount, var7);
			}
		}
	}

	private void parseBone(String line, int lineCount) {
		String[] params = line.split("\"");
		int id = Integer.parseInt(RegexPatterns.SPACE_SYMBOL.matcher(params[0]).replaceAll(""));
		String boneName = params[1];
		int parentID = Integer.parseInt(RegexPatterns.SPACE_SYMBOL.matcher(params[2]).replaceAll(""));
		Bone parent = parentID >= 0 ? (Bone) this.bones.get(parentID) : null;
		this.bones.add(id, new Bone(boneName, id, parent, (SMDModel) null));
		ValveStudioModel.print(boneName);
	}

	private void startParsingAnimation(BufferedReader reader, int count, ResourceLocation resloc)
			throws GabeNewellException {
		int currentTime = 0;
		int lineCount = count + 1;
		String currentLine = null;

		try {
			while ((currentLine = reader.readLine()) != null) {
				++lineCount;
				String[] params = RegexPatterns.MULTIPLE_WHITESPACE.split(currentLine);
				if (params[0].equalsIgnoreCase("time")) {
					currentTime = Integer.parseInt(params[1]);
					this.frames.add(currentTime, new Frame(this));
				} else {
					if (currentLine.startsWith("end")) {
						this.totalFrames = this.frames.size();
						ValveStudioModel.print("Total number of frames = " + this.totalFrames);
						return;
					}

					int boneIndex = Integer.parseInt(params[0]);
					float[] locRots = new float[6];

					for (int i = 1; i < 7; ++i) {
						locRots[i - 1] = Float.parseFloat(params[i]);
					}

					Matrix4f animated = VectorHelper.matrix4FromLocRot(locRots[0], -locRots[1], -locRots[2], locRots[3],
							-locRots[4], -locRots[5]);
					((Frame) this.frames.get(currentTime)).addTransforms(boneIndex, animated);
				}
			}

		} catch (Exception var11) {
			throw new GabeNewellException(
					"an error occurred reading the SMD file \"" + resloc + "\" on line #" + lineCount, var11);
		}
	}

	public int requestFrameID() {
		int result = this.frameIDBank++;
		return result;
	}

	private void setBoneChildren() {
		for (int i = 0; i < this.bones.size(); ++i) {
			Bone theBone = (Bone) this.bones.get(i);
			this.bones.stream().filter((child) -> {
				return child.parent == theBone;
			}).forEach(theBone::addChild);
		}

	}

	public void reform() {
		int rootID = this.owner.body.root.ID;

		for (Frame frame : this.frames) {
			frame.fix(rootID, 0.0F);
			frame.reform();
		}

	}

	public void precalculateAnimation(SMDModel model) {
		for (Frame frame1 : this.frames) {
			model.resetVerts();
			Frame frame = frame1;

			for (int j = 0; j < model.bones.size(); ++j) {
				Bone bone = (Bone) model.bones.get(j);
				Matrix4f animated = (Matrix4f) frame.transforms.get(j);
				bone.preloadAnimation(frame, animated);
			}
		}

	}

	public int getNumFrames() {
		return this.frames.size();
	}

	public void setCurrentFrame(int i) {
		if (this.lastFrameIndex != i) {
			this.currentFrameIndex = i;
			this.lastFrameIndex = i;
		}

	}
}