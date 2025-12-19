package io.github.some_example_name.android;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Align;

import java.util.Iterator;

public class Level1Screen implements Screen {

    private final MainGame game;

    private enum State {
        INSTRUCTIONS,
        RUNNING,
        FINISHED
    }

    private State state = State.INSTRUCTIONS;
    private boolean win = false;

    private Texture[] jugTextures;
    private Texture[] trashTextures;
    private Texture background;

    private static class FallingObject {
        Rectangle bounds;
        Texture texture;
        boolean isGood;
    }

    private Array<FallingObject> objects;

    private long lastSpawnTime;
    private float spawnInterval = 0.7f;
    private float elapsedTime = 0f;
    private float targetTime = 30f;

    private int lives = 3;

    private final Vector3 touchPos = new Vector3();
    private final GlyphLayout layout = new GlyphLayout();

    private BitmapFont levelFont;

    public Level1Screen(MainGame game) {
        this.game = game;

        background = new Texture(Gdx.files.internal("level1_background.jpg"));

        jugTextures = new Texture[] {
            new Texture(Gdx.files.internal("jug1.png")),
            new Texture(Gdx.files.internal("jug2.png")),
            new Texture(Gdx.files.internal("jug3.png")),
            new Texture(Gdx.files.internal("jug4.png"))
        };

        trashTextures = new Texture[] {
            new Texture(Gdx.files.internal("trash1.png")),
            new Texture(Gdx.files.internal("trash2.png")),
            new Texture(Gdx.files.internal("trash3.png"))
        };

        objects = new Array<FallingObject>();
        spawnObject();

        FreeTypeFontGenerator generator =
            new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Regular.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter param =
            new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 46;
        param.color = new Color(0.3f, 0.18f, 0.08f, 1f);
        param.borderWidth = 0f;
        param.shadowOffsetX = 0;
        param.shadowOffsetY = 0;
        param.characters =
            "абвгдеёжзийклмнопрстуфхцчшщъыьэюя" +
                "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" +
                "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "0123456789.,!?-—–«»\"':;() ";

        levelFont = generator.generateFont(param);
        generator.dispose();
    }

    private void spawnObject() {
        FallingObject obj = new FallingObject();
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        boolean isGood = Math.random() < 0.6;
        obj.isGood = isGood;

        if (isGood) {
            Texture t = jugTextures[(int)(Math.random() * jugTextures.length)];
            obj.texture = t;
        } else {
            Texture t = trashTextures[(int)(Math.random() * trashTextures.length)];
            obj.texture = t;
        }

        float width = obj.texture.getWidth();
        float height = obj.texture.getHeight();

        obj.bounds = new Rectangle();
        obj.bounds.width = width;
        obj.bounds.height = height;

        float x = (float) (Math.random() * (screenWidth - width));
        float y = screenHeight;

        obj.bounds.setPosition(x, y);

        objects.add(obj);
        lastSpawnTime = TimeUtils.millis();
    }

