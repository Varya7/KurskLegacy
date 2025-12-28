package io.github.some_example_name.android;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Align;

public class LevelMenuScreen implements Screen {

    private final MainGame game;
    private BitmapFont titleFont;
    private BitmapFont bookFont;
    private BitmapFont gameFont;
    private GlyphLayout layout;

    private Texture bookTexture;
    private TextureRegion bookRegion;

    private final String[] scenes = {
        "Введение",
        "Уровень 1",
        "Уровень 2",
        "Уровень 3",
        "Заключение"
    };

    private final String[] games = {
        "Игра 1",
        "Игра 2",
        "Игра 3"
    };

    private int hoveredSceneIndex = -1;
    private int hoveredGameIndex = -1;
    private boolean showGamesPage = false;
    private boolean gamesButtonHovered = false;

    private final boolean[] sceneAvailable = {true, true, true, true, true};
    private final boolean[] gameAvailable = {true, true, true};

    public LevelMenuScreen(MainGame game) {
        this.game = game;
        loadAssets();
    }

    private void loadAssets() {
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
            "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ0123456789.,!?-—–«»\"':;() ";
        titleFont = generator.generateFont(titleParam);

        FreeTypeFontGenerator.FreeTypeFontParameter bookParam =
            new FreeTypeFontGenerator.FreeTypeFontParameter();
        bookParam.size = 48;
        bookParam.color = new Color(1f, 0.95f, 0.8f, 1f);
        bookParam.borderWidth = 2f;
        bookParam.borderColor = new Color(0.1f, 0.05f, 0.02f, 0.9f);
        bookParam.shadowOffsetX = 2;
        bookParam.shadowOffsetY = 2;
        bookParam.characters = titleParam.characters;
        bookFont = generator.generateFont(bookParam);

        FreeTypeFontGenerator.FreeTypeFontParameter gameParam =
            new FreeTypeFontGenerator.FreeTypeFontParameter();
        gameParam.size = 64;
        gameParam.color = new Color(1f, 0.96f, 0.85f, 1f);
        gameParam.borderWidth = 3f;
        gameParam.borderColor = new Color(0.12f, 0.06f, 0.03f, 0.95f);
        gameParam.shadowOffsetX = 3;
        gameParam.shadowOffsetY = 3;
        gameParam.characters = titleParam.characters;
        gameFont = generator.generateFont(gameParam);

        generator.dispose();

        bookTexture = new Texture(Gdx.files.internal("book.png"));
        bookRegion = new TextureRegion(bookTexture);

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

        updateHover(w, h);

        game.getBatch().begin();

        String title = showGamesPage ? "Игры" : "История Курска";
        layout.setText(titleFont, title, new Color(1f, 0.96f, 0.85f, 1f), w, Align.center, true);
        titleFont.draw(game.getBatch(), layout, 0, h - 100);

        if (showGamesPage) {
            drawGamesPage(w, h);
        } else {
            drawBookPage(w, h);
        }

        game.getBatch().end();
        handleClick();
    }

    private void drawBookPage(float w, float h) {
        float bookScale = Math.min(w * 0.8f / bookRegion.getRegionWidth(),
            h * 0.7f / bookRegion.getRegionHeight());
        float drawW = bookRegion.getRegionWidth() * bookScale;
        float drawH = bookRegion.getRegionHeight() * bookScale;
        float drawX = (w - drawW) / 2f;
        float drawY = h * 0.15f;

        game.getBatch().setColor(Color.WHITE);
        game.getBatch().draw(bookRegion, drawX, drawY, drawW, drawH);

        float gamesBtnHeight = 65f;
        drawUnifiedButton((w - 350f) / 2f, drawY + 20f, 350f, gamesBtnHeight, "ИГРЫ", gamesButtonHovered, false);

        float pageWidth = drawW * 0.55f;
        float btnHeight = 65f;
        float btnGap = 15f;
        float pageX = drawX + drawW * 0.22f;
        float pageY = drawY + drawH * 0.55f;

        for (int i = 0; i < scenes.length; i++) {
            float btnY = pageY + (2 - i % 3) * (btnHeight + btnGap);
            if (i >= 3) btnY -= 3 * (btnHeight + btnGap);

            boolean hovered = (i == hoveredSceneIndex);
            drawUnifiedButton(pageX, btnY, pageWidth, btnHeight, scenes[i], hovered, false);
        }
    }

