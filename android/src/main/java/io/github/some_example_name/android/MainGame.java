package io.github.some_example_name.android;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Preferences;

public class MainGame extends Game {

    private SpriteBatch batch;
    public Texture whitePixelTexture;

    public boolean introCompleted = false;
    public boolean[] levelCompleted = new boolean[3];
    private Preferences prefs;

    @Override
    public void create() {
        batch = new SpriteBatch();

        prefs = Gdx.app.getPreferences("game_prefs");
        introCompleted = prefs.getBoolean("introCompleted", false);

        levelCompleted[0] = prefs.getBoolean("level1Completed", false);
        levelCompleted[1] = prefs.getBoolean("level2Completed", false);
        levelCompleted[2] = prefs.getBoolean("level3Completed", false);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whitePixelTexture = new Texture(pixmap);
        pixmap.dispose();

        if (introCompleted) {
            setScreen(new LevelMenuScreen(this));
        } else {
            setScreen(new SceneScreen(this));
        }
    }

    public void setIntroCompleted(boolean value) {
        introCompleted = value;
        prefs.putBoolean("introCompleted", value);
        prefs.flush();
    }

    public void setLevelCompleted(int index, boolean value) {
        if (index < 0 || index >= levelCompleted.length) return;
        levelCompleted[index] = value;
        prefs.putBoolean("level" + (index + 1) + "Completed", value);
        prefs.flush();
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (batch != null) batch.dispose();
        if (whitePixelTexture != null) whitePixelTexture.dispose();
    }


}
