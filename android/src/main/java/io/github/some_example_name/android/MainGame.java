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

    // прогресс
    public boolean introCompleted = false;
    public boolean[] levelCompleted = new boolean[3]; // [0] – уровень 1, [1] – 2, [2] – 3

    private Preferences prefs;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Preferences для сохранения прогресса между запусками
        prefs = Gdx.app.getPreferences("game_prefs");
        introCompleted = prefs.getBoolean("introCompleted", false);

        // при желании можно подтянуть и уровни:
        levelCompleted[0] = prefs.getBoolean("level1Completed", false);
        levelCompleted[1] = prefs.getBoolean("level2Completed", false);
        levelCompleted[2] = prefs.getBoolean("level3Completed", false);

        // 1x1 белый пиксель для рамок/квадратов в меню
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whitePixelTexture = new Texture(pixmap);
        pixmap.dispose();

        // стартовый экран: если введение уже пройдено – сразу меню, иначе интро
        if (introCompleted) {
            setScreen(new LevelMenuScreen(this));
        } else {
            setScreen(new SceneScreen(this));
        }
    }

    // чтобы удобно помечать введение как пройденное и сохранять
    public void setIntroCompleted(boolean value) {
        introCompleted = value;
        prefs.putBoolean("introCompleted", value);
        prefs.flush();
    }

    // чтобы помечать завершение уровней
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
