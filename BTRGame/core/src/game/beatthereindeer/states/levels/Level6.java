package game.beatthereindeer.states.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import game.beatthereindeer.Main;
import game.beatthereindeer.states.GameStateManager;
import game.beatthereindeer.states.State;

/**
 * Created by Geral on 15/01/2018.
 */

public class Level6 extends State {

    //STORIE
    private Sprite storie;
    private Vector2 storiePosition;
    private float alphaStorie;
    // botao
    private int buttonTexture;
    private Texture[] buttonTextures;
    private Sprite button;
    private Vector2 buttonPosition;
    private float alphaButton;

    // transiçao
    private boolean isShowing;
    private boolean isHiding;

    //Input
    private Vector3 pressPos;
    private boolean pressed;

    public Level6(GameStateManager gsm) {
        super(gsm);

        camera.setToOrtho(false, Main.WIDTH, Main.HEIGHT);

        // Storie
        storie = new Sprite(new Texture(Gdx.files.internal("lvl6/lvl6.png")));
        storiePosition = new Vector2(Main.WIDTH / 2 - (storie.getWidth() / 2), Main.HEIGHT / 2 - (storie.getHeight() / 2));
        alphaStorie = 0f;
        storie.setPosition(storiePosition.x, storiePosition.y);
        storie.setAlpha(alphaStorie);

        // botao
        //texturas
        buttonTexture = 0;
        buttonTextures = new Texture[2];
        buttonTextures[0] = new Texture(Gdx.files.internal("controls/rightArrow.png"));
        buttonTextures[1] = new Texture(Gdx.files.internal("controls/rightArrowPressed.png"));

        button = new Sprite(buttonTextures[buttonTexture]);
        buttonPosition = new Vector2(Main.WIDTH - button.getWidth() - 20f, 20f);
        alphaButton = 0f;
        button.setPosition(buttonPosition.x, buttonPosition.y);
        button.setAlpha(alphaButton);

        // transition
        isShowing = true;
        isHiding = false;

        // input
        pressed = false;
    }

    @Override
    public void handleInput() {
// se tocar no botao
        if (Gdx.input.isTouched()) {
            pressPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f);
            camera.unproject(pressPos);
            //continua
            if (button.getBoundingRectangle().contains(pressPos.x, pressPos.y)) {
                pressed = true;
                buttonTexture = 1;
            }
        }
    }

    @Override
    public void update(Float dt) {
        if (isShowing)
            showScreen();
        else {
            if (!pressed)
                handleInput();
            else if (!isHiding)
                hideScreen();
        }
        button.setTexture(buttonTextures[buttonTexture]);
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(camera.combined);
        sb.begin();
        storie.draw(sb);
        button.draw(sb);
        sb.end();
    }

    @Override
    public void dispose() {
        storie.getTexture().dispose();
        for (Texture texture: buttonTextures) {
            texture.dispose();
        }
    }
    // mostra ecra
    private void showScreen() {
        if (alphaStorie + 0.008f < 1f) {
            alphaStorie += 0.008f;
        } else {
            alphaStorie = 1f;
            if (alphaButton + 0.01f < 1f)
                alphaButton += 0.01f;
            else {
                alphaButton = 1f;
                isShowing = false;
            }
        }

        storie.setAlpha(alphaStorie);
        button.setAlpha(alphaButton);
    }

    // esconde ecra
    private void hideScreen() {
        if (alphaStorie - 0.01f > 0f) {
            alphaStorie -= 0.01f;
            alphaButton -= 0.01f;
        } else {
            alphaButton = 0f;
            alphaStorie = 0f;
            isHiding = true;

            gsm.setLevelCount(7);
            dispose();
            gsm.set(new Level7(gsm));

        }
        storie.setAlpha(alphaStorie);
        button.setAlpha(alphaButton);
    }
}
