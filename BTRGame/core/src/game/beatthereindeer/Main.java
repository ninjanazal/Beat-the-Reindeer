package game.beatthereindeer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import game.beatthereindeer.states.GameStateManager;
import game.beatthereindeer.states.IntroState;
import game.beatthereindeer.states.MenuState;
import game.beatthereindeer.states.levels.Level1;
import game.beatthereindeer.states.levels.Level2;
import game.beatthereindeer.states.levels.Level3;
import game.beatthereindeer.states.levels.Level4;
import game.beatthereindeer.states.levels.Level5;
import game.beatthereindeer.states.levels.Level6;
import game.beatthereindeer.states.levels.Level7;
import game.beatthereindeer.states.levels.Level8;
import game.beatthereindeer.states.levels.Level9;

public class Main extends ApplicationAdapter {
	private SpriteBatch batch;

	public static final int WIDTH = 1920;
	public static final int HEIGHT = 1080;


	public static final String TITLE = "Beat The Reindeer";

	private GameStateManager gsm;

	@Override
	public void create () {

		// inicia o spriteBatch e a classe GameStateManager
		batch = new SpriteBatch();
		batch.enableBlending();

		gsm = new GameStateManager();

		// define o primeiro estado do jogo
		gsm.push(new IntroState(gsm));
		// cor do fundo
		Gdx.gl.glClearColor(0, 0, 0, 1);
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render(batch);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