    private void drawGamesPage(float w, float h) {
        drawBackButton(50f, h - 120f, 200f, 60f);

        float btnSize = 200f;
        float gap = 50f;
        int count = games.length;
        float totalWidth = count * btnSize + (count - 1) * gap;
        float startX = (w - totalWidth) / 2f;
        float centerY = h * 0.55f;
        float btnY = centerY - btnSize / 2f;

        for (int i = 0; i < games.length; i++) {
            float btnX = startX + i * (btnSize + gap);
            boolean hovered = (i == hoveredGameIndex);
            drawUnifiedGameButton(btnX, btnY, btnSize, games[i], hovered);
        }
    }

    private void drawUnifiedButton(float x, float y, float width, float height, String text, boolean hovered, boolean completed) {
        Color fill, border, textColor;
        float bw;

        if (hovered) {
            fill = new Color(0.85f, 0.65f, 0.35f, 1f);
            border = new Color(1f, 0.95f, 0.6f, 1f);
            textColor = Color.WHITE;
            bw = 5f;
        } else {
            fill = new Color(0.22f, 0.14f, 0.09f, 1f);
            border = new Color(1f, 0.9f, 0.5f, 1f);
            textColor = Color.WHITE;
            bw = 4f;
        }

        game.getBatch().setColor(fill);
        game.getBatch().draw(game.whitePixelTexture, x, y, width, height);

        game.getBatch().setColor(border);
        game.getBatch().draw(game.whitePixelTexture, x, y, width, bw);
        game.getBatch().draw(game.whitePixelTexture, x, y + height - bw, width, bw);
        game.getBatch().draw(game.whitePixelTexture, x, y, bw, height);
        game.getBatch().draw(game.whitePixelTexture, x + width - bw, y, bw, height);

        layout.setText(bookFont, text, textColor, width - 20, Align.center, true);
        bookFont.setColor(textColor);
        bookFont.draw(game.getBatch(), layout, x + 10, y + height * 0.75f);
    }

    private void drawUnifiedGameButton(float x, float y, float size, String text, boolean hovered) {
        Color fill = hovered ? new Color(0.85f, 0.65f, 0.35f, 1f) : new Color(0.22f, 0.14f, 0.09f, 1f);
        Color border = hovered ? new Color(1f, 0.95f, 0.6f, 1f) : new Color(1f, 0.9f, 0.5f, 1f);
        float bw = hovered ? 5f : 4f;

        game.getBatch().setColor(fill);
        game.getBatch().draw(game.whitePixelTexture, x, y, size, size);

        game.getBatch().setColor(border);
        game.getBatch().draw(game.whitePixelTexture, x, y, size, bw);
        game.getBatch().draw(game.whitePixelTexture, x, y + size - bw, size, bw);
        game.getBatch().draw(game.whitePixelTexture, x, y, bw, size);
        game.getBatch().draw(game.whitePixelTexture, x + size - bw, y, bw, size);

        layout.setText(gameFont, text, Color.WHITE, size - 30, Align.center, true);
        gameFont.setColor(Color.WHITE);
        gameFont.draw(game.getBatch(), layout, x + 15, y + size * 0.75f);
    }

    private void drawBackButton(float x, float y, float width, float height) {
        boolean hovered = isOverBackButton(x, y, width, height);
        drawUnifiedButton(x, y, width, height, "НАЗАД", hovered, false);
    }

    private boolean isOverGamesButton(float x, float y, float width, float height) {
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }

    private boolean isOverBackButton(float x, float y, float width, float height) {
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }

    private void updateHover(float w, float h) {
        hoveredSceneIndex = -1;
        hoveredGameIndex = -1;
        gamesButtonHovered = false;

        float mx = Gdx.input.getX();
        float my = h - Gdx.input.getY();

        if (showGamesPage) {
            float btnSize = 200f;
            float gap = 50f;
            int count = games.length;
            float totalWidth = count * btnSize + (count - 1) * gap;
            float startX = (w - totalWidth) / 2f;
            float centerY = h * 0.55f;
            float btnY = centerY - btnSize / 2f;

            for (int i = 0; i < games.length; i++) {
                float btnX = startX + i * (btnSize + gap);
                if (mx >= btnX && mx <= btnX + btnSize &&
                    my >= btnY && my <= btnY + btnSize) {
                    hoveredGameIndex = i;
                    break;
                }
            }
        } else {
            float bookScale = Math.min(w * 0.8f / bookRegion.getRegionWidth(), h * 0.7f / bookRegion.getRegionHeight());
            float drawW = bookRegion.getRegionWidth() * bookScale;
            float drawH = bookRegion.getRegionHeight() * bookScale;
            float drawX = (w - drawW) / 2f;
            float drawY = h * 0.15f;
            float gamesBtnY = drawY + 20f;

            if (isOverGamesButton((w - 350f) / 2f, gamesBtnY, 350f, 65f)) {
                gamesButtonHovered = true;
                return;
            }

            float pageWidth = drawW * 0.55f;
            float btnHeight = 65f;
            float btnGap = 15f;
            float pageX = drawX + drawW * 0.22f;
            float pageY = drawY + drawH * 0.55f;

            for (int i = 0; i < scenes.length; i++) {
                float btnY = pageY + (2 - i % 3) * (btnHeight + btnGap);
                if (i >= 3) btnY -= 3 * (btnHeight + btnGap);

                if (mx >= pageX && mx <= pageX + pageWidth &&
                    my >= btnY && my <= btnY + btnHeight) {
                    hoveredSceneIndex = i;
                    break;
                }
            }
        }
    }

    private void handleClick() {
        if (!Gdx.input.justTouched()) return;

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        if (showGamesPage) {
            if (hoveredGameIndex != -1) {
                switch (hoveredGameIndex) {
                    case 0: game.setScreen(new Level1Screen(game)); break;
                    case 1: game.setScreen(new Level2Screen(game)); break;
                    case 2: game.setScreen(new Level3Screen(game)); break;
                }
            }
            if (isOverBackButton(50f, h - 120f, 200f, 60f)) {
                showGamesPage = false;
            }
        } else {
            if (hoveredSceneIndex != -1 && sceneAvailable[hoveredSceneIndex]) {
                switch (hoveredSceneIndex) {
                    case 0: game.setScreen(new SceneScreen(game)); break;
                    case 1: game.setScreen(new Scene1Screen(game)); break;
                    case 2: game.setScreen(new Scene2Screen(game)); break;
                    case 3: game.setScreen(new Scene3Screen(game)); break;
                    case 4: game.setScreen(new ConclusionScreen(game)); break;
                }
            }
            float bookScale = Math.min(w * 0.8f / bookRegion.getRegionWidth(), h * 0.7f / bookRegion.getRegionHeight());
            float drawW = bookRegion.getRegionWidth() * bookScale;
            float drawH = bookRegion.getRegionHeight() * bookScale;
            float drawX = (w - drawW) / 2f;
            float gamesBtnY = h * 0.15f + 20f;
            if (isOverGamesButton((w - 350f) / 2f, gamesBtnY, 350f, 65f)) {
                showGamesPage = true;
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
        if (titleFont != null) titleFont.dispose();
        if (bookFont != null) bookFont.dispose();
        if (gameFont != null) gameFont.dispose();
        if (bookTexture != null) bookTexture.dispose();
    }
}
