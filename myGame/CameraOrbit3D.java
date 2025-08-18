package myGame;

import tage.*;
import tage.input.action.AbstractInputAction;
import java.lang.Math;
import tage.input.InputManager;
import org.joml.*;
import net.java.games.input.Event;

public class CameraOrbit3D {

    private Engine engine;
    private Camera camera; // the camera being controlled
    private GameObject avatar; // the target avatar the camera looks at
    private float cameraAzimuth; // rotation around target Y axis
    private float cameraElevation; // elevation of camera above target
    private float cameraRadius; // distance between camera and target

    public CameraOrbit3D(Camera cam, GameObject av, String keyboardName, String gamepadName, Engine e) {
        engine = e;
        camera = cam;
        avatar = av;
        cameraAzimuth = 0.0f; // start BEHIND and ABOVE the target
        cameraElevation = 20.0f; // elevation is in degrees
        cameraRadius = 2.0f; // distance from camera to avatar
        setupInputs(keyboardName, gamepadName);
        updateCameraPosition();
    }

    private void setupInputs(String keyboardName, String gamepadName) {
        InputManager im = engine.getInputManager();
        OrbitAzimuthAction azmLeft = new OrbitAzimuthAction(-1);
        im.associateAction(keyboardName,
                net.java.games.input.Component.Identifier.Key.J, azmLeft,
                InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        OrbitAzimuthAction azmRight = new OrbitAzimuthAction(1);
        im.associateAction(keyboardName,
                net.java.games.input.Component.Identifier.Key.K, azmRight,
                InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

        OrbitRadiusAction zoomInAction = new OrbitRadiusAction(-0.5f);
        im.associateAction(keyboardName,
                net.java.games.input.Component.Identifier.Key.Z, zoomInAction,
                InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

        OrbitRadiusAction zoomOutAction = new OrbitRadiusAction(0.5f);
        im.associateAction(keyboardName,
                net.java.games.input.Component.Identifier.Key.X, zoomOutAction,
                InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

        OrbitElevationAction elevationUpAction = new OrbitElevationAction(1.0f);
        im.associateAction(keyboardName,
                net.java.games.input.Component.Identifier.Key._1, elevationUpAction,
                InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

        OrbitElevationAction elevationDownAction = new OrbitElevationAction(-1.0f);
        im.associateAction(keyboardName,
                net.java.games.input.Component.Identifier.Key._2, elevationUpAction,
                InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

        // Add gamepad inputs here
        if (gamepadName != null) {
            im.associateAction(gamepadName,
                    net.java.games.input.Component.Identifier.Button._7, azmLeft,
                    InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
            im.associateAction(gamepadName,
                    net.java.games.input.Component.Identifier.Button._6, azmRight,
                    InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
            im.associateAction(gamepadName,
                    net.java.games.input.Component.Identifier.Button._4, zoomInAction,
                    InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
            im.associateAction(gamepadName,
                    net.java.games.input.Component.Identifier.Button._5, zoomOutAction,
                    InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
            // L3
            im.associateAction(gamepadName,
                    net.java.games.input.Component.Identifier.Button._10, elevationUpAction,
                    InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
            // R3
            im.associateAction(gamepadName,
                    net.java.games.input.Component.Identifier.Button._11, elevationDownAction,
                    InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

        }

    }

    // Compute the camera’s azimuth, elevation, and distance, relative to
    // the target in spherical coordinates, then convert to world Cartesian
    // coordinates and set the camera position from that.
    public void updateCameraPosition() {
        Vector3f avatarRot = avatar.getWorldForwardVector();
        double avatarAngle = Math
                .toDegrees((double) avatarRot.angleSigned(new Vector3f(0, 0, -1), new Vector3f(0, 1, 0)));
        float totalAz = cameraAzimuth - (float) avatarAngle;
        double theta = Math.toRadians(totalAz);
        double phi = Math.toRadians(cameraElevation);
        float x = cameraRadius * (float) (Math.cos(phi) * Math.sin(theta));
        float y = cameraRadius * (float) (Math.sin(phi));
        float z = cameraRadius * (float) (Math.cos(phi) * Math.cos(theta));
        camera.setLocation(new Vector3f(x, y, z).add(avatar.getWorldLocation()));
        camera.lookAt(avatar);
    }

    private class OrbitAzimuthAction extends AbstractInputAction {
        public int direction = 1;

        public OrbitAzimuthAction(int direction) {
            this.direction = direction;
        }

        public void performAction(float time, Event event) {
            float rotAmount;
            if (event.getValue() < -0.2) {
                rotAmount = -0.2f;
            } else {
                if (event.getValue() > 0.2) {
                    rotAmount = 0.2f;
                } else {
                    rotAmount = 0.0f;
                }
            }
            cameraAzimuth += rotAmount * direction;
            cameraAzimuth = cameraAzimuth % 360;
            updateCameraPosition();
        }
    }

    private class OrbitRadiusAction extends AbstractInputAction {
        public float zoomIncrement = 0.5f;

        public OrbitRadiusAction(float zoomIncrement) {
            this.zoomIncrement = zoomIncrement;
        }

        public void performAction(float time, Event event) {
            cameraRadius += zoomIncrement;
            if (cameraRadius > 3.0f) {
                cameraRadius = 3.0f;
            } else if (cameraRadius < 1.0f) {
                cameraRadius = 1.0f;
            }
        }

    }

    private class OrbitElevationAction extends AbstractInputAction {
        public float elevationIncrement = 1.0f;

        public OrbitElevationAction(float elevationIncrement) {
            this.elevationIncrement = elevationIncrement;
        }

        public void performAction(float time, Event event) {
            cameraElevation += elevationIncrement;
            if (cameraElevation > 90.0f) {
                cameraElevation = 90.0f;
            }else if(cameraElevation < -8.0f){
                cameraElevation = -8.0f;
            }
        }
    }
}