    @Override
    public void render(float delta) {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        Gdx.gl.glClearColor(0.16f, 0.10f, 0.06f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // ---------- ИНСТРУКЦИЯ ----------
        if (state == State.INSTRUCTIONS) {
            game.getBatch().begin();

            if (background != null) {
                game.getBatch().draw(background, 0, 0, w, h);
            }

            String text =
                "Нажимай на глиняные изделия,\n" +
                    "не нажимай на мусор.\n\n" +
                    "Если изделие упадёт или\n" +
                    "ты нажмёшь на мусор,\n" +
                    "теряешь одну из 3 жизней.\n\n" +
                    "Помоги мастеру сохранить горшки\n" +
                    "в течение 30 секунд.\n\n" +
                    "Нажми, чтобы начать.";

            layout.setText(levelFont, text,
                new Color(0.3f, 0.18f, 0.08f, 1f),
                w - 80, Align.center, true);
            levelFont.draw(game.getBatch(), layout,
                40, h * 0.65f);

            game.getBatch().end();

            if (Gdx.input.justTouched()) {
                state = State.RUNNING;
                elapsedTime = 0f;
                lives = 3;
                objects.clear();
                spawnObject();
            }
            return;
        }

        if (state == State.RUNNING) {
            updateGame(delta);
        }

        // ---------- ОТРИСОВКА ИГРЫ ----------
        game.getBatch().begin();

        if (background != null) {
            game.getBatch().draw(background, 0, 0, w, h);
        }

        for (FallingObject obj : objects) {
            game.getBatch().draw(obj.texture,
                obj.bounds.x,
                obj.bounds.y,
                obj.bounds.width,
                obj.bounds.height);
        }

        String timeText = "Время: " + (int)Math.ceil(Math.max(0, targetTime - elapsedTime)) + " c";
        String livesText = "Жизни: " + lives;

        levelFont.draw(game.getBatch(), timeText, 20, h - 40);
        levelFont.draw(game.getBatch(), livesText, 20, h - 90);

        if (state == State.FINISHED) {
            String result = win ? "Вы выиграли!" : "Вы проиграли";

            float panelW = w - 80;
            float panelH = 180;
            float panelX = 40;
            float panelY = h / 2f - panelH / 2f;

            game.getBatch().setColor(1f, 0.98f, 0.9f, 0.95f);
            game.getBatch().draw(game.whitePixelTexture,
                panelX, panelY, panelW, panelH);

            game.getBatch().setColor(0.4f, 0.25f, 0.1f, 1f);
            game.getBatch().draw(game.whitePixelTexture,
                panelX, panelY, panelW, 4);
            game.getBatch().draw(game.whitePixelTexture,
                panelX, panelY + panelH - 4, panelW, 4);
            game.getBatch().draw(game.whitePixelTexture,
                panelX, panelY, 4, panelH);
            game.getBatch().draw(game.whitePixelTexture,
                panelX + panelW - 4, panelY, 4, panelH);

            game.getBatch().setColor(Color.WHITE);

            layout.setText(levelFont, result,
                new Color(0.2f, 0.12f, 0.05f, 1f),
                panelW - 40, Align.center, true);
            levelFont.draw(game.getBatch(), layout,
                panelX + 20, panelY + panelH / 2f + 20);
        }


        game.getBatch().end();

        if (state == State.FINISHED && Gdx.input.justTouched()) {
            if (win) {
                game.setLevelCompleted(0, true);
                game.setScreen(new LevelMenuScreen(game));
            } else {
                state = State.INSTRUCTIONS;
            }
        }
    }

    private void updateGame(float delta) {
        if (state != State.RUNNING) return;

        elapsedTime += delta;

        if (lives <= 0) {
            win = false;
            state = State.FINISHED;
            return;
        }

        if (elapsedTime >= targetTime) {
            win = (lives > 0);
            state = State.FINISHED;
            return;
        }

        if (TimeUtils.timeSinceMillis(lastSpawnTime) > (long)(spawnInterval * 1000)) {
            spawnObject();
        }

        float fallSpeed = 300f;
        float screenHeight = Gdx.graphics.getHeight();

        Iterator<FallingObject> it = objects.iterator();
        while (it.hasNext()) {
            FallingObject obj = it.next();
            obj.bounds.y -= fallSpeed * delta;

            if (obj.bounds.y + obj.bounds.height < 0) {
                if (obj.isGood) {
                    lives--;
                }
                it.remove();
            }
        }

        if (Gdx.input.justTouched()) {
            float x = Gdx.input.getX();
            float y = Gdx.input.getY();
            touchPos.set(x, y, 0);

            float worldY = screenHeight - touchPos.y;
            float worldX = touchPos.x;

            FallingObject clicked = null;
            for (FallingObject obj : objects) {
                if (obj.bounds.contains(worldX, worldY)) {
                    clicked = obj;
                    break;
                }
            }

            if (clicked != null) {
                if (clicked.isGood) {
                    objects.removeValue(clicked, true);
                } else {
                    lives--;
                    objects.removeValue(clicked, true);
                }
            }
        }
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        for (Texture t : jugTextures) t.dispose();
        for (Texture t : trashTextures) t.dispose();
        if (background != null) background.dispose();
        if (levelFont != null) levelFont.dispose();
    }
}
