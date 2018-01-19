package game.beatthereindeer.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import game.beatthereindeer.Main;

/**
 * Created by Eurico on 26/12/2017.
 */

public class IntroState extends State {
    private Sprite logoDog;
    private Sprite logoName;

    private Vector2 logoDogPosition;
    private Vector2 logoNamePosition;

    private float alpha;
    private boolean didWait, wait;
    private int counter, waitSeconds;

    public IntroState(GameStateManager gsm) {
        super(gsm);
        camera.setToOrtho(false, Main.WIDTH, Main.HEIGHT);

        logoDog = new Sprite(new Texture(Gdx.files.internal("intro/logoDog.png")));
        logoName = new Sprite(new Texture(Gdx.files.internal("intro/logoName.png")));

        logoDogPosition = new Vector2((Main.WIDTH / 2) - (logoDog.getWidth() / 2), (Main.HEIGHT / 2) - 40f);
        logoNamePosition = new Vector2((Main.WIDTH / 2) - (logoName.getWidth() / 2), (Main.HEIGHT / 2) - logoName.getHeight() - 40f);

        logoDog.setPosition(logoDogPosition.x, logoDogPosition.y);
        logoName.setPosition(logoNamePosition.x, logoNamePosition.y);

        alpha = 0f;
        didWait = false;
        wait = false;
        counter = 0;
        waitSeconds = 3;
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(Float dt) {

        // faz o logo aparecer
        if (!didWait) {
            if (alpha + 0.01 < 1.0f)
                alpha += 0.01f;
            else {
                alpha = 1f;
                wait = true;
            }
        }
        // espera 250 updates
        if (wait && !didWait)
            if (counter < 60 * waitSeconds)
                counter++;
            else
                didWait = true;

        // faz o logo desaparecer
        if (didWait) {
            if (alpha - 0.01 > 0.0f)
                alpha -= 0.01f;
            else
                alpha = 0f;
        }

        if (didWait && alpha == 0f) {
            dispose();
            gsm.set(new MenuState(gsm));

        }

        logoDog.setAlpha(alpha);
        logoName.setAlpha(alpha);

    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(camera.combined);
        sb.begin();
        logoDog.draw(sb);
        logoName.draw(sb);
        sb.end();
    }

    @Override
    public void dispose() {
        logoDog.getTexture().dispose();
        logoName.getTexture().dispose();
    }
}
