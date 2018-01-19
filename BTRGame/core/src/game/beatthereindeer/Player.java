package game.beatthereindeer;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player {
    //Bounding Box
    private Rectangle playerBB;
    private Rectangle[] mapBB;

    //texture & sprite
    private TextureRegion[] animation;
    private Sprite player;
    private float playerAlpha;

    //position
    private Vector2 position;
    private float jumpAcelaration;
    private float MaxJumpAceleration;

    //Gravity
    private float GRAVITY = -90f;
    private float velocity;

    private boolean isJumping;

    // animation
    private String state;
    private int frame;
    private float lastFrameTime;

    // Direction
    private Vector2 direction;

    // hp
    private int hp;
    private Sprite hpBar;

    // Gifts
    private int giftCount;


    public Player(Rectangle[] mapBB, Vector2 startPos) {
        this.mapBB = new Rectangle[mapBB.length];
        this.mapBB = mapBB;
        MaxJumpAceleration = 600f;

        state = "idle";
        frame = 0;
        lastFrameTime = 0f;

        isJumping = false;
        velocity = 550f;

        animation = new TextureRegion[7];
        loadSpriteSheet();

        position = startPos;


        playerAlpha = 0f;
        player = new Sprite(animation[0]);
        player.setPosition(position.x, position.y);
        playerBB = new Rectangle(position.x + 30f, position.y, player.getRegionWidth() - 70f, player.getRegionHeight());

        direction = new Vector2(1f, 0f);

        hp = 100;
        hpBar = new Sprite(new Texture(Gdx.files.internal("controls/hpBar.png")));
        hpBar.setSize(hp, 4f);
        hpBar.setPosition(position.x, position.y + player.getRegionHeight() + 20f);

        giftCount = 0;
    }

    public void update(float dt) {
        lastFrameTime += dt;

        if (isJumping && jumpAcelaration > 0) {
            position.y += jumpAcelaration * dt * 2;
            jumpAcelaration -= jumpAcelaration * dt * 2.2f;
        }

        position.y += GRAVITY * dt;
        GRAVITY += GRAVITY * dt * 3.5f;


        player.setPosition(position.x, position.y);
        playerBB.set(position.x + 30f, position.y, player.getRegionWidth() - 70f, player.getRegionHeight());

        collision();
        player.setPosition(position.x, position.y);
        playerBB.set(position.x + 30f, position.y, player.getRegionWidth() - 70f, player.getRegionHeight());

        getFrame();
        player.setRegion(animation[frame]);

        hpBar.setSize(hp + 1, 4f);
        hpBar.setPosition(position.x + 20, position.y + player.getRegionHeight() + 20f);

    }

    public void render(SpriteBatch sb) {

        player.setAlpha(playerAlpha);
        hpBar.setAlpha(playerAlpha);

        player.draw(sb);
        hpBar.draw(sb);
    }

    private void loadSpriteSheet() {
        animation[0] = new TextureRegion(new Texture(Gdx.files.internal("player/spriteSheetSanta.png")), 0, 0, 231, 223);
        animation[1] = new TextureRegion(new Texture(Gdx.files.internal("player/spriteSheetSanta.png")), 231, 0, 231, 223);
        animation[2] = new TextureRegion(new Texture(Gdx.files.internal("player/spriteSheetSanta.png")), 2 * 231, 0, 231, 223);
        animation[3] = new TextureRegion(new Texture(Gdx.files.internal("player/spriteSheetSanta.png")), 3 * 231, 0, 231, 223);
        animation[4] = new TextureRegion(new Texture(Gdx.files.internal("player/spriteSheetSanta.png")), 4 * 231, 0, 231, 223);
        animation[5] = new TextureRegion(new Texture(Gdx.files.internal("player/spriteSheetSanta.png")), 5 * 231, 0, 231, 223);
        animation[6] = new TextureRegion(new Texture(Gdx.files.internal("player/spriteSheetSanta.png")), 6 * 231, 0, 231, 223);
    }

    // ACTIONS
    public void walkLeft(float dt) {
        direction.x = -1f;

        if (!animation[0].isFlipX()) {
            for (TextureRegion r : animation)
                r.flip(true, false);
        }
        position.x -= velocity * dt;
        playerBB.set(position.x + 30f, position.y, player.getRegionWidth() - 70f, player.getRegionHeight());

        if (state != "shoot")
            state = "run";

        for (Rectangle r : mapBB) {
            if (r.overlaps(playerBB)) {
                if (r.getY() + r.getHeight() >= playerBB.getY() && playerBB.getX() + playerBB.getWidth() > r.getX() + r.getWidth()) {
                    position.x += velocity * dt;
                    playerBB.set(position.x + 30f, position.y, player.getRegionWidth() - 70f, player.getRegionHeight());

                }
            }
        }


    }

    public void walkRight(float dt) {
        direction.x = 1f;

        if (animation[0].isFlipX()) {
            for (TextureRegion r : animation)
                r.flip(true, false);
        }
        position.x += velocity * dt;
        playerBB.set(position.x + 30f, position.y, player.getRegionWidth() - 70f, player.getRegionHeight());
        if (state != "shoot")
            state = "run";

        for (Rectangle r : mapBB) {
            if (r.overlaps(playerBB)) {
                if (r.getY() + r.getHeight() >= playerBB.getY() && playerBB.getX() < r.getX()) {
                    position.x -= velocity * dt;
                    playerBB.set(position.x + 30f, position.y, player.getRegionWidth() - 70f, player.getRegionHeight());
                }

            }
        }
    }

    public void jump() {
        isJumping = true;
    }

    public void fire() {
        state = "shoot";
    }

    //GETTERS


    public Rectangle getPlayerBB() {
        return playerBB;
    }

    public String getState() {
        return state;
    }

    public float getWidth() {
        return player.getRegionWidth();
    }

    public Vector2 getDirection() {
        return direction;
    }

    public Vector2 getPosition() {
        return position;
    }

    public boolean isJumping() {
        return isJumping;
    }

    public void setPlayerAlpha(float playerAlpha) {
        this.playerAlpha = playerAlpha;
    }

    private void getFrame() {

        if (state == "idle") {
            if (frame > 1) {
                frame = 0;
                lastFrameTime = 0;
            } else if (lastFrameTime >= 0.5f) {
                frame++;
                lastFrameTime = 0;
                if (frame > 1)
                    frame = 0;
            }
        } else if (state == "run") {
            if (frame < 2 || frame > 3) {
                frame = 2;
                lastFrameTime = 0;
            } else if (lastFrameTime >= 0.08f) {
                frame++;
                lastFrameTime = 0;
                if (frame > 3)
                    state = "idle";
            }
        } else if (state == "shoot") {
            if (frame < 4) {
                frame = 4;
                lastFrameTime = 0;
            } else if (lastFrameTime >= 0.08f) {
                frame++;
                lastFrameTime = 0;
                if (frame > 6) {
                    frame = 0;
                    state = "idle";
                }
            }
        }
    }

    public void dispose() {
        for (TextureRegion s : animation)
            s.getTexture().dispose();
        player.getTexture().dispose();
    }

    private void collision() {
        for (Rectangle r : mapBB) {
            if (r.overlaps(playerBB)) {
                if (r.getX() <= playerBB.getX() + playerBB.getWidth() && r.getX() + r.getWidth() >= playerBB.getX()) {
                    if (r.getY() + r.getHeight() > playerBB.getY() && r.getY() + r.getHeight() < playerBB.getY() + playerBB.getHeight()) {
                        position.y = r.getY() + r.getHeight();
                        isJumping = false;
                        jumpAcelaration = MaxJumpAceleration;
                        GRAVITY = -90;

                    } else if (r.getY() < playerBB.getY() + playerBB.getHeight()) {
                        position.y = r.getY() - playerBB.getHeight();
                        jumpAcelaration = 0;
                        GRAVITY = -200;

                    }
                }
            }
        }
    }

    public void hit() {
        if (hp > 0)
            hp -= 50;
        else hp = 0;
    }

    public int getHp() {
        return hp;
    }

    public int getGiftCount() {
        return giftCount;
    }

    public void giftCollected() {
        giftCount++;
    }
}
