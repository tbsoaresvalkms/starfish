package com.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.ScreenUtils;
import com.game.entities.BaseActor;
import com.game.entities.Starfish;
import com.game.entities.Turtle;
import com.game.entities.Whirlpool;

public class StarfishGame extends Game {

    protected Stage mainStage;
    private Turtle turtle;
    private Starfish starfish;
    private BaseActor ocean;

    @Override
    public void create() {
        this.mainStage = new Stage();
        this.ocean = new BaseActor(0, 0, mainStage);
        this.ocean.loadTexture("assets/water.jpg");
        this.ocean.setSize(800, 600);

        starfish = new Starfish(380, 380, mainStage);
        turtle = new Turtle(20, 20, mainStage);
    }

    @Override
    public void render() {
        System.out.println(this.mainStage.getActors().size);
        float deltaTime = Gdx.graphics.getDeltaTime();
        this.mainStage.act(deltaTime);
        boolean resultOverlapsTurtle = this.starfish.overlapsTurtle(turtle);
        if (resultOverlapsTurtle) {
            Whirlpool whirlpool = new Whirlpool(0, 0, mainStage);
            whirlpool.centerAtActor(starfish);
            whirlpool.setOpacity(0.25f);

            BaseActor youWinMessage = new BaseActor(0, 0, mainStage);
            youWinMessage.loadTexture("assets/you-win.png");
            youWinMessage.centerAtPosition(400, 300);
            youWinMessage.setOpacity(0);
            youWinMessage.addAction(Actions.delay(1));
            youWinMessage.addAction(Actions.after(Actions.fadeIn(1)));

        }

        ScreenUtils.clear(Color.WHITE);
        mainStage.draw();
    }
}
