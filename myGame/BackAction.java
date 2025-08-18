package myGame;

import tage.GameObject;
import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;
import org.joml.*;
import tage.Camera;


public class BackAction extends AbstractInputAction {
    private MyGame game;
    private Vector3f oldPosition, newPosition, fwd, loc, newLocation, fwdDirection;
    
// Moves GameObject Backwards
    public BackAction(MyGame g) {
        game = g;
    }

    @Override
    public void performAction(float time, Event e) {
        fwd = game.astro.getWorldForwardVector();
        loc = game.astro.getWorldLocation();
            newLocation = loc.add(fwd.mul(-.02f));
            game.astro.setLocalLocation(newLocation);
    }
}