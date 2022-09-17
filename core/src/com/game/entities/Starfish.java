package com.game.entities;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class Starfish extends BaseActor {
    private boolean collected;

    public Starfish(float x, float y, Stage stage) {
        super(x, y, stage);
        loadTexture("assets/starfish.png");

        Action spin = Actions.rotateBy(30, 1);
        this.addAction(Actions.forever(spin));
        setBoundaryPolygon(8);
    }

    public boolean overlapsTurtle(Turtle turtle) {
        if (this.overlaps(turtle) && !isCollected()) {
            this.collected();
            clearActions();
            this.addAction(Actions.fadeOut(1));
            this.addAction(Actions.after(Actions.removeActor()));
            return true;
        }

        return false;
    }

    public boolean isCollected() {
        return collected;
    }

    public void collected() {
        this.collected = true;
    }
}
