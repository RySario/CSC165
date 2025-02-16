package myGame;

import tage.*;
import tage.shapes.*;

import java.lang.Math;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import org.joml.*;

public class MyGame extends VariableFrameRateGame {
	private static Engine engine;

	private boolean paused = false;
	private int counter = 0;
	private double lastFrameTime, currFrameTime, elapsTime;

	private GameObject dol, cub, sphere, torus, xAxis, yAxis, zAxis;
	private ObjShape dolS, cubS, sphereS, torusS, linex, liney, linez;
	private TextureImage doltx, cubetx, spheretx, torustx, warningtx, disarmtx, explodetx;
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
		linex = new Line(new Vector3f(-10f, 0f, 0f), new Vector3f(10f, 0f, 0f));
		liney = new Line(new Vector3f(0f, -10f, 0f), new Vector3f(0f, 10f, 0f));
		linez = new Line(new Vector3f(0f, 0f, -10f), new Vector3f(0f, 0f, 10f));
	}

	@Override
	public void loadTextures() {
		doltx = new TextureImage("Dolphin_HighPolyUV.png");
		cubetx = new TextureImage("Bamboo.PNG");
		spheretx = new TextureImage("scroll.png");
		torustx = new TextureImage("Iceberg.png");
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

		// build and set HUD
		int elapsTimeSec = Math.round((float) elapsTime);
		String elapsTimeStr = Integer.toString(elapsTimeSec);
		String counterStr = Integer.toString(counter);
		String dispStr1 = "Time = " + elapsTimeStr;
		String dispStr2 = "Keyboard hits = " + counterStr;
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

	@Override
	public void keyPressed(KeyEvent e) {
		Vector3f loc, fwd, up, right, newLocation, oldPosition, fwdDirection, newPosition;
		Vector4f oldUp;
		Matrix4f oldRotation, rotAroundUp, newRotation;
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
				onDol = !onDol;
				if (!onDol) {
					cam = (engine.getRenderSystem().getViewport("MAIN").getCamera());
					loc = dol.getWorldLocation();
					fwd = dol.getWorldForwardVector();
					up = dol.getWorldUpVector();
					right = dol.getWorldRightVector();
					cam.setU(right);
					cam.setV(up);
					cam.setN(fwd);
					cam.setLocation(loc.add(up.mul(0.3f)).add(fwd.mul(-1f)).add(right.mul(0.5f)));
				}

				break;
			case KeyEvent.VK_4:

				break;
		}
		super.keyPressed(e);
	}
}