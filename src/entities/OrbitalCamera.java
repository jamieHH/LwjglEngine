package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class OrbitalCamera extends Camera {

	private Entity target;
	private float distFromTarget = 50;
	private float angleAroundTarget = 0;

	public OrbitalCamera(Entity target) {
		this.target = target;
		pitch = 20;
	}
	
	public void move() {
		calcZoom();
		calcPitch();
		calcAngleAroundTarget();
		float horizDistance = calcHorizontalDistance();
		float verticDistance = calcVerticalDistance();
		calcCameraPosition(horizDistance, verticDistance);
		this.yaw = 180 - (target.getRotY() + angleAroundTarget);
	}

	private void calcCameraPosition(float horizDistance, float verticDistance) {
		float theta = target.getRotY() + angleAroundTarget;
		float offsX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
		float offsZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
		position.x = target.getPosition().x - offsX;
		position.y = target.getPosition().y + verticDistance;
		position.z = target.getPosition().z - offsZ;
	}

	private float calcHorizontalDistance() {
		return  (float) (distFromTarget * Math.cos(Math.toRadians(pitch)));
	}

	private float calcVerticalDistance() {
		return  (float) (distFromTarget * Math.sin(Math.toRadians(pitch)));
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
			if (pitch - pitchDiff < 90) {
				if (pitch - pitchDiff > 0) {
					pitch -= pitchDiff;
				} else {
					pitch = 0;
				}
			} else {
				pitch = 90;
			}
		}
	}

	private void calcAngleAroundTarget() {
		if (Mouse.isButtonDown(1)) {
			float angleDiff = Mouse.getDX() * 0.3f;
			angleAroundTarget -= angleDiff;
		}
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
}
