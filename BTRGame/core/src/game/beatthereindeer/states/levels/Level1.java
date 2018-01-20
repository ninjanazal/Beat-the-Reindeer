package game.beatthereindeer.states.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
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


public class Level1 extends State {

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

    //DEBUG
    ShapeRenderer shapeRenderer;


    public Level1(GameStateManager gsm) {
        super(gsm);

        //MAP

        mapSprite = new Sprite(new Texture(Gdx.files.internal("lvl1/map1.png")));
        mapSprite.setSize(17308f, 6739f);
        mapSprite.setPosition(0f, 0f);

        visualMap = new Sprite(new Texture(Gdx.files.internal("lvl1/phone.png")));
        visualMap.setPosition(5494f, 4990f);
        createMapBB();

        //BACKGROUND
        backgroundSprite = new Sprite(new Texture(Gdx.files.internal("lvl1BG/map1Background.png")));
        backgroundSprite.setSize(20000f, 6739);
        backgroundSprite.setPosition(-1300, -10f);
        backgroundSprite.setAlpha(0.5f);

        //Player

        player = new Player(mapBB, new Vector2(200f, 1200f));
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
            gsm.setLevelCount(1);
            gsm.set(new MenuState(gsm));
        }

        //ON WIN
        if (player.getGiftCount() >= 10 && new Vector2(6920f, 5385f).dst(player.getPosition()) < 300f) {
            dispose();
            gsm.setLevelCount(2);
            gsm.set(new Level2(gsm));

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

        if (10 - player.getGiftCount() > 0)
            font.draw(sb, "You need more " + Integer.toString(10 - (player.getGiftCount())) + "\ngifts to enter the Boss!", 6920f, 5385f);
        else font.draw(sb, "Come here ma dude!", 6920f, 5385f);

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
        mapBB = new Rectangle[86];

        mapBB[0] = new Rectangle(0f, -200f, 17380f, 339f);
        mapBB[1] = new Rectangle(-10f, -100, 10f, 5000f);
        mapBB[2] = new Rectangle(16290f, 139f, 1030f, 591f);
        mapBB[3] = new Rectangle(1100f, 120f, 840f, 243f);
        mapBB[4] = new Rectangle(1334f, 359f, 122f, 192f);
        mapBB[5] = new Rectangle(1422f, 439f, 240f, 254f);
        mapBB[6] = new Rectangle(1460f, 694f, 119f, 109f);
        mapBB[7] = new Rectangle(1660f, 364f, 37f, 219f);
        mapBB[8] = new Rectangle(640f, 896, 474, 800);
        mapBB[9] = new Rectangle(1820f, 950f, 1987f, 570f);
        mapBB[10] = new Rectangle(1690f, 972f, 131f, 29f);
        mapBB[11] = new Rectangle(1114f, 1119f, 185f, 26f);
        mapBB[12] = new Rectangle(1694f, 1301f, 126f, 27f);
        mapBB[13] = new Rectangle(3679f, 1528f, 121f, 111f);
        mapBB[14] = new Rectangle(4075f, 1119f, 159f, 135f);
        mapBB[15] = new Rectangle(3730f, 119f, 1020f, 355f);
        mapBB[16] = new Rectangle(3606f, 120f, 124f, 129f);
        mapBB[17] = new Rectangle(4235f, 959f, 1033f, 802f);
        mapBB[18] = new Rectangle(5268f, 1229f, 196f, 134f);
        mapBB[19] = new Rectangle(5268f, 807f, 885f, 423f);
        mapBB[20] = new Rectangle(5805f, 1229f, 320f, 229f);
        mapBB[21] = new Rectangle(6206f, 1037f, 121f, 110f);
        mapBB[22] = new Rectangle(6267f, 899f, 121f, 110f);
        mapBB[23] = new Rectangle(6460f, 692f, 122f, 110f);
        mapBB[24] = new Rectangle(5512, 120f, 134f, 122f);
        mapBB[25] = new Rectangle(5645, 120f, 1023f, 319f);
        mapBB[26] = new Rectangle(6801f, 122f, 818f, 243f);
        mapBB[27] = new Rectangle(6820f, 549f, 69f, 110f);
        mapBB[28] = new Rectangle(6887, 808f, 1127f, 66f);
        mapBB[29] = new Rectangle(7998f, 145f, 120f, 112f);
        mapBB[30] = new Rectangle(8094f, 288f, 119f, 111f);
        mapBB[31] = new Rectangle(8154f, 421f, 121f, 110f);
        mapBB[32] = new Rectangle(8214f, 577f, 122f, 110f);
        mapBB[33] = new Rectangle(8365f, 613f, 726f, 176f);
        mapBB[34] = new Rectangle(9160f, 165f, 281f, 731);
        mapBB[35] = new Rectangle(9398, 163f, 218f, 856f);
        mapBB[36] = new Rectangle(9950f, 171f, 1784f, 1347f);
        mapBB[37] = new Rectangle(11731f, 173f, 1050f, 1600f);
        mapBB[38] = new Rectangle(12300f, 1768f, 306f, 280f);
        mapBB[39] = new Rectangle(12789f, 170f, 824f, 1819f);
        mapBB[40] = new Rectangle(13611f, 170f, 1087f, 1320f);
        mapBB[41] = new Rectangle(14557f, 1488f, 125f, 232f);
        mapBB[42] = new Rectangle(9613f, 169f, 276f, 1065f);
        mapBB[43] = new Rectangle(9799f, 1232f, 151f, 138f);
        mapBB[44] = new Rectangle(14702f, 1200f, 672f, 247f);
        mapBB[45] = new Rectangle(15380f, 817f, 1079f, 674f);
        mapBB[46] = new Rectangle(15373f, 1489f, 142f, 227f);
        mapBB[47] = new Rectangle(15516f, 1491f, 307f, 280f);
        mapBB[48] = new Rectangle(16453f, 1260f, 855f, 1390f);
        mapBB[49] = new Rectangle(11534f, 1510f, 196f, 101f);
        mapBB[50] = new Rectangle(11982f, 1763f, 317f, 142f);
        mapBB[51] = new Rectangle(14444f, 1484f, 117f, 113f);
        mapBB[52] = new Rectangle(15824f, 1487f, 121f, 109f);
        mapBB[53] = new Rectangle(14227f, 2325f, 243f, 110f);
        mapBB[54] = new Rectangle(15083f, 1472f, 231f, 14f);
        mapBB[55] = new Rectangle(13616f, 1489f, 143f, 348f);
        mapBB[56] = new Rectangle(13759f, 1491f, 85f, 103f);
        mapBB[57] = new Rectangle(13758f, 1589f, 43f, 129f);
        mapBB[58] = new Rectangle(14022f, 2051f, 291f, 120f);
        mapBB[59] = new Rectangle(14149f, 2167f, 162f, 107f);
        mapBB[60] = new Rectangle(14469f, 2365f, 459f, 246f);
        mapBB[61] = new Rectangle(14696f, 2612f, 130f, 108f);
        mapBB[62] = new Rectangle(14758f, 2717f, 123f, 114f);
        mapBB[63] = new Rectangle(14880f, 2619f, 297f, 425f);
        mapBB[64] = new Rectangle(14993f, 3041f, 119f, 115f);
        mapBB[65] = new Rectangle(15117f, 3122f, 931f, 270f);
        mapBB[66] = new Rectangle(15269f, 3435f, 292f, 71f);
        mapBB[67] = new Rectangle(15297f, 3508f, 783f, 147f);
        mapBB[68] = new Rectangle(15419f, 3676f, 835f, 148f);
        mapBB[69] = new Rectangle(15442f, 3821f, 805f, 140f);
        mapBB[70] = new Rectangle(16228f, 3918f, 422f, 124f);
        mapBB[71] = new Rectangle(16313f, 4040f, 338f, 780f);
        mapBB[72] = new Rectangle(14882f, 3797f, 121f, 109f);
        mapBB[73] = new Rectangle(14758f, 3928f, 122f, 110f);
        mapBB[74] = new Rectangle(14448f, 3854f, 304f, 280f);
        mapBB[75] = new Rectangle(14132f, 4033f, 305f, 290f);
        mapBB[76] = new Rectangle(14047f, 4397f, 240f, 110f);
        mapBB[77] = new Rectangle(14051f, 4504f, 116f, 115f);
        mapBB[78] = new Rectangle(13939f, 4626f, 121f, 108f);
        mapBB[79] = new Rectangle(12816f, 4407f, 1085f, 436f);
        mapBB[80] = new Rectangle(11686f, 4402f, 1122f, 702f);
        mapBB[81] = new Rectangle(10966f, 4706f, 137f, 112f);
        mapBB[82] = new Rectangle(10861f, 4712f, 123f, 216f);
        mapBB[83] = new Rectangle(5511f, 4646f, 5353f, 457f);
        mapBB[84] = new Rectangle(5265f, 5099f, 246f, 1024f);
        mapBB[85] = new Rectangle(12810f, 4839f, 132f, 125f);

    }

    private void createMonsters() {
        reindeers = new ArrayList<Reindeer>();
        reindeerTexture = new Texture(Gdx.files.internal("monsters/reindeerSprite.png"));

        reindeers.add(new Reindeer(reindeerTexture, new Vector2(2595f, 1527f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(5150f, 1757f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(7076f, 864f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(11494f, 1606f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(11962f, 1934f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(15529f, 1776f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(15736f, 4049f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(14732f, 4138f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(10858f, 5101f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(8468f, 5101f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(12815f, 4979f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(5988f, 1471f)));
        reindeers.add(new Reindeer(reindeerTexture, new Vector2(2202f, 144f)));
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
