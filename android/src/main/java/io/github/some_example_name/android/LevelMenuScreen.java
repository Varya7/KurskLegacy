package io.github.some_example_name.android;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Align;

public class LevelMenuScreen implements Screen {

    private final MainGame game;
    private BitmapFont titleFont;
    private BitmapFont itemFont;
    private GlyphLayout layout;

    private final String[] items = {
        "Введение",
        "Уровень 1",
        "Уровень 2",
        "Уровень 3"
    };
    private int hoveredIndex = -1;

    public LevelMenuScreen(MainGame game) {
        this.game = game;

        FreeTypeFontGenerator generator =
            new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Regular.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter titleParam =
            new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParam.size = 72;
        titleParam.color = new Color(1f, 0.96f, 0.85f, 1f);
        titleParam.borderWidth = 3f;
        titleParam.borderColor = new Color(0.15f, 0.08f, 0.03f, 0.95f);
        titleParam.shadowOffsetX = 3;
        titleParam.shadowOffsetY = 3;
        titleParam.characters =
            "абвгдеёжзийклмнопрстуфхцчшщъыьэюя" +
                "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" +
                "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "0123456789.,!?-—–«»\"':;() ";

        titleFont = generator.generateFont(titleParam);

        FreeTypeFontGenerator.FreeTypeFontParameter itemParam =
            new FreeTypeFontGenerator.FreeTypeFontParameter();
        itemParam.size = 40;
        itemParam.color = new Color(1f, 0.96f, 0.85f, 1f);
        itemParam.borderWidth = 2f;
        itemParam.borderColor = new Color(0.12f, 0.06f, 0.03f, 0.95f);
        itemParam.shadowOffsetX = 2;
        itemParam.shadowOffsetY = 2;
        itemParam.characters = titleParam.characters;

        itemFont = generator.generateFont(itemParam);

        generator.dispose();

        layout = new GlyphLayout();

        if (game.whitePixelTexture == null) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            game.whitePixelTexture = new Texture(pixmap);
            pixmap.dispose();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.16f, 0.10f, 0.06f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        updateHover();

        game.getBatch().begin();

        layout.setText(titleFont, "Меню уровней",
            new Color(1f, 0.96f, 0.85f, 1f),
            w, Align.center, true);
        titleFont.draw(game.getBatch(), layout, 0, h - 120);

        float squareSize = 220f;
        float gap = 40f;
        int count = items.length;

        float totalWidth = count * squareSize + (count - 1) * gap;
        float startX = (w - totalWidth) / 2f;
        float centerY = h / 2f;
        float squareY = centerY - squareSize / 2f;

        for (int i = 0; i < count; i++) {
            float x = startX + i * (squareSize + gap);
            float y = squareY;

            boolean hovered = (i == hoveredIndex);

            boolean completed;
            if (i == 0) {
                completed = game.introCompleted;
            } else {
                completed = game.levelCompleted[i - 1];
            }

            Color fill = completed
                ? new Color(0.7f, 0.55f, 0.25f, 1f)
                : new Color(0.22f, 0.14f, 0.09f, 1f);

            Color border;
            float bw;
            if (completed) {
                border = new Color(1f, 0.98f, 0.7f, 1f);
                bw = 6f;
            } else if (hovered) {
                border = new Color(1f, 0.95f, 0.6f, 1f);
                bw = 4f;
            } else {
                border = new Color(1f, 0.9f, 0.5f, 1f);
                bw = 3f;
            }

            Color textColor = new Color(1f, 0.96f, 0.85f, 1f);

            game.getBatch().setColor(fill);
            game.getBatch().draw(game.whitePixelTexture, x, y, squareSize, squareSize);

            game.getBatch().setColor(border);
            game.getBatch().draw(game.whitePixelTexture, x, y, squareSize, bw);
            game.getBatch().draw(game.whitePixelTexture, x, y + squareSize - bw, squareSize, bw);
            game.getBatch().draw(game.whitePixelTexture, x, y, bw, squareSize);
            game.getBatch().draw(game.whitePixelTexture, x + squareSize - bw, y, bw, squareSize);

            layout.setText(itemFont, items[i], textColor, squareSize, Align.center, true);
            itemFont.setColor(textColor);
            itemFont.draw(game.getBatch(), layout, x, y - 20f);
        }

        game.getBatch().setColor(Color.WHITE);
        game.getBatch().end();

        handleClick();
    }

    private void updateHover() {
        hoveredIndex = -1;

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        float squareSize = 220f;
        float gap = 40f;
        int count = items.length;

        float totalWidth = count * squareSize + (count - 1) * gap;
        float startX = (w - totalWidth) / 2f;
        float centerY = h / 2f;
        float squareY = centerY - squareSize / 2f;

        float mx = Gdx.input.getX();
        float my = Gdx.input.getY();
        float myWorld = h - my;

        for (int i = 0; i < count; i++) {
            float x = startX + i * (squareSize + gap);
            float y = squareY;

            if (mx >= x && mx <= x + squareSize &&
                myWorld >= y && myWorld <= y + squareSize) {
                hoveredIndex = i;
                break;
            }
        }
    }

    private void handleClick() {
        if (!Gdx.input.justTouched() || hoveredIndex == -1) return;

        switch (hoveredIndex) {
            case 0:
                game.setScreen(new SceneScreen(game));
                break;
            case 1:
                game.setScreen(new Level1Screen(game));
                break;
            case 2:
                game.setScreen(new Level2Screen(game));
                break;
            case 3:
                game.setScreen(new Level3Screen(game));
                break;
        }
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (titleFont != null) titleFont.dispose();
        if (itemFont != null) itemFont.dispose();
    }
}
