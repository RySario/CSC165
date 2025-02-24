package myGame;

import tage.*;
import tage.rml.Degreef;
import tage.shapes.*;
import java.util.ArrayList;
import java.lang.Math;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import org.joml.*;

public class MyGame extends VariableFrameRateGame {
	private static Engine engine;
	private int score = 0;
	private String status = "SAFE";
	private float warningDist = 2.0f;
	private float detonateDist = 1.5f;
	private float disarmDist = 1.0f;

	private boolean paused = false;
	private double lastFrameTime, currFrameTime, elapsTime;

	private GameObject dol, cub, sphere, torus, tent, xAxis, yAxis, zAxis;
	private ArrayList<GameObject> satellites = new ArrayList<>();
	private ObjShape dolS, cubS, sphereS, torusS, manTent, linex, liney, linez;
	private TextureImage doltx, cubetx, spheretx, torustx, tenttx, warningtx, defusetx, explodetx;
	private Light light1;
	private boolean onDol;

	public MyGame() {
		super();
	}

	public static void main(String[] args) {
		MyGame game = new MyGame();
		engine = new Engine(game);
		game.initializeSystem();
		game.game_loop();
	}

	@Override
	public void loadShapes() {
		dolS = new ImportedModel("dolphinHighPoly.obj");
		cubS = new Cube();
		sphereS = new Sphere();
		torusS = new Torus();
		manTent = new manualTent();
		linex = new Line(new Vector3f(-10f, 0f, 0f), new Vector3f(10f, 0f, 0f));
		liney = new Line(new Vector3f(0f, -10f, 0f), new Vector3f(0f, 10f, 0f));
		linez = new Line(new Vector3f(0f, 0f, -10f), new Vector3f(0f, 0f, 10f));
	}

	@Override
	public void loadTextures() {
		doltx = new TextureImage("Dolphin_HighPolyUV.png");
		cubetx = new TextureImage("Bamboo.PNG");
		tenttx = new TextureImage("LightningSky.PNG");
		spheretx = new TextureImage("scroll.png");
		torustx = new TextureImage("Iceberg.png");
		tenttx = new TextureImage("LightningSky.PNG");
		explodetx = new TextureImage("explode.png");
		defusetx = new TextureImage("defuse.png");
		warningtx =  new TextureImage("warning.png");
	}

	@Override
	public void buildObjects() {
		Matrix4f initialTranslation, initialScale;

		// build dolphin in the center of the window
		dol = new GameObject(GameObject.root(), dolS, doltx);
		initialTranslation = (new Matrix4f()).translation(0, 0, 0);
		initialScale = (new Matrix4f()).scaling(1.0f);
		dol.setLocalTranslation(initialTranslation);
		dol.setLocalScale(initialScale);
		dol.setLocalRotation((new Matrix4f()).rotation(250f, 0, 1, 0));

		xAxis = new GameObject(GameObject.root(), linex);
		(xAxis.getRenderStates()).setColor(new Vector3f(1f, 0f, 0f));

		yAxis = new GameObject(GameObject.root(), liney);
		(yAxis.getRenderStates()).setColor(new Vector3f(0f, 1f, 0f));

		zAxis = new GameObject(GameObject.root(), linez);
		(zAxis.getRenderStates()).setColor(new Vector3f(0f, 0f, 1f));

		cub = new GameObject(GameObject.root(), cubS, cubetx);
		initialTranslation = (new Matrix4f()).translation(3, 0, 0);
		initialScale = (new Matrix4f()).scaling(0.5f);
		cub.setLocalTranslation(initialTranslation);
		cub.setLocalScale(initialScale);

		sphere = new GameObject(GameObject.root(), sphereS, spheretx);
		initialTranslation = (new Matrix4f()).translation(0, 3, 0);
		initialScale = (new Matrix4f()).scaling(0.5f);
		sphere.setLocalTranslation(initialTranslation);
		sphere.setLocalScale(initialScale);

		torus = new GameObject(GameObject.root(), torusS, torustx);
		initialTranslation = (new Matrix4f()).translation(-3, 0, 0);
		initialScale = (new Matrix4f()).scaling(0.5f);
		torus.setLocalTranslation(initialTranslation);
		torus.setLocalScale(initialScale);

		tent = new GameObject(GameObject.root(), manTent, tenttx);
		initialTranslation = (new Matrix4f()).translation(-3, 0, -3);
		initialScale = (new Matrix4f()).scaling(0.5f);
		tent.setLocalTranslation(initialTranslation);
		tent.setLocalScale(initialScale);

		satellites.add(cub);
		satellites.add(sphere);
		satellites.add(torus);
		satellites.add(tent);
	}

