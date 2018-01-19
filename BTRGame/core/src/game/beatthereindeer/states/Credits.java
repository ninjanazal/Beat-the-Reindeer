package game.beatthereindeer.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import game.beatthereindeer.Main;

/**
 * Created by Eurico on 02/01/2018.
 */

public class Credits extends State {
    // Images
    // Label
    private Sprite label;
    private Vector2 labelPosition;
    private float alphaLabel;

    // person1
    private Sprite person1;
    private Vector2 person1Position;
    private float alphaPerson1;

    // person2
    private Sprite person2;
    private Vector2 person2Position;
    private float alphaPerson2;

    // person3
    private Sprite person3;
    private Vector2 person3Position;
    private float alphaPerson3;

    //transition
    private boolean isShowing;
    private boolean isSkipPressed;

    // button
    private Texture[] buttonTextures;
    private int buttonTexture;
    private Sprite button;
    private float alphaButton;
    private Vector2 buttonPosition;

    // background
    private Sprite backGround;
    private Vector2 backGroundPosition;
    private float alphaBackGround;
    private float backGroundMoveVelocity;

    //INPUT
    private Vector3 pressPoss;
    private boolean didTouched;


    public Credits(GameStateManager gsm) {
        super(gsm);
        camera.setToOrtho(false, Main.WIDTH, Main.HEIGHT);

        //Images
        //label
        label = new Sprite(new TextureRegion(new Texture(Gdx.files.internal("credits/chars.png")), 1869, 465, 570, 200));
        labelPosition = new Vector2(675f, 864f);
        alphaLabel = 0f;
        label.setPosition(labelPosition.x, labelPosition.y);
        label.setAlpha(alphaLabel);

        //person1
        person1 = new Sprite(new TextureRegion(new Texture(Gdx.files.internal("credits/chars.png")), 0, 0, 623, 665));
        person1Position = new Vector2(4f, 180f);
        alphaPerson1 = 0f;
        person1.setAlpha(alphaPerson1);
        person1.setPosition(person1Position.x, person1Position.y);

        // person2
        person2 = new Sprite(new TextureRegion(new Texture(Gdx.files.internal("credits/chars.png")), 623, 0, 623, 665));
        person2Position = new Vector2(651f, 180f);
        alphaPerson2 = 0f;
        person2.setAlpha(alphaPerson2);
        person2.setPosition(person2Position.x, person2Position.y);

        // person3
        person3 = new Sprite(new TextureRegion(new Texture(Gdx.files.internal("credits/chars.png")), 1246, 0, 623, 665));
        person3Position = new Vector2(1301f, 180f);
        alphaPerson3 = 0f;
        person3.setAlpha(alphaPerson3);
        person3.setPosition(person3Position.x, person3Position.y);

        // BackGround
        backGround = new Sprite(new Texture(Gdx.files.internal("credits/backGroundCredits.png")));
        backGroundPosition = Vector2.Zero;
        alphaBackGround = 0f;
        backGround.setAlpha(alphaBackGround);
        backGround.setPosition(backGroundPosition.x,backGroundPosition.y);
        backGroundMoveVelocity = 10.0f;

        //button
        //Texture
        buttonTexture = 0;
        buttonTextures = new Texture[2];
        buttonTextures[0] = new Texture(Gdx.files.internal("controls/rightArrow.png"));
        buttonTextures[1] = new Texture(Gdx.files.internal("controls/rightArrowPressed.png"));
        // positions, alpha
        buttonPosition = new Vector2(1771, 25);
        alphaButton = 0f;
        button = new Sprite(buttonTextures[buttonTexture]);
        button.setPosition(buttonPosition.x, buttonPosition.y);
        button.setAlpha(alphaButton);

        // transition
        isShowing = true;
        isSkipPressed = false;

    }

    @Override
    public void handleInput() {
        if (Gdx.input.isTouched()) {
            pressPoss = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f);
            camera.unproject(pressPoss);
            if (button.getBoundingRectangle().contains(pressPoss.x, pressPoss.y)) {
                isSkipPressed = true;
                buttonTexture = 1;
            } else buttonTexture = 0;


            didTouched = true;
        } else {
            didTouched = false;
        }

    }

    @Override
    public void update(Float dt) {
        // aparece ecra
        if (isShowing)
            showScreen();
        if (!isShowing && !isSkipPressed) {
            handleInput();

        } else if (isSkipPressed)
            hideScreen();

        // move
        moveBackground();
        backGroundPosition.x += backGroundMoveVelocity * dt;
        backGround.setX(backGroundPosition.x);

        // alpha
        button.setTexture(buttonTextures[buttonTexture]);
        button.setAlpha(alphaButton);
        label.setAlpha(alphaLabel);
        person1.setAlpha(alphaPerson1);
        person2.setAlpha(alphaPerson2);
        person3.setAlpha(alphaPerson3);
        backGround.setAlpha(alphaBackGround);


    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(camera.combined);
        sb.begin();
        backGround.draw(sb);
        button.draw(sb);
        label.draw(sb);
        person1.draw(sb);
        person2.draw(sb);
        person3.draw(sb);
        sb.end();
    }

    @Override
    public void dispose() {
        for (Texture texture : buttonTextures) {
            texture.dispose();
        }
        person1.getTexture().dispose();
        button.getTexture().dispose();
        label.getTexture().dispose();
        person2.getTexture().dispose();
        person3.getTexture().dispose();
    }

    private void showScreen() {
        if(alphaBackGround + 0.01f<1f)
            alphaBackGround +=0.01f;
        else
            alphaBackGround = 1f;

        if(alphaBackGround > 0.3f) {
            if (alphaButton + 0.01f < 1f)
                alphaButton += 0.01f;
            else
                alphaButton = 1f;

            if (alphaButton > 0.3f) {
                if (alphaLabel + 0.01f < 1f)
                    alphaLabel += 0.01f;
                else
                    alphaLabel = 1f;
                if (alphaLabel > 0.6f) {
                    if (alphaPerson1 + 0.01f < 1f)
                        alphaPerson1 += 0.01f;
                    else
                        alphaPerson1 = 1f;
                    if (alphaPerson1 > 0.6f) {
                        if (alphaPerson2 + 0.01f < 1f)
                            alphaPerson2 += 0.01f;
                        else
                            alphaPerson2 = 1f;
                        if (alphaPerson2 > 0.6f) {
                            if (alphaPerson3 + 0.01f < 1f)
                                alphaPerson3 += 0.01f;
                            else {
                                alphaPerson3 = 1f;
                                isShowing = false;
                            }
                        }
                    }
                }
            }
        }


    }

    private void hideScreen() {
        if (alphaButton - 0.01f > 0f) {
            alphaButton -= 0.01f;
            alphaPerson1 -= 0.01f;
            alphaLabel -= 0.01f;
            alphaPerson2 -= 0.01f;
            alphaPerson3 -= 0.01f;
            alphaBackGround -= 0.01f;
        } else {
            alphaButton = 0f;
            alphaPerson1 = 0f;
            alphaLabel = 0f;
            alphaPerson2 = 0f;
            alphaPerson3 = 0f;
            alphaBackGround = 0f;

            dispose();
            gsm.set(new MenuState(gsm));
        }
    }

    private void moveBackground(){
        if(backGroundPosition.x >= 0 && backGroundMoveVelocity > 0)
            backGroundMoveVelocity *= -1f;
        else if(backGroundPosition.x <= -30 && backGroundMoveVelocity <0)
            backGroundMoveVelocity *= -1f;
    }
}
