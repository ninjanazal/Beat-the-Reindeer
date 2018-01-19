package game.beatthereindeer.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import game.beatthereindeer.Main;
import game.beatthereindeer.states.levels.Level0;
import game.beatthereindeer.states.levels.Level1;
import game.beatthereindeer.states.levels.Level2;
import game.beatthereindeer.states.levels.Level3;
import game.beatthereindeer.states.levels.Level4;
import game.beatthereindeer.states.levels.Level5;
import game.beatthereindeer.states.levels.Level6;
import game.beatthereindeer.states.levels.Level7;
import game.beatthereindeer.states.levels.Level8;
import game.beatthereindeer.states.levels.Level9;

/**
 * Created by Eurico on 26/12/2017.
 */

public class MenuState extends State {

    private float backGroundVelocity;
    private Vector2 backGroundPosition;
    private Sprite backGroundSprite;
    private float alphaBackground;

    private boolean showScreen;

    // fundo menu
    private Sprite backMenuSprite;
    private Vector2 backMenuPosition;
    private float alphaBackMenu;

    // Start
    private Sprite buttonStartSprite;
    private Vector2 buttonStartPosition;
    private float alphaButtonStart;
    private boolean isStartPressed;

    // Credits
    private Sprite buttonCreditsSprite;
    private Vector2 buttonCreditsPosition;
    private float alphaButtonCredits;
    private boolean isCreditsPressed;

    // gameLogo
    private Sprite gameLogoSprite;
    private Vector2 gameLogoPosition;
    private float alphaGameLogo;


    // INPUT
    private Vector3 pressPos;

    // Counter
    private float timeCounter;

    public MenuState(GameStateManager gsm) {
        super(gsm);
        // define a camera
        camera.setToOrtho(false, Main.WIDTH, Main.HEIGHT);

        // imagem de fundo
        backGroundSprite = new Sprite(new Texture(Gdx.files.internal("menu/menuScreen.png")));
        backGroundPosition = new Vector2(0f, 0f);
        backGroundVelocity = 15.0f;

        // define a posiçao do fundo
        backGroundSprite.setPosition(backGroundPosition.x, backGroundPosition.y);


        //--------- Menu -----------
        // buttao Start
        buttonStartSprite = new Sprite(new Texture(Gdx.files.internal("menu/buttonStart.png")));
        buttonStartPosition = new Vector2(333f, 488f);
        buttonStartSprite.setPosition(buttonStartPosition.x, buttonStartPosition.y);
        isStartPressed = false;

        //butao Credits
        buttonCreditsSprite = new Sprite(new Texture(Gdx.files.internal("menu/buttonCredits.png")));
        buttonCreditsPosition = new Vector2(333f, 342f);
        buttonCreditsSprite.setPosition(buttonCreditsPosition.x, buttonCreditsPosition.y);
        isCreditsPressed = false;

        //fundo Menu
        backMenuSprite = new Sprite(new Texture(Gdx.files.internal("menu/backMenu.png")));
        backMenuPosition = new Vector2(290f, 240f);
        backMenuSprite.setPosition(backMenuPosition.x, backMenuPosition.y);

        // logo
        gameLogoSprite = new Sprite(new Texture(Gdx.files.internal("menu/gameLogo.png")));
        gameLogoPosition = new Vector2(270f, 721f);
        gameLogoSprite.setPosition(gameLogoPosition.x, gameLogoPosition.y);

        // transiçao
        alphaBackground = 0f;
        alphaButtonStart = 0f;
        alphaButtonCredits = 0f;
        alphaBackMenu = 0f;
        alphaGameLogo = 0f;

        // alfa inicial para as imagens
        backGroundSprite.setAlpha(alphaBackground);
        buttonStartSprite.setAlpha(alphaButtonStart);
        buttonCreditsSprite.setAlpha(alphaButtonCredits);
        backMenuSprite.setAlpha(alphaBackMenu);
        gameLogoSprite.setAlpha(alphaGameLogo);

        // mostrar ecra de inicio
        showScreen = true;

        // counter
        timeCounter = 0f;
    }

