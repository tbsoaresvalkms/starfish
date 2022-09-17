package com.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public class BaseActor extends Actor {
    private Animation<TextureRegion> animation;
    private float elapsedTime;
    private boolean animationPaused;
    private Vector2 velocityVec;
    private Vector2 accelerationVec;
    private float acceleration;
    private float maxSpeed;
    private float deceleration;
    private Polygon boundaryPolygon;

    public BaseActor(float x, float y, Stage stage) {
        setPosition(x, y);
        stage.addActor(this);
        this.velocityVec = new Vector2(0, 0);
        this.accelerationVec = new Vector2(0, 0);
        this.maxSpeed = 1000;
    }

    public void setAnimation(Animation<TextureRegion> animation) {
        this.animation = animation;
        TextureRegion texture = animation.getKeyFrame(0);
        int width = texture.getRegionWidth();
        int height = texture.getRegionHeight();
        setSize(width, height);
        setOrigin(width / 2.0f, height / 2.0f);
        setBoundaryRectangle();
    }

    public void setAnimationPaused(boolean animationPaused) {
        this.animationPaused = animationPaused;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (!this.animationPaused) {
            this.elapsedTime += delta;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a);
        if (animation != null && isVisible()) {
            batch.draw(animation.getKeyFrame(elapsedTime),
                    getX(),
                    getY(),
                    getOriginX(),
                    getOriginY(),
                    getWidth(),
                    getHeight(),
                    getScaleX(),
                    getScaleY(),
                    getRotation());
        }
    }

    public Animation<TextureRegion> loadAnimationFromFiles(String[] filesNames, float frameDuration, boolean loop) {
        Array<TextureRegion> textureRegions = new Array<>();

        for (String filesName : filesNames) {
            Texture texture = new Texture(Gdx.files.internal(filesName));
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            textureRegions.add(new TextureRegion(texture));
        }

        return createAnimation(frameDuration, loop, textureRegions);
    }

    public Animation<TextureRegion> loadAnimationFromSheet(String filesName, int rows, int cols, float frameDuration, boolean loop) {
        Texture texture = new Texture(Gdx.files.internal(filesName), true);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        int frameWidth = texture.getWidth() / cols;
        int frameHeight = texture.getHeight() / rows;

        TextureRegion[][] split = TextureRegion.split(texture, frameWidth, frameHeight);
        Array<TextureRegion> textureRegions = new Array<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < rows; c++) {
                textureRegions.add(split[r][c]);
            }
        }

        return createAnimation(frameDuration, loop, textureRegions);
    }

    private Animation<TextureRegion> createAnimation(float frameDuration, boolean loop, Array<TextureRegion> textureRegions) {
        Animation<TextureRegion> animation = new Animation<>(frameDuration, textureRegions);

        if (loop) {
            animation.setPlayMode(Animation.PlayMode.LOOP);
        } else {
            animation.setPlayMode(Animation.PlayMode.NORMAL);
        }

        if (this.animation == null) {
            setAnimation(animation);
        }

        return animation;
    }

    public Animation<TextureRegion> loadTexture(String fileName) {
        return loadAnimationFromFiles(new String[]{fileName}, 1, true);
    }

    public boolean isAnimationFinished() {
        return this.animation.isAnimationFinished(elapsedTime);
    }

    public void setSpeed(float speed) {
        if (velocityVec.len() == 0) {
            velocityVec.set(speed, 0);
        } else {
            velocityVec.setLength(speed);
        }
    }

    public float getSpeed() {
        return this.velocityVec.len();
    }

    public void setMotionAngle(float angle) {
        this.velocityVec.setAngleDeg(angle);
    }

    public float getMotionAngle() {
        return this.velocityVec.angleDeg();
    }

    public boolean isMoving() {
        return (getSpeed() > 0);
    }

    public void setAcceleration(float acc) {
        this.acceleration = acc;
    }

    public void accelerateAtAngle(float angle) {
        this.accelerationVec.add(new Vector2(this.acceleration, 0).setAngleDeg(angle));
    }

    public void accelerateForward() {
        accelerateAtAngle(getRotation());
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void setDeceleration(float deceleration) {
        this.deceleration = deceleration;
    }

    public void applyPhysics(float deltaTime) {
        velocityVec.add(accelerationVec.x * deltaTime, accelerationVec.y * deltaTime);
        float speed = getSpeed();
        if (accelerationVec.len() == 0) {
            speed -= deceleration * deltaTime;
        }

        speed = MathUtils.clamp(speed, 0, maxSpeed);
        setSpeed(speed);
        moveBy(velocityVec.x * deltaTime, velocityVec.y * deltaTime);
        accelerationVec.set(0, 0);
    }

    public void setBoundaryRectangle() {
        float width = getWidth();
        float height = getHeight();
        float[] vertices = {0, 0, width, 0, width, height, 0, height};
        this.boundaryPolygon = new Polygon(vertices);
    }

    public void setBoundaryPolygon(int numSides) {
        float w = getWidth();
        float h = getHeight();
        float[] vertices = new float[2 * numSides];
        for (int i = 0; i < numSides; i++) {
            float angle = i * 6.28f / numSides;
            vertices[2 * i] = w / 2 * MathUtils.cos(angle) + w / 2;
            vertices[2 * i + 1] = h / 2 * MathUtils.sin(angle) + h / 2;
        }
        this.boundaryPolygon = new Polygon(vertices);
    }

    public Polygon getBoundaryPolygon() {
        this.boundaryPolygon.setPosition(getX(), getY());
        this.boundaryPolygon.setOrigin(getOriginX(), getOriginY());
        this.boundaryPolygon.setRotation(getRotation());
        this.boundaryPolygon.setScale(getScaleX(), getScaleY());
        return this.boundaryPolygon;
    }

    public boolean overlaps(BaseActor other) {
        Polygon boundaryPolygon1 = this.getBoundaryPolygon();
        Polygon boundaryPolygon2 = other.getBoundaryPolygon();

        if (boundaryPolygon1.getBoundingRectangle().overlaps(boundaryPolygon2.getBoundingRectangle())) {
            return Intersector.overlapConvexPolygons(boundaryPolygon1, boundaryPolygon2);
        }

        return false;
    }

    public void centerAtPosition(float x, float y) {
        setPosition(x - getWidth() / 2, y - getHeight() / 2);
    }

    public void centerAtActor(BaseActor other) {
        centerAtPosition(other.getX() + other.getWidth() / 2, other.getY() + other.getHeight() / 2);
    }

    public void setOpacity(float opacity) {
        this.getColor().a = opacity;
    }
}
