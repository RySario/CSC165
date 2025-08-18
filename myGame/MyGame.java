package myGame;

import tage.*;
import tage.shapes.*;
import java.util.ArrayList;
import java.lang.Math;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import org.joml.*;
import tage.nodeControllers.*;
import tage.input.InputManager;


public class MyGame extends VariableFrameRateGame {
	public int space;
	public InputManager im;
	public static Engine engine;
	public int score = 0;
	public String status = "SAFE";
	public float warningDist = 1.5f;
	public float detonateDist = 1.0f;
	public float disarmDist = 1.5f;

	public boolean paused = false;
	public double lastFrameTime, currFrameTime, elapsTime;

	public GameObject astro, cub, sphere, xAxis, yAxis, zAxis;
	public ArrayList<GameObject> satellites = new ArrayList<>();
	public ObjShape astroS, cubS, sphereS, linex, liney, linez;
	public TextureImage astrotx, cubetx, spheretx;
	public Light light1;
	public CameraOrbit3D orbitController;

	RotationController rc;

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
		astroS = new ImportedModel("Astronaut.obj");
		//astroS.setTexCoords(null);
		cubS = new Cube();
		sphereS = new Sphere();
		linex = new Line(new Vector3f(-10f, 0f, 0f), new Vector3f(10f, 0f, 0f));
		liney = new Line(new Vector3f(0f, -10f, 0f), new Vector3f(0f, 10f, 0f));
		linez = new Line(new Vector3f(0f, 0f, -10f), new Vector3f(0f, 0f, 10f));
	}

	@Override
	public void loadSkyBoxes(){
		space = (engine.getSceneGraph()).loadCubeMap("space");
		(engine.getSceneGraph()).setActiveSkyBoxTexture(space);
		(engine.getSceneGraph()).setSkyBoxEnabled(true);
	}

	@Override
	public void loadTextures() {
		astrotx = new TextureImage("AstronautUV.png");
		cubetx = new TextureImage("Bamboo.PNG");
		spheretx = new TextureImage("scroll.png");
	}

	@Override
	public void buildObjects() {
		Matrix4f initialTranslation, initialScale;

		// build astronaut in the center of the window
		astro = new GameObject(GameObject.root(), astroS, astrotx);
		initialTranslation = (new Matrix4f()).translation(0, 0, 0);
		initialScale = (new Matrix4f()).scaling(0.1f);
		//astro.setLocalTranslation(initialTranslation);
		astro.setLocalScale(initialScale);

		xAxis = new GameObject(GameObject.root(), linex);
		(xAxis.getRenderStates()).setColor(new Vector3f(1f, 0f, 0f));

		yAxis = new GameObject(GameObject.root(), liney);
		(yAxis.getRenderStates()).setColor(new Vector3f(0f, 1f, 0f));

		zAxis = new GameObject(GameObject.root(), linez);
		(zAxis.getRenderStates()).setColor(new Vector3f(0f, 0f, 1f));

		cub = new GameObject(GameObject.root(), cubS, cubetx);
		initialTranslation = (new Matrix4f()).translation(3, 0.5f, 0);
		initialScale = (new Matrix4f()).scaling(0.5f);
		cub.setLocalTranslation(initialTranslation);
		cub.setLocalScale(initialScale);

		sphere = new GameObject(GameObject.root(), sphereS, spheretx);
		initialTranslation = (new Matrix4f()).translation(-3, 0.5f, 3);
		initialScale = (new Matrix4f()).scaling(0.5f);
		sphere.setLocalTranslation(initialTranslation);
		sphere.setLocalScale(initialScale);

		satellites.add(cub);
		satellites.add(sphere);
	}

	@Override
	public void initializeLights() {
		Light.setGlobalAmbient(0.5f, 0.5f, 0.5f);
		light1 = new Light();
		light1.setLocation(new Vector3f(5.0f, 4.0f, 2.0f));
		(engine.getSceneGraph()).addLight(light1);
	}

	public void createViewports() {
		(engine.getRenderSystem()).addViewport("MAIN", 0, 0, 1f, 1f);
		Viewport leftVp = (engine.getRenderSystem()).getViewport("MAIN");
		Camera leftCamera = leftVp.getCamera();
		leftCamera.setLocation(new Vector3f(-2, 0, 2));
		leftCamera.setU(new Vector3f(1, 0, 0));
		leftCamera.setV(new Vector3f(0, 1, 0));
		leftCamera.setN(new Vector3f(0, 0, -1));
	}

	@Override
	public void initializeGame() {
		im = engine.getInputManager();

		// ------------- Assign Key to FwdAction -------------
		// Keyboard: W
		// Gamepad: Triangle (PS5)
		FwdAction fwdAction = new FwdAction(this);
		im.associateActionWithAllKeyboards(
				net.java.games.input.Component.Identifier.Key.W, fwdAction,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Button._3, fwdAction,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		// ------------- Assign Key to BackAction -------------
		// Keyboard: S
		// Gamepad: X (PS5)
		BackAction backAction = new BackAction(this);
		im.associateActionWithAllKeyboards(
				net.java.games.input.Component.Identifier.Key.S, backAction,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Button._1, backAction,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		// ------------- Assign Key to TurnLeftAction -------------
		// Keyboard: A
		// Gamepad: Square (PS5)
		TurnLeftAction turnLeftAction = new TurnLeftAction(this);
		im.associateActionWithAllKeyboards(
				net.java.games.input.Component.Identifier.Key.A, turnLeftAction,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Button._0, turnLeftAction,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		// ------------- Assign Key to TurnLeftAction -------------
		// Keyboard: D
		// Gamepad: Circle (PS5)
		TurnRightAction turnRightAction = new TurnRightAction(this);
		im.associateActionWithAllKeyboards(
				net.java.games.input.Component.Identifier.Key.D, turnRightAction,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Button._2, turnRightAction,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		// ------------- Initialize Orbit Controller -------------
		Camera cam;
		cam = (engine.getRenderSystem().getViewport("MAIN").getCamera());
		orbitController = new CameraOrbit3D(cam, astro, im.getKeyboardName(), im.getFirstGamepadName(), engine);

		// ------------- Initialize Overhead Controller -------------
		lastFrameTime = System.currentTimeMillis();
		currFrameTime = System.currentTimeMillis();
		elapsTime = 0.0;
		(engine.getRenderSystem()).setWindowDimensions(1900, 1000);

		// ------------- positioning the camera -------------
		(engine.getRenderSystem().getViewport("MAIN").getCamera()).setLocation(new Vector3f(0, 3, 5));

		// ------------- Initialize Rotation Controller -------------
		rc = new RotationController(engine, new Vector3f(0, 1, 0), 0.001f);
		rc.toggle();
		(engine.getSceneGraph()).addNodeController(rc);
	}

	@Override
	public void update() { // rotate astronaut if not paused
		im.update((float) elapsTime);
		orbitController.updateCameraPosition();
		lastFrameTime = currFrameTime;
		currFrameTime = System.currentTimeMillis();
		if (!paused)
			elapsTime += (currFrameTime - lastFrameTime) / 1000.0;
		// build and set HUD
		int elapsTimeSec = Math.round((float) elapsTime);
		String scoreStr = Integer.toString(score);
		String dispStr1 = "Score = " + scoreStr + "     Status = " + status;
		Vector3f hud1Color = new Vector3f(1, 0, 0);
		(engine.getHUDmanager()).setHUD1(dispStr1, hud1Color, 15, 15);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		Vector3f loc, fwd, up, right, newLocation, oldPosition, fwdDirection, newPosition, sideDirection;
		Vector4f oldUp, oldRight;
		Matrix4f oldRotation, rotAroundUp, newRotation, addedRotation;
		Camera cam;

		switch (e.getKeyCode()) {

			case KeyEvent.VK_SPACE:
				break;
		}

		super.keyPressed(e);
	}
}