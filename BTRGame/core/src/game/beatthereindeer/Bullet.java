package game.beatthereindeer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Eurico on 14/01/2018.
 */

public class Bullet {
    private Sprite bullet;
    private Rectangle bulletBC;
    private Vector2 position;

    private boolean isAlive;

    private float velocity;
    private float time;

    public Bullet(Texture texture, float direction, Vector2 startPos) {

        isAlive = true;
        time = 0;
        if (direction > 0) {
            position = new Vector2(startPos.x + 150f, startPos.y + 100f);
            velocity = 1100f;
        }
        else {
            position = new Vector2(startPos.x, startPos.y + 100f);
            velocity = -1100f;
        }
        bullet = new Sprite(texture);
        bullet.setPosition(position.x,position.y);
        bulletBC = new Rectangle(position.x, position.y, bullet.getWidth(), bullet.getHeight());

    }

    public void update(float dt) {
        if(time > 5f)
            isAlive = false;
        if(isAlive) {
            time += dt;
            position.x += velocity * dt;
            bullet.setPosition(position.x, position.y);
            bulletBC.x = position.x;
        }
    }

    public void render(SpriteBatch sb) {
        bullet.draw(sb);
    }

    public void dispose(){
        bullet.getTexture().dispose();
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public Rectangle getBulletBC() {
        return bulletBC;
    }
}
