package io.github.some_example_name.android;


import com.badlogic.gdx.Game;
import io.github.some_example_name.android.KurskJourneyGame;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class KurskJourneyGame extends Game {
    public SpriteBatch batch;

    @Override
    public void create() {
        //batch = new SpriteBatch();
        //setScreen(new SceneScreen(this, 0)); // Стартуем со сцены 0
    }

    @Override
    public void dispose() {
        //batch.dispose();
        //getScreen().dispose();
    }
}
