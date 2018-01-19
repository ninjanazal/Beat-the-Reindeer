package game.beatthereindeer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Eurico on 11/01/2018.
 */

public class Controls {
    private Sprite leftButton, rightButton, upButton, fireButton;
    private float alphaButtons;

    private Texture[] buttonsTexture;

    private boolean isLeftPressed, isRightPressed, isUpPressed, isFirePressed;


    public Controls(Rectangle camRect) {
        buttonsTexture = new Texture[8];
        alphaButtons = 0f;
        buttonsTexture[0] = new Texture(Gdx.files.internal("controls/leftArrow.png"));
        buttonsTexture[1] = new Texture(Gdx.files.internal("controls/leftArrowPressed.png"));
        buttonsTexture[2] = new Texture(Gdx.files.internal("controls/rightArrow.png"));
        buttonsTexture[3] = new Texture(Gdx.files.internal("controls/rightArrowPressed.png"));
        buttonsTexture[4] = new Texture(Gdx.files.internal("controls/upArrow.png"));
        buttonsTexture[5] = new Texture(Gdx.files.internal("controls/upArrowPressed.png"));
        buttonsTexture[6] = new Texture(Gdx.files.internal("controls/fireArrow.png"));
        buttonsTexture[7] = new Texture(Gdx.files.internal("controls/fireArrowPressed.png"));

        leftButton = new Sprite(buttonsTexture[0]);
        leftButton.setPosition(camRect.x + 20f, camRect.y + 20f);
        leftButton.setAlpha(alphaButtons);

        rightButton = new Sprite(buttonsTexture[2]);
        rightButton.setPosition(leftButton.getX() + leftButton.getTexture().getWidth() + 50, leftButton.getY());
        rightButton.setAlpha(alphaButtons);

        upButton = new Sprite(buttonsTexture[4]);
        upButton.setPosition(camRect.x + camRect.width - upButton.getTexture().getWidth() - 20f, leftButton.getY());
        upButton.setAlpha(alphaButtons);

        fireButton = new Sprite(buttonsTexture[6]);
        fireButton.setPosition(upButton.getX() - fireButton.getTexture().getWidth() - 50f, leftButton.getY());
        fireButton.setAlpha(alphaButtons);

        isLeftPressed = false;
        isRightPressed = false;
        isUpPressed = false;
        isFirePressed = false;

    }

    public void updateInput(Vector3 pressPos) {

        if (leftButton.getBoundingRectangle().contains(pressPos.x, pressPos.y)) {
            isLeftPressed = true;
            isRightPressed = false;
        } else if (rightButton.getBoundingRectangle().contains(pressPos.x, pressPos.y)) {
            isRightPressed = true;
            isLeftPressed = false;
        }
        if (upButton.getBoundingRectangle().contains(pressPos.x, pressPos.y))
            isUpPressed = true;

        if (fireButton.getBoundingRectangle().contains(pressPos.x, pressPos.y))
            isFirePressed = true;


        if (isLeftPressed)
            leftButton.setTexture(buttonsTexture[1]);
        else leftButton.setTexture(buttonsTexture[0]);

        if (isRightPressed)
            rightButton.setTexture(buttonsTexture[3]);
        else rightButton.setTexture(buttonsTexture[2]);

        if (isUpPressed)
            upButton.setTexture(buttonsTexture[5]);
        else upButton.setTexture(buttonsTexture[4]);

        if (isFirePressed)
            fireButton.setTexture(buttonsTexture[7]);
        else fireButton.setTexture(buttonsTexture[6]);
    }

    public void render(SpriteBatch sb) {
        leftButton.setAlpha(alphaButtons);
        rightButton.setAlpha(alphaButtons);
        upButton.setAlpha(alphaButtons);
        fireButton.setAlpha(alphaButtons);

        leftButton.draw(sb);
        rightButton.draw(sb);
        upButton.draw(sb);
        fireButton.draw(sb);
    }

    public void dispose() {
        for (Texture t : buttonsTexture)
            t.dispose();
    }

    public void updateButtonsPosition(Rectangle camRect) {
        leftButton.setPosition(camRect.x + 20f, camRect.y + 20f);
        rightButton.setPosition(leftButton.getX() + leftButton.getTexture().getWidth() + 50, leftButton.getY());
        upButton.setPosition(camRect.x + camRect.width - upButton.getTexture().getWidth() - 20f, leftButton.getY());
        fireButton.setPosition(upButton.getX() - fireButton.getTexture().getWidth() - 50f, leftButton.getY());

        leftButton.getBoundingRectangle();
        rightButton.getBoundingRectangle();
        upButton.getBoundingRectangle();
        fireButton.getBoundingRectangle();

    }

    public void setAlphaButtons(float alphaButtons) {
        this.alphaButtons = alphaButtons;
    }


    public boolean isLeftPressed() {
        return isLeftPressed;
    }

    public boolean isRightPressed() {
        return isRightPressed;
    }

    public boolean isUpPressed() {
        return isUpPressed;
    }

    public boolean isFirePressed() {
        return isFirePressed;
    }

    public void resetButtons(){
        isLeftPressed = false;
        isRightPressed = false;
        isUpPressed = false;
        isFirePressed = false;

        if (isLeftPressed)
            leftButton.setTexture(buttonsTexture[1]);
        else leftButton.setTexture(buttonsTexture[0]);

        if (isRightPressed)
            rightButton.setTexture(buttonsTexture[3]);
        else rightButton.setTexture(buttonsTexture[2]);

        if (isUpPressed)
            upButton.setTexture(buttonsTexture[5]);
        else upButton.setTexture(buttonsTexture[4]);

        if (isFirePressed)
            fireButton.setTexture(buttonsTexture[7]);
        else fireButton.setTexture(buttonsTexture[6]);
    }

}
