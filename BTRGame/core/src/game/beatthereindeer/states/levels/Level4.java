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

public class Level4 extends State {
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

    public Level4(GameStateManager gsm) {
        super(gsm);

        //MAP

        mapSprite = new Sprite(new Texture(Gdx.files.internal("lvl4/map2.png")));
        mapSprite.setSize(17308f, 6739f);
        mapSprite.setPosition(0f, 0f);

        visualMap = new Sprite(new Texture(Gdx.files.internal("lvl1/phone.png")));
        visualMap.setPosition(12891f, 1387f);
        createMapBB();

        //BACKGROUND
        backgroundSprite = new Sprite(new Texture(Gdx.files.internal("lvl4/map2Bg.png")));
        backgroundSprite.setSize(17308f, 6739f);
        backgroundSprite.setPosition(0, 0);
        backgroundSprite.setAlpha(1.0f);

        //Player

        player = new Player(mapBB, new Vector2(200f, 3780f));
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
        if (player.getHp() == 0) {
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
        if (player.getGiftCount() >= 10 && new Vector2(6920f, 5385f).dst(player.getPosition()) < 300f) {
            dispose();
            gsm.setLevelCount(5);
            gsm.set(new Level5(gsm));

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

        if (15 - player.getGiftCount() > 0)
            font.draw(sb, "You need more " + Integer.toString(15 - (player.getGiftCount())) + "\ngifts to enter the Boss!", 14682f, 1883f);
        else font.draw(sb, "Come here ma dude!", 14682f, 1883f);

        sb.end();

        //DEBUG
        //debugRender();

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
        visualMap.setAlpha(alphaMap);
    }

    private void createMapBB() {
        mapBB = new Rectangle[54];

        mapBB[0] = new Rectangle(-30f, 2805f, 30f, 4000f);
        mapBB[1] = new Rectangle(0f, 2805f, 881f, 128f);
        mapBB[2] = new Rectangle(871f, 2638f, 1101f, 139f);
        mapBB[3] = new Rectangle(1971f, 2434f, 657f, 126f);
        mapBB[4] = new Rectangle(2634f, 2368f, 664f, 130f);
        mapBB[5] = new Rectangle(3279f, 2564f, 879f, 129f);
        mapBB[6] = new Rectangle(4143f, 2763f, 226f, 133f);
        mapBB[7] = new Rectangle(4358f, 2978f, 446f, 136f);
        mapBB[8] = new Rectangle(4796f, 3199f, 230f, 133f);
        mapBB[9] = new Rectangle(5022f, 3065f, 640f, 135f);
        mapBB[10] = new Rectangle(5624f, 3203f, 135f, 132f);
        mapBB[11] = new Rectangle(5684f, 3332f, 143f, 137f);
        mapBB[12] = new Rectangle(5737f, 3462f, 135f, 136f);
        mapBB[13] = new Rectangle(5793f, 3598f, 206f, 132f);
        mapBB[14] = new Rectangle(5963f, 3731f, 135f, 138f);
        mapBB[15] = new Rectangle(6011f, 3871f, 151f, 130f);
        mapBB[16] = new Rectangle(6075f, 3996f, 144f, 137f);
        mapBB[17] = new Rectangle(6124f, 4132f, 162f, 132f);
        mapBB[18] = new Rectangle(6181f, 4267f, 223f, 133f);
        mapBB[19] = new Rectangle(6390f, 4390f, 135f, 745f);
        mapBB[20] = new Rectangle(5038f, 3998f, 449f, 134f);
        mapBB[21] = new Rectangle(4377f, 4213f, 673f, 135f);
        mapBB[22] = new Rectangle(3934f, 4333f, 451f, 131f);
        mapBB[23] = new Rectangle(3765f, 4467f, 230f, 135f);
        mapBB[24] = new Rectangle(3719f, 4601f, 229f, 130f);
        mapBB[25] = new Rectangle(3692f, 4736f, 229f, 131f);
        mapBB[26] = new Rectangle(3652f, 4870f, 233f, 135f);
        mapBB[27] = new Rectangle(3600f, 5000f, 231f, 134f);
        mapBB[28] = new Rectangle(3544f, 5133f, 235f, 133f);
        mapBB[29] = new Rectangle(3497f, 5267f, 229f, 131f);
        mapBB[30] = new Rectangle(3428f, 5393f, 233f, 138f);
        mapBB[31] = new Rectangle(3386f, 5534f, 238f, 135f);
        mapBB[32] = new Rectangle(3323f, 5666f, 238f, 131f);
        mapBB[33] = new Rectangle(4349f, 5131f, 3144f, 133f);
        mapBB[34] = new Rectangle(7480f, 4730f, 228f, 398f);
        mapBB[35] = new Rectangle(7696f, 4598f, 227f, 133f);
        mapBB[36] = new Rectangle(7918f, 4338f, 225f, 263f);
        mapBB[37] = new Rectangle(8104f, 4181f, 230f, 133f);
        mapBB[38] = new Rectangle(8323f, 4066f, 661f, 134f);
        mapBB[39] = new Rectangle(8978f, 3926f, 2590f, 135f);
        mapBB[40] = new Rectangle(11564f, 3801f, 229f, 135f);
        mapBB[41] = new Rectangle(11779f, 3668f, 231f, 134f);
        mapBB[42] = new Rectangle(12005f, 3536f, 888f, 132f);
        mapBB[43] = new Rectangle(12878f, 3435f, 229f, 132f);
        mapBB[44] = new Rectangle(13100f, 3303f, 232f, 133f);
        mapBB[45] = new Rectangle(13322f, 3168f, 231f, 136f);
        mapBB[46] = new Rectangle(13545f, 3046f, 227f, 135f);
        mapBB[47] = new Rectangle(13762f, 2915f, 229f, 134f);
        mapBB[48] = new Rectangle(13984f, 2799f, 225f, 131f);
        mapBB[49] = new Rectangle(14194f, 2661f, 886f, 135f);
        mapBB[50] = new Rectangle(13873f, 1296f, 2633, 290f);
        mapBB[51] = new Rectangle(16501f, 1561f, 794f, 242f);
        mapBB[52] = new Rectangle(17108f, 1805f, 190f, 1342f);
        mapBB[53] = new Rectangle(13551f, 1265f, 347f, 1017f);
    }

    private void createMonsters() {
        reindeers = new ArrayList<Reindeer>();
        reindeerTexture = new Texture(Gdx.files.internal("monsters/reindeerSprite.png"));

        reindeers.add(new Reindeer(reindeerTexture, new Vector2(1689f, 2782f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(3456f, 2694f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(4913f, 3340f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(5249f, 4131f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(4176f, 4465f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(4789f, 5262f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(6351f, 5265f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(7658f, 5135f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(8299f, 4319f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(10185f, 4068f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(11748f, 3946f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(12858f, 3667f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(14777f, 2802f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(16800f, 1811f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(5603f, 5260f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(3401f, 5798)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(6222f, 4398)));
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