	@Override
	public void initializeLights() {
		Light.setGlobalAmbient(0.5f, 0.5f, 0.5f);
		light1 = new Light();
		light1.setLocation(new Vector3f(5.0f, 4.0f, 2.0f));
		(engine.getSceneGraph()).addLight(light1);
	}

	@Override
	public void initializeGame() {
		lastFrameTime = System.currentTimeMillis();
		currFrameTime = System.currentTimeMillis();
		onDol = true;
		elapsTime = 0.0;
		(engine.getRenderSystem()).setWindowDimensions(1900, 1000);

		// ------------- positioning the camera -------------
		(engine.getRenderSystem().getViewport("MAIN").getCamera()).setLocation(new Vector3f(0, 0, 5));
	}

	@Override
	public void update() { // rotate dolphin if not paused
		lastFrameTime = currFrameTime;
		currFrameTime = System.currentTimeMillis();
		if (!paused)
			elapsTime += (currFrameTime - lastFrameTime) / 1000.0;
		checkStatus();
		// build and set HUD
		int elapsTimeSec = Math.round((float) elapsTime);
		String scoreStr = Integer.toString(score);
		String dispStr1 = "Score = " + scoreStr;
		String dispStr2 = "Status = " + status;
		Vector3f hud1Color = new Vector3f(1, 0, 0);
		Vector3f hud2Color = new Vector3f(0, 0, 1);
		(engine.getHUDmanager()).setHUD1(dispStr1, hud1Color, 15, 15);
		(engine.getHUDmanager()).setHUD2(dispStr2, hud2Color, 500, 15);

		Vector3f loc, fwd, up, right, newLocation;
		Camera cam;
		cam = (engine.getRenderSystem().getViewport("MAIN").getCamera());
		loc = dol.getWorldLocation();
		fwd = dol.getWorldForwardVector();
		up = dol.getWorldUpVector();
		right = dol.getWorldRightVector();
		cam.setU(right);
		cam.setV(up);
		cam.setN(fwd);
		if (onDol) {
			cam.setLocation(loc.add(up.mul(0.5f)).add(fwd.mul(-1f)));
		}

	}

