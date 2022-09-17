package com.game.entities;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class Whirlpool extends BaseActor {
    public Whirlpool(float x, float y, Stage stage) {
        super(x, y, stage);

        loadAnimationFromSheet("assets/whirlpool.png", 2, 5, 0.1f, false);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (isAnimationFinished()) {
            remove();
        }
    }
}
