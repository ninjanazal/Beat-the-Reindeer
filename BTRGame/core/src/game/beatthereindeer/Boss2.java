package game.beatthereindeer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Geral on 15/01/2018.
 */

public class Boss2 {
    //ANIMATION
    private int frame;
    private TextureRegion[] animation;
    private Sprite monster;
    private Rectangle monsterBB;
    private float velocity;

    //SHOOTING
    private boolean shoot;
    private float timeToShoot;
    private float shootingTime;
    private float chargeTime;
    private float gap;

    //HP
    private float hp;
    private Sprite hpBar;
    private boolean alive;


    public Boss2() {
        //ANIMATION
        frame = 0;
        animation = new TextureRegion[3];
        animation[0] = new TextureRegion(new Texture(Gdx.files.internal("lvl5/boss2.png")), 0, 0, 195, 307);
        animation[1] = new TextureRegion(new Texture(Gdx.files.internal("lvl5/boss2.png")), animation[0].getRegionWidth(), 0, 195, 307);
        animation[2] = new TextureRegion(new Texture(Gdx.files.internal("lvl5/boss2.png")), 2 * animation[0].getRegionWidth(), 0, 195, 307);

        monster = new Sprite(animation[frame]);
        monster.setSize(518, 816);
        monster.setPosition(1277, 118);

        monsterBB = new Rectangle(monster.getX(), monster.getY(), monster.getWidth(), monster.getHeight());

        velocity = 25f;

        //HP
        alive = true;
        hp = 100;

        hpBar = new Sprite(new Texture(Gdx.files.internal("controls/hpBar.png")));
        hpBar.setSize(((hp * 416) / 100), 10);
        hpBar.setPosition(1373, 989);

        //TIMERS
        shoot = false;
        timeToShoot = 2f;
        shootingTime = 1f;
        chargeTime = 2f;
        gap = 2f;
    }

    public void update(float dt) {

        if (alive) {
            hpBar.setSize(((hp * 416) / 100) + 10, 10);

            shoot = false;
            timeToShoot -= dt;

            if (timeToShoot <= 0) {
                frame = 1;
                chargeTime -= dt;
                if (chargeTime <= 0) {
                    frame = 2;
                    shoot = true;
                    if (gap != 0) {
                        chargeTime = 1.1f;
                        gap -= 1;
                    }
                    else
                        shootingTime -= 1;
                    if (shootingTime <= 0) {
                        timeToShoot = 3f;
                        shootingTime = 2f;
                        chargeTime = 2f;
                        frame = 0;
                        gap = 2f;
                    }
                }
            }

            moveMonster(dt);
            monster.setRegion(animation[frame]);
            monsterBB.set(monster.getX(), monster.getY(), monster.getWidth(), monster.getHeight());

        }

    }

    public void render(SpriteBatch sb) {
        if (alive) {
            monster.draw(sb);
            hpBar.draw(sb);
        }
    }

    public void dispose() {
        for (TextureRegion tr : animation)
            tr.getTexture().dispose();

        hpBar.getTexture().dispose();
        monster.getTexture().dispose();
    }

    // FUNC
    public void hit() {
        if (hp > 0)
            hp -= 1f;
        else {
            hp = 0;
            alive = false;
        }
    }

    public Rectangle getMonsterBB() {
        return monsterBB;
    }

    public boolean isShoot() {
        return shoot;
    }

    public float getHp() {
        return hp;
    }

    public boolean isAlive() {
        return alive;
    }

    public Vector2 getCenter() {
        return new Vector2(monster.getX() + (monster.getHeight() / 2), monster.getY() + (monster.getWidth() / 2));
    }

    //MOVE
    private void moveMonster(float dt) {
        if (monster.getY() <= 118 && velocity < 0)
            velocity *= -1;
        else if (monster.getY() >= 260 && velocity > 0)
            velocity *= -1;

        monster.setY(monster.getY() + velocity * dt);
    }
}
