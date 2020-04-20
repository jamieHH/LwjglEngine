package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class OrbitalCamera extends Camera {

	private Entity target;
	private float distFromTarget = 20;
	private float angleAroundTarget = 0;

	public OrbitalCamera(Entity target) {
		this.target = target;
		super.setWorld(target.getWorld());
		setRotX(20);
	}
	
	public void tick() {
		calcZoom();
		calcPitch();
		calcAngleAroundTarget();
		float horizDistance = calcHorizontalDistance();
		float verticDistance = calcVerticalDistance();
		calcCameraPosition(horizDistance, verticDistance);
		setRotY(180 - (target.getRotY() + angleAroundTarget));
	}

	private void calcCameraPosition(float horizDistance, float verticDistance) {
		float theta = target.getRotY() + angleAroundTarget;
		float offsX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
		float offsZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
		setPosition(new Vector3f(target.getPosX() - offsX, target.getPosY() + verticDistance + 4, target.getPosZ() - offsZ));
	}

	private float calcHorizontalDistance() {
		return  (float) (distFromTarget * Math.cos(Math.toRadians(getRotX())));
	}

	private float calcVerticalDistance() {
		return  (float) (distFromTarget * Math.sin(Math.toRadians(getRotX())));
	}

	private void calcZoom() {
		float zoomLevel = Mouse.getDWheel() * 0.1f;
		if (distFromTarget - zoomLevel > 0) {
			distFromTarget -= zoomLevel;
		} else {
			distFromTarget = 0;
		}
	}

	private void calcPitch() {
		if (Mouse.isButtonDown(1)) {
			float pitchDiff = Mouse.getDY() * 0.1f;
			if (getRotX() - pitchDiff < 90) {
				if (getRotX() - pitchDiff > 0) {
					setRotX(getRotX() - pitchDiff);
				} else {
					setRotX(0);
				}
			} else {
				setRotX(90);
			}
		}
	}

	private void calcAngleAroundTarget() {
		if (Mouse.isButtonDown(1)) {
			float angleDiff = Mouse.getDX() * 0.3f;
			angleAroundTarget -= angleDiff;
		}
	}
}
