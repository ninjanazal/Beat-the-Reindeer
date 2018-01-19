package game.beatthereindeer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Geral on 15/01/2018.
 */

public class Bullet2 {
    private Sprite bullet;
    private Rectangle bulletBC;
    private Vector2 position;
    private Vector2 direction;

    private boolean isAlive;

    private float velocity;
    private float time;

    public Bullet2(Texture texture, Vector2 direction, Vector2 startPos) {

        isAlive = true;
        time = 0;
        velocity = 18f;
        position = startPos;
        this.direction = direction;
        this.direction.nor();
        this.direction.scl(velocity);

        bullet = new Sprite(texture);
        bullet.setAlpha(0.8f);
        bullet.setPosition(position.x,position.y);
        bulletBC = new Rectangle(position.x, position.y, bullet.getWidth(), bullet.getHeight());

    }

    public void update(float dt) {
        if(time > 5f)
            isAlive = false;
        if(isAlive) {
            time += dt;
            position.add(direction);
            bullet.setPosition(position.x, position.y);
            bulletBC.setPosition(position.x,position.y);
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
