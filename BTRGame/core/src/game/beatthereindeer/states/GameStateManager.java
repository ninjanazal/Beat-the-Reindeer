package game.beatthereindeer.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Json;

import java.util.Stack;

/**
 * Created by Eurico on 26/12/2017.
 */

public class GameStateManager{

    private Stack<State> states;
    private int levelCount;

    public  GameStateManager(){
        states = new Stack<State>();
        levelCount = 0;
    }

    public void push(State state){
        states.push(state);
    }

    public void pop(){
        states.pop();
    }

    public void set(State state){
        states.pop();
        states.push(state);
    }

    // Update do state no topo da stack
    public void update(float dt){
        states.peek().update(dt);
    }

    // Render do state no topo da stack
    public void render(SpriteBatch sB){
        states.peek().render(sB);
    }

    public int getLevelCount() {
        return levelCount;
    }

    public void setLevelCount(int levelCount) {
        this.levelCount = levelCount;
    }
}

