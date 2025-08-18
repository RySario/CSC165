package myGame;

import tage.GameObject;
import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;
import org.joml.*;
import tage.Camera;
import java.lang.Math;

public class TurnRightAction extends AbstractInputAction {
    private MyGame game;
    Matrix4f oldRotation, rotAroundUp, newRotation;
    private Vector4f oldUp;

    public TurnRightAction(MyGame g) {
        game = g;
    }

    @Override
    public void performAction(float time, Event e) {
       oldRotation = new Matrix4f(game.astro.getWorldRotation());
				oldUp = new Vector4f(0f, 1f, 0f, 1f).mul(oldRotation);
				rotAroundUp = new Matrix4f().rotation(-.005f, new Vector3f(oldUp.x(), oldUp.y(), oldUp.z()));
				newRotation = oldRotation;
				newRotation.mul(rotAroundUp);
				game.astro.setLocalRotation(newRotation);
    }
}