    @Override
    public void handleInput() {
        if (Gdx.input.isTouched()) {
            pressPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f);
            camera.unproject(pressPos);
            //start
            if (buttonStartSprite.getBoundingRectangle().contains(pressPos.x, pressPos.y))
                isStartPressed = true;
            else
                isStartPressed = false;
            if (buttonCreditsSprite.getBoundingRectangle().contains(pressPos.x, pressPos.y))
                isCreditsPressed = true;
            else
                isCreditsPressed = false;
        } else {
            isStartPressed = false;
            isCreditsPressed = false;
        }
    }

    @Override
    public void update(Float dt) {
        // mostra o ecra
        if (showScreen)
            showScreen();

        // INPUT
        if (!showScreen && !isStartPressed && !isCreditsPressed)
            handleInput();

        if (isStartPressed) {
            buttonStartSprite.getTexture().dispose();
            buttonStartSprite.setTexture(new Texture(Gdx.files.internal("menu/buttonStartPressed.png")));
        } else {
            buttonStartSprite.getTexture().dispose();
            buttonStartSprite.setTexture(new Texture(Gdx.files.internal("menu/buttonStart.png")));
        }
        if (isCreditsPressed) {
            buttonCreditsSprite.getTexture().dispose();
            buttonCreditsSprite.setTexture(new Texture(Gdx.files.internal("menu/buttonCreditsPressed.png")));
        } else {
            buttonCreditsSprite.getTexture().dispose();
            buttonCreditsSprite.setTexture(new Texture(Gdx.files.internal("menu/buttonCredits.png")));
        }
        //move o fundo
        moveBackGround();
        backGroundPosition.x += backGroundVelocity * dt;
        backGroundSprite.setPosition(backGroundPosition.x, backGroundPosition.y);

        if (isStartPressed || isCreditsPressed)
            hideScreen(dt);

        // define novos alfas
        backGroundSprite.setAlpha(alphaBackground);
        buttonStartSprite.setAlpha(alphaButtonStart);
        buttonCreditsSprite.setAlpha(alphaButtonCredits);
        backMenuSprite.setAlpha(alphaBackMenu);
        gameLogoSprite.setAlpha(alphaGameLogo);

    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(camera.combined);
        sb.begin();

        // fundo
        backGroundSprite.draw(sb);
        // fundoMenu
        backMenuSprite.draw(sb);
        //botoes
        buttonStartSprite.draw(sb);
        buttonCreditsSprite.draw(sb);
        // logo
        gameLogoSprite.draw(sb);
        sb.end();
    }

    @Override
    public void dispose() {
        backGroundSprite.getTexture().dispose();
        buttonStartSprite.getTexture().dispose();
        buttonCreditsSprite.getTexture().dispose();
        backMenuSprite.getTexture().dispose();
        gameLogoSprite.getTexture().dispose();
    }

    private void showScreen() {
        // faz aparecer o menu
        //aparece o fundo
        if (alphaBackground + 0.01f < 1.0f)
            alphaBackground += 0.01f;
        else
            alphaBackground = 1f;

        // aparece o menu
        if (alphaBackground > 0.3f) {
            if (alphaBackMenu + 0.01f < 1.0f)
                alphaBackMenu += 0.01f;
            else
                alphaBackMenu = 1f;

            if (alphaBackMenu > 0.3f) {
                if (alphaButtonStart + 0.01f < 1.0f) {
                    alphaButtonStart += 0.01f;
                    alphaButtonCredits += 0.01f;
                    alphaGameLogo += 0.01f;

                } else {
                    alphaButtonStart = 1f;
                    alphaButtonCredits = 1f;
                    showScreen = false;
                }
            }
        }
    }

    private void hideScreen(float dt) {
        // faz desaparecer o ecra caso a escolha seja start
        if (isStartPressed) {
            if (alphaButtonCredits - 0.01f > 0f) {
                alphaButtonCredits -= 0.01f;
                alphaBackMenu -= 0.01f;
                alphaBackground -= 0.01f;
            } else {
                alphaBackground = 0f;
                alphaButtonCredits = 0f;
                alphaBackMenu = 0f;
                if (timeCounter >= 500.0f)
                    timeCounter += dt;
                else {
                    if (gsm.getLevelCount() == 0) {
                        dispose();
                        gsm.set(new Level0(gsm));
                    } else if (gsm.getLevelCount() == 1) {
                        dispose();
                        gsm.set(new Level1(gsm));
                    } else if (gsm.getLevelCount() == 2) {
                        dispose();
                        gsm.set(new Level2(gsm));
                    } else if (gsm.getLevelCount() == 3) {
                        dispose();
                        gsm.set(new Level3(gsm));
                    } else if (gsm.getLevelCount() == 4) {
                        dispose();
                        gsm.set(new Level4(gsm));
                    } else if (gsm.getLevelCount() == 5) {
                        dispose();
                        gsm.set(new Level5(gsm));
                    } else if (gsm.getLevelCount() == 6) {
                        dispose();
                        gsm.set(new Level6(gsm));
                    } else if (gsm.getLevelCount() == 7) {
                        dispose();
                        gsm.set(new Level7(gsm));
                    } else if (gsm.getLevelCount() == 8) {
                        dispose();
                        gsm.set(new Level8(gsm));
                    } else if (gsm.getLevelCount() == 9) {
                        dispose();
                        gsm.set(new Level9(gsm));
                    }
                }
            }
            // caso a escolha seja credits
        } else if (isCreditsPressed) {
            if (alphaButtonStart - 0.01f > 0f) {
                alphaButtonStart -= 0.01f;
                alphaBackMenu -= 0.01f;
                alphaBackground -= 0.01f;
            } else {
                alphaBackground = 0f;
                alphaButtonStart = 0f;
                alphaBackMenu = 0f;
                if (timeCounter >= 500.0f)
                    timeCounter += dt;
                else {
                    dispose();
                    gsm.set(new Credits(gsm));

                }
            }
        }
    }

    private void moveBackGround() {
        // move o fundo
        if (backGroundPosition.x >= 0 && backGroundVelocity > 0f)
            backGroundVelocity *= -1f;
        else if (backGroundPosition.x <= -267f && backGroundVelocity < 0f)
            backGroundVelocity *= -1f;
    }
}
