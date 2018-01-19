package game.beatthereindeer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;


/**
 * Created by Eurico on 14/01/2018.
 */

public class Reindeer {

    private TextureRegion[] animation;
    private Sprite reindeer;
    private Rectangle reindeerBB;

    private boolean shoot;
    private float waitForShoot;
    private float timeToShoot;

    private int frame;

    private boolean alive;
    private float hp;
    private Sprite hpBar;

    private float direction;

    public Reindeer(Texture texture, Vector2 position) {

        alive = true;
        shoot = false;
        timeToShoot = 0f;
        waitForShoot = 0f;
        hp = 100f;

        frame = 0;
        animation = new TextureRegion[2];

        animation[0] = new TextureRegion(texture, 0, 0, 216, 250);
        animation[1] = new TextureRegion(texture, 215, 0, 153, 230);

        reindeer = new Sprite(animation[0]);

        reindeer.setPosition(position.x, position.y);
        reindeerBB = new Rectangle(position.x, position.y,  reindeer.getRegionWidth(), reindeer.getRegionHeight());

        hpBar = new Sprite(new Texture(Gdx.files.internal("controls/hpBar.png")));
        hpBar.setSize(hp + 1,4);
        hpBar.setPosition(position.x + 10,position.y + reindeer.getRegionHeight());

    }

    public void update(Vector2 playerPos, float dt) {
        shoot = false;

        if (playerPos.dst(reindeer.getX(), reindeer.getY()) < 1100f) {
            timeToShoot += dt;
            if (playerPos.x < reindeer.getX()) {
                direction = -1f;
                if (animation[0].isFlipX()) {
                    for (TextureRegion r : animation)
                        r.flip(true, false);
                }
            } else if (playerPos.x > reindeer.getX()) {
                direction = 1f;
                if (!animation[0].isFlipX()) {
                    for (TextureRegion r : animation)
                        r.flip(true, false);
                }
            }
        }
        if (timeToShoot > 0.5f) {
            frame = 1;
            waitForShoot += dt;
            if (waitForShoot > 0.8f) {
                shoot = true;
                timeToShoot = 0f;
                waitForShoot = 0f;
            }
        } else {
            frame = 0;
        }

        reindeer.setRegion(animation[frame]);

        hpBar.setSize(hp + 1,4);

    }

    public void render(SpriteBatch sb) {
        reindeer.draw(sb);
        hpBar.draw(sb);
    }

    public void dispose() {
        for (TextureRegion textureRegion : animation)
            textureRegion.getTexture().dispose();
        reindeer.getTexture().dispose();
        hpBar.getTexture().dispose();
    }

    public float getTimeToShoot() {
        return timeToShoot;
    }

    public Rectangle getReindeerBB() {
        return reindeerBB;
    }

    public boolean isAlive() {
        return alive;
    }

    public void hit() {
        hp -= 25;
        if (hp <= 0)
            alive = false;
    }

    public boolean isShoot() {
        return shoot;
    }

    public float getDirection(){return direction;}

    public Vector2 getPosition(){return new Vector2(reindeer.getX(),reindeer.getY());}
}
