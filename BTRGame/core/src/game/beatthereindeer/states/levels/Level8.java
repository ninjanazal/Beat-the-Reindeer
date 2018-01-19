package game.beatthereindeer.states.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import game.beatthereindeer.Boss2;
import game.beatthereindeer.Boss3;
import game.beatthereindeer.Bullet;
import game.beatthereindeer.Bullet2;
import game.beatthereindeer.Controls;
import game.beatthereindeer.Main;
import game.beatthereindeer.Player;
import game.beatthereindeer.states.Credits;
import game.beatthereindeer.states.GameStateManager;
import game.beatthereindeer.states.MenuState;
import game.beatthereindeer.states.State;

/**
 * Created by Geral on 15/01/2018.
 */

public class Level8 extends State {

    //player
    private Player player;
    private List<Bullet> bullets;
    private Texture bulletTexture;
    private Vector2 lastPosition;
    private Vector2 lastPosition2, lastPosition3, lastPosition4, lastPosition5;

    // monsters
    private Boss3 boss;
    private List<Bullet2> bossBullets;
    private Texture bossBulletTexture;


    // Buttons
    private Controls controls;
    private Vector3 pressPoss;

    // Map
    private Sprite mapSprite;
    private float alphaMap;
    private Rectangle[] mapBB;

    // transitions
    private boolean isShowing;
    private boolean isPlaying;

    // camera REct
    private Rectangle cameraRect;

    //DEBUG
    ShapeRenderer shapeRenderer;