	private void checkStatus() {

		// WARNING: GETTING TOO CLOSE (dolphin within .5 units of satellite)
		// Detonate! You Lose! (dolphin within .25 units of satellite)
		// Disarmed! Good Job! (cam within .25 units of satellite) (dolphin getting
		// close has no effect)

		// Change texture in this function for detonation!!
		GameObject detonated = null;
		if (onDol) {
			for (int i = 0; i < satellites.size(); i++) {
				GameObject satellite = satellites.get(i);
				float satelliteX = satellite.getLocalLocation().x();
				float satelliteY = satellite.getLocalLocation().y();
				float satelliteZ = satellite.getLocalLocation().z();
				float dolX = dol.getLocalLocation().x();
				float dolY = dol.getLocalLocation().y();
				float dolZ = dol.getLocalLocation().z();
				float distance = (float) Math.sqrt(Math.pow(satelliteX - dolX, 2) + Math.pow(satelliteY - dolY, 2) + Math.pow(satelliteZ - dolZ, 2));
				if (distance <= detonateDist) {
					satellite.setTextureImage(explodetx);
					status = "Detonate! You Lose!";
					detonated = satellites.get(i);
					break;
				} else if (distance <= warningDist) {
					satellite.setTextureImage(warningtx);
					status = "WARNING: GETTING TOO CLOSE";
					break;
				} 
			}
		} else {
			Camera cam;
			cam = (engine.getRenderSystem().getViewport("MAIN").getCamera());
			for (int i = 0; i < satellites.size(); i++) {
				GameObject satellite = satellites.get(i);
				float satelliteX = satellite.getLocalLocation().x();
				float satelliteY = satellite.getLocalLocation().y();
				float satelliteZ = satellite.getLocalLocation().z();
				float camX = cam.getLocation().x();
				float camY = cam.getLocation().y();
				float camZ = cam.getLocation().z();
				float distance = (float) Math.sqrt(Math.pow(satelliteX - camX, 2) + Math.pow(satelliteY - camY, 2)
						+ Math.pow(satelliteZ - camZ, 2));
				if (distance <= disarmDist) {
					satellite.setTextureImage(defusetx);
					status = "Disarmed, Good Job!";
					score += 1;
					detonated = satellites.get(i);
					break;

				} else if (distance <= warningDist) {
					status = "Not Close Enough!";
				}
			}
		}
		if (detonated != null) {
			satellites.remove(detonated);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		Vector3f loc, fwd, up, right, newLocation, oldPosition, fwdDirection, newPosition, sideDirection;
		Vector4f oldUp, oldRight;
		Matrix4f oldRotation, rotAroundUp, newRotation, addedRotation;
		Camera cam;

		switch (e.getKeyCode()) {
			case KeyEvent.VK_W:
				fwd = dol.getWorldForwardVector();
				loc = dol.getWorldLocation();
				if (onDol) {
					newLocation = loc.add(fwd.mul(.02f));
					dol.setLocalLocation(newLocation);
				} else {
					cam = (engine.getRenderSystem().getViewport("MAIN").getCamera());
					oldPosition = cam.getLocation();
					fwdDirection = cam.getN();
					fwdDirection.mul(0.01f);
					newPosition = oldPosition.add(fwdDirection.x(),
							fwdDirection.y(), fwdDirection.z());
					cam.setLocation(newPosition);
				}
				break;

			case KeyEvent.VK_S:
				fwd = dol.getWorldForwardVector();
				loc = dol.getWorldLocation();
				if (onDol) {
					newLocation = loc.add(fwd.mul(-.02f));
					dol.setLocalLocation(newLocation);
				} else {
					cam = (engine.getRenderSystem().getViewport("MAIN").getCamera());
					oldPosition = cam.getLocation();
					fwdDirection = cam.getN();
					fwdDirection.mul(-0.01f);
					newPosition = oldPosition.add(fwdDirection.x(),
							fwdDirection.y(), fwdDirection.z());
					cam.setLocation(newPosition);
				}
				break;

			case KeyEvent.VK_A:
					oldRotation = new Matrix4f(dol.getWorldRotation());
					oldUp = new Vector4f(0f, 1f, 0f, 1f).mul(oldRotation);
					rotAroundUp = new Matrix4f().rotation(.005f, new Vector3f(oldUp.x(), oldUp.y(), oldUp.z()));
					newRotation = oldRotation;
					newRotation.mul(rotAroundUp);
					dol.setLocalRotation(newRotation);
				break;

			case KeyEvent.VK_D:
					oldRotation = new Matrix4f(dol.getWorldRotation());
					oldUp = new Vector4f(0f, 1f, 0f, 1f).mul(oldRotation);
					rotAroundUp = new Matrix4f().rotation(-.005f, new Vector3f(oldUp.x(), oldUp.y(), oldUp.z()));
					newRotation = oldRotation;
					newRotation.mul(rotAroundUp);
					dol.setLocalRotation(newRotation);
				break;

			case KeyEvent.VK_SPACE:

				break;
			case KeyEvent.VK_UP:
				if (onDol) {
					oldRotation = new Matrix4f(dol.getWorldRotation());
					oldRight = new Vector4f(1f, 0f, 0f, 1f).mul(oldRotation);
					addedRotation = (new Matrix4f()).rotation(-0.01f,
							new Vector3f(oldRight.x(), oldRight.y(), oldRight.z()));
					newRotation = addedRotation.mul(oldRotation);
					dol.setLocalRotation(newRotation);
				} else {

				}
				break;

			case KeyEvent.VK_DOWN:
				if (onDol) {
					oldRotation = new Matrix4f(dol.getWorldRotation());
					oldRight = new Vector4f(1f, 0f, 0f, 1f).mul(oldRotation);
					addedRotation = (new Matrix4f()).rotation(0.01f,
							new Vector3f(oldRight.x(), oldRight.y(), oldRight.z()));
					newRotation = addedRotation.mul(oldRotation);
					dol.setLocalRotation(newRotation);
				} else {

				}

				break;
		}
		super.keyPressed(e);
	}
}