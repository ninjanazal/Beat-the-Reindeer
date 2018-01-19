package game.beatthereindeer.states.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import game.beatthereindeer.Bullet;
import game.beatthereindeer.Controls;
import game.beatthereindeer.Main;
import game.beatthereindeer.Player;
import game.beatthereindeer.Reindeer;
import game.beatthereindeer.states.GameStateManager;
import game.beatthereindeer.states.MenuState;
import game.beatthereindeer.states.State;

/**
 * Created by Geral on 15/01/2018.
 */

public class Level7 extends State {
    //player
    private Player player;
    private List<Bullet> bullets;
    private Texture bulletTexture;

    //Gifts
    private List<Sprite> gifts;
    private Texture giftTexture;

    // on Screan giftCount;
    private Sprite onScreenGift;
    private BitmapFont font;

    // monsters
    private List<Reindeer> reindeers;
    private Texture reindeerTexture;
    private List<Bullet> monsterBullet;

    // Button
    private Controls controls;
    private Vector3 pressPoss;
    // Map
    private Sprite mapSprite;
    private float alphaMap;
    private Rectangle[] mapBB;

    // Background
    private Sprite backgroundSprite;

    private Sprite visualMap;

    // camera REct
    private Rectangle cameraRect;


    // transitions
    private boolean isShowing;
    private boolean isPlaying;

    private ShapeRenderer shapeRenderer;

    public Level7(GameStateManager gsm) {
        super(gsm);

        //MAP

        mapSprite = new Sprite(new Texture(Gdx.files.internal("lvl7/map3.png")));
        mapSprite.setSize(6617f, 17000f);
        mapSprite.setPosition(0f, 0f);

        visualMap = new Sprite(new Texture(Gdx.files.internal("lvl7/over.png")));
        visualMap.setSize(6617f,3505f);
        visualMap.setPosition(0, 0);
        visualMap.setAlpha(0.2f);
        createMapBB();

        //BACKGROUND
        backgroundSprite = new Sprite(new Texture(Gdx.files.internal("lvl7/map3Bg.png")));
        backgroundSprite.setSize(6617f, 17000f);
        backgroundSprite.setPosition(0, 0);

        //Player

        player = new Player(mapBB, new Vector2(200f, 16860f));
        //player = new Player(mapBB, new Vector2(1108f, 1181f));

        bullets = new ArrayList<Bullet>();
        bulletTexture = new Texture(Gdx.files.internal("player/bullet.png"));

        //monsters
        createMonsters();
        monsterBullet = new ArrayList<Bullet>();

        //Gifts
        giftTexture = new Texture(Gdx.files.internal("controls/gift.png"));
        gifts = new ArrayList<Sprite>();


        //CAMERA
        pressPoss = new Vector3().setZero();

        camera.setToOrtho(false, Main.WIDTH, Main.HEIGHT);

        //camara position
        camera.position.set(player.getPosition().x + 150f, player.getPosition().y + 250f, 0f);
        camera.update();

        cameraRect = new Rectangle(camera.position.x - (Main.WIDTH / 2), camera.position.y - (Main.HEIGHT / 2), Main.WIDTH, Main.HEIGHT);

        // ONSCREEN
        onScreenGift = new Sprite(giftTexture);
        onScreenGift.setPosition(cameraRect.x + 50f, cameraRect.y + cameraRect.height - (onScreenGift.getHeight() + 50f));
        font = new BitmapFont(Gdx.files.internal("font/font.fnt"));

        font.setColor(Color.WHITE);

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


        if (isPlaying && player.getHp() != 0) {
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
            // monsters
            for (Reindeer r : reindeers) {
                if (r.isAlive()) {
                    r.update(player.getPosition(), dt);
                    if (r.isShoot())
                        monsterBullet.add(new Bullet(bulletTexture, r.getDirection(), r.getPosition()));
                }
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
                for (Bullet bM : monsterBullet) {
                    if (bM.isAlive())
                        if (bM.getBulletBC().overlaps(r))
                            bM.setAlive(false);
                }
            }


            // bullets update
            //player bullet
            for (Bullet b : bullets)
                if (b.isAlive())
                    b.update(dt);
            //monster Bullet
            for (Bullet bM : monsterBullet)
                if (bM.isAlive())
                    bM.update(dt);

            //player bullet on Reindeer
            for (Bullet b : bullets)
                if (b.isAlive())
                    for (Reindeer r : reindeers)
                        if (r.getReindeerBB().overlaps(b.getBulletBC())) {
                            b.setAlive(false);
                            r.hit();
                        }

            //monster bullets on player
            for (Bullet bM : monsterBullet)
                if (bM.isAlive())
                    if (bM.getBulletBC().overlaps(player.getPlayerBB())) {
                        bM.setAlive(false);
                        player.hit();
                    }

            for (Sprite gft : gifts)
                if (gft.getBoundingRectangle().overlaps(player.getPlayerBB())) {
                    gft.setX(0f);
                    player.giftCollected();
                }

            //Iterators
            // bullet player
            Iterator<Bullet> it = bullets.iterator();
            while (it.hasNext()) {
                Bullet b = it.next();
                if (!b.isAlive())
                    it.remove();
            }
            //reindeers
            Iterator<Reindeer> iterator = reindeers.iterator();
            while (iterator.hasNext()) {
                Reindeer r = iterator.next();
                if (!r.isAlive()) {
                    iterator.remove();
                    Sprite g = new Sprite(giftTexture);
                    g.setPosition(r.getPosition().x + r.getReindeerBB().getWidth() / 2, r.getPosition().y + r.getReindeerBB().getHeight() / 2);
                    gifts.add(g);
                }
            }

            // reindeers bullets
            Iterator<Bullet> monsterIterator = monsterBullet.iterator();
            while (monsterIterator.hasNext()) {
                Bullet bulletm = monsterIterator.next();
                if (!bulletm.isAlive())
                    monsterIterator.remove();
            }
            // gifts
            Iterator<Sprite> giftIT = gifts.iterator();
            while (giftIT.hasNext()) {
                Sprite gift = giftIT.next();
                if (gift.getX() == 0)
                    giftIT.remove();
            }

            if (player.getDirection().x > 0) {
                camera.position.set(player.getPosition().x + 150f, player.getPosition().y + 250f, 0f);
                camera.update();
            } else if (player.getDirection().x < 0) {
                camera.position.set(player.getPosition().x - 150f + player.getWidth(), player.getPosition().y + 250f, 0f);
                camera.update();
            }

            cameraRect.x = camera.position.x - (Main.WIDTH / 2);
            cameraRect.y = camera.position.y - (Main.HEIGHT / 2);

            controls.updateButtonsPosition(cameraRect);

            //ONSCREEN
            onScreenGift.setPosition(cameraRect.x + 50f, cameraRect.y + cameraRect.height - (onScreenGift.getHeight() + 50f));
        }

        //ON LOSE
        if (player.getHp() == 0 || (player.getGiftCount() < 20 && new Vector2(3573f, 66f).dst(player.getPosition()) < 500f)) {
            player.dispose();
            for (Bullet b : bullets)
                b.dispose();
            for (Bullet bM : monsterBullet)
                bM.dispose();
            for (Reindeer r : reindeers)
                r.dispose();
            backgroundSprite.getTexture().dispose();
            mapSprite.getTexture().dispose();
            visualMap.getTexture().dispose();
            gsm.setLevelCount(4);
            gsm.set(new MenuState(gsm));
        }

        //ON WIN
        if (player.getGiftCount() >= 20 && new Vector2(3573f, 66f).dst(player.getPosition()) < 500f) {
            dispose();
            gsm.setLevelCount(8);
            gsm.set(new Level8(gsm));

        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(camera.combined);
        sb.begin();

        // draw background
        backgroundSprite.draw(sb);
        mapSprite.draw(sb);

        //monster
        for (Reindeer r : reindeers)
            r.render(sb);

        player.render(sb);

        //bullets
        for (Bullet bullet : bullets)
            bullet.render(sb);

        for (Bullet bulletM : monsterBullet)
            bulletM.render(sb);

        for (Sprite g : gifts)
            g.draw(sb);

        visualMap.draw(sb);
        controls.render(sb);

        //ONSCREEN
        onScreenGift.draw(sb);
        font.draw(sb, "= " + Integer.toString(player.getGiftCount()), onScreenGift.getX() + onScreenGift.getWidth() + 20f, onScreenGift.getY() + (onScreenGift.getHeight() / 2));

        if (20 - player.getGiftCount() > 0)
            font.draw(sb, "You need more " + Integer.toString(20 - (player.getGiftCount())) + "\ngifts to enter the Boss!\n DONT ENTER!!", 3068f, 1167f);
        else font.draw(sb, "Come here ma dude!", 3068f, 1167f);

        sb.end();

        //DEBUG
        debugRender();
    }

    @Override
    public void dispose() {
        mapSprite.getTexture().dispose();
        backgroundSprite.getTexture().dispose();
        player.dispose();
        controls.dispose();
        onScreenGift.getTexture().dispose();
        font.dispose();
        bulletTexture.dispose();
        for (Reindeer r : reindeers)
            r.dispose();
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
        mapBB = new Rectangle[15];

        mapBB[0] = new Rectangle(0f, 15728f, 4933f, 142f);
        mapBB[1] = new Rectangle(3893f, 14572f, 2721f, 128f);
        mapBB[2] = new Rectangle(1198f, 13289, 5403f, 149f);
        mapBB[3] = new Rectangle(0, 11992f, 2798f, 155f);
        mapBB[4] = new Rectangle(1719f, 10536f, 4898f, 145f);
        mapBB[5] = new Rectangle(0f, 9074f, 2787f, 167f);
        mapBB[6] = new Rectangle(2271f, 7540f, 4341f, 145f);
        mapBB[7] = new Rectangle(0f, 6319f, 3058f, 151f);
        mapBB[8] = new Rectangle(0f, 5017f, 4947f, 156f);
        mapBB[9] = new Rectangle(3902f, 3403f, 2716f, 150f);
        mapBB[10] = new Rectangle(2265f, 1800f, 4352f, 149f);
        mapBB[11] = new Rectangle(0f, 809f, 3069f, 138f);
        mapBB[12] = new Rectangle(0f, -20f, 6617f, 20f);
        mapBB[13] = new Rectangle(-20f, 0f, 20f, 17000f);
        mapBB[14] = new Rectangle(6617f, 0f, 20f, 17000f);

    }

    private void createMonsters() {
        reindeers = new ArrayList<Reindeer>();
        reindeerTexture = new Texture(Gdx.files.internal("monsters/reindeerSprite.png"));

        reindeers.add(new Reindeer(reindeerTexture, new Vector2(2904f, 15867f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(4811, 15867f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(3940f, 14752f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(6365f, 13444f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(1483f, 13444f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(4591f, 13458f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(2723f, 12151f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(1821f, 12151f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(6405f, 10693f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(5015f, 10693f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(1781f, 10700f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(2629f, 9214f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(6350f, 7713f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(4113f, 7699f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(2998f, 6484f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(86f, 6498f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(180f, 5198f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(1601f, 5226f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(4803f, 5198f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(3979f, 3547f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(6185f, 1968f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(5478f, 1975f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(3817f, 15883f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(6420f, 14734f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(3134f, 13460f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(173f, 12143f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(3215f, 10679f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(694f, 9226f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(1561f, 9226f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(2660f, 7699f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(1550f, 6466f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(3505f, 5171f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(5402f, 3559f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(2637f, 1979f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(2949f, 1958f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(3597f, 1948f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(1492f, 966f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(2221f, 968f)));
    }

    private void debugRender() {
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (Rectangle r : mapBB) {
            shapeRenderer.rect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
        }

        shapeRenderer.end();
    }
}