    public Level8(GameStateManager gsm) {
        super(gsm);

        //MAP
        mapSprite = new Sprite(new Texture(Gdx.files.internal("lvl8/lvl8Map.png")));
        mapSprite.setSize(1920f, 1080f);
        mapSprite.setPosition(0f, 0f);
        createMapBB();

        //Player
        player = new Player(mapBB, new Vector2(200f, 1200f));
        bullets = new ArrayList<Bullet>();
        bulletTexture = new Texture(Gdx.files.internal("player/bullet.png"));

        //MONSTER
        boss = new Boss3();
        bossBullets = new ArrayList<Bullet2>();
        bossBulletTexture = new Texture(Gdx.files.internal("lvl5/bullet2.png"));

        //camara position
        camera.setToOrtho(false, Main.WIDTH, Main.HEIGHT);
        camera.position.set(Main.WIDTH / 2, Main.HEIGHT / 2, 0f);
        camera.update();

        cameraRect = new Rectangle(camera.position.x - (Main.WIDTH / 2), camera.position.y - (Main.HEIGHT / 2), Main.WIDTH, Main.HEIGHT);


        //CAMERA
        pressPoss = new Vector3().setZero();
        // transitions
        isShowing = true;
        isPlaying = false;

        // Buttons
        controls = new Controls(cameraRect);

        //DEBUG
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void handleInput() {
        pressPoss.setZero();
        controls.resetButtons();

        for (int i = 0; i < 3; i++) {
            if (Gdx.input.isTouched(i)) {
                camera.unproject(pressPoss.set(Gdx.input.getX(i), Gdx.input.getY(i), 0f));
                controls.updateInput(pressPoss);
            }
        }
    }

    @Override
    public void update(Float dt) {
        if (isShowing)
            showScreen();


        if (isPlaying && (player.getHp() != 0 || boss.getHp() != 0)) {
            handleInput();

            if (controls.isLeftPressed() && pressPoss != Vector3.Zero)
                player.walkLeft(dt);
            if (controls.isRightPressed() && pressPoss != Vector3.Zero)
                player.walkRight(dt);
            if (controls.isUpPressed() && !player.isJumping())
                player.jump();
            if (controls.isFirePressed() && player.getState() != "shoot") {
                player.fire();
                bullets.add(new Bullet(bulletTexture, player.getDirection().x, player.getPosition()));
            }

            player.update(dt);
            boss.update(dt);
            if (!boss.isShoot()) {
                lastPosition = new Vector2(player.getPosition().x - boss.getCenter().x, player.getPosition().y - boss.getCenter().y);
                lastPosition2 = new Vector2(player.getPosition().x - boss.getCenter().x, player.getPosition().y - boss.getCenter().y).rotate(50);
                lastPosition3 = new Vector2(player.getPosition().x - boss.getCenter().x, player.getPosition().y - boss.getCenter().y).rotate(-50);
                lastPosition4 =new Vector2(player.getPosition().x - boss.getCenter().x, player.getPosition().y - boss.getCenter().y).rotate(25);
                lastPosition5 =new Vector2(player.getPosition().x - boss.getCenter().x, player.getPosition().y - boss.getCenter().y).rotate(-25);
            } else if (boss.isShoot() && boss.isAlive()) {
                bossBullets.add(new Bullet2(bossBulletTexture, lastPosition, boss.getCenter()));
                bossBullets.add(new Bullet2(bossBulletTexture, lastPosition2, boss.getCenter()));
                bossBullets.add(new Bullet2(bossBulletTexture, lastPosition3, boss.getCenter()));
                bossBullets.add(new Bullet2(bossBulletTexture, lastPosition4, boss.getCenter()));
                bossBullets.add(new Bullet2(bossBulletTexture, lastPosition5, boss.getCenter()));
                bossBullets.add(new Bullet2(bossBulletTexture, lastPosition, boss.getCenter()));
                bossBullets.add(new Bullet2(bossBulletTexture, lastPosition2, boss.getCenter()));
                bossBullets.add(new Bullet2(bossBulletTexture, lastPosition3, boss.getCenter()));
                bossBullets.add(new Bullet2(bossBulletTexture, lastPosition4, boss.getCenter()));
                bossBullets.add(new Bullet2(bossBulletTexture, lastPosition5, boss.getCenter()));


            }
            // bullets +map
            for (Rectangle r : mapBB) {
                //player bullets
                for (Bullet b : bullets) {
                    if (b.isAlive()) {
                        if (b.getBulletBC().overlaps(r))
                            b.setAlive(false);
                    }
                }
            }
            // bullets update
            //player bullet
            for (Bullet b : bullets)
                if (b.isAlive())
                    b.update(dt);


            // bullet on Boss
            for (Bullet b : bullets)
                if (b.isAlive() && b.getBulletBC().overlaps(boss.getMonsterBB())) {
                    boss.hit();
                    b.setAlive(false);
                }

            //MONSTER BULLETS
            //bullets update
            for (Bullet2 bm : bossBullets)
                if (bm.isAlive())
                    bm.update(dt);


            //boss bullet off screen
            for (Bullet2 bm : bossBullets)
                if (!cameraRect.contains(bm.getBulletBC()))
                    bm.setAlive(false);

            for (Bullet2 bm : bossBullets)
                if (bm.isAlive() && bm.getBulletBC().overlaps(player.getPlayerBB())) {
                    bm.setAlive(false);
                    player.hit();
                }

            //Iterators
            // bullet player
            Iterator<Bullet> it = bullets.iterator();
            while (it.hasNext()) {
                Bullet b = it.next();
                if (!b.isAlive())
                    it.remove();
            }

            // bullet monster
            Iterator<Bullet2> itMonster = bossBullets.iterator();
            while (itMonster.hasNext()) {
                Bullet2 bm2 = itMonster.next();
                if (!bm2.isAlive())
                    itMonster.remove();
            }
        }
        if (player.getHp() == 0) {
            dispose();
            gsm.set(new MenuState(gsm));
        } else if (!boss.isAlive()) {
            dispose();
           gsm.setLevelCount(9);
           gsm.set(new Level9(gsm));
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(camera.combined);
        sb.begin();

        // draw background
        mapSprite.draw(sb);

        player.render(sb);
        boss.render(sb);

        //bullets
        for (Bullet bullet : bullets)
            bullet.render(sb);

        for (Bullet2 bullet2 : bossBullets)
            bullet2.render(sb);

        controls.render(sb);

        sb.end();
        //debugRender();
    }

    @Override
    public void dispose() {
        controls.dispose();
        player.dispose();
        mapSprite.getTexture().dispose();

        for (Bullet b : bullets)
            b.dispose();
        for (Bullet2 bm : bossBullets)
            bm.dispose();
        boss.dispose();
    }
    private void showScreen() {
        if (alphaMap + 0.01f < 1f)
            alphaMap += 0.01f;
        else {
            alphaMap = 1f;
            isShowing = false;
            isPlaying = true;
        }

        mapSprite.setAlpha(alphaMap);
        player.setPlayerAlpha(alphaMap);
        controls.setAlphaButtons(alphaMap);
    }

    private void createMapBB() {
        mapBB = new Rectangle[3];

        mapBB[0] = new Rectangle(0f, 0f, 1920f, 166f);
        mapBB[1] = new Rectangle(-20, 0f, 20f, 1080f);
        mapBB[2] = new Rectangle(1910f, 0f, 10f, 1080f);


    }

    private void debugRender() {
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (Rectangle r : mapBB) {
            shapeRenderer.rect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
        }
        shapeRenderer.rect(boss.getMonsterBB().getX(), boss.getMonsterBB().getY(), boss.getMonsterBB().getWidth(), boss.getMonsterBB().getHeight());

        shapeRenderer.end();
    }
}
