package io.github.some_example_name.android;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;

public class ConclusionScreen implements Screen {
    private final MainGame game;
    private BitmapFont font;
    private BitmapFont finalFont;
    private BitmapFont nameFont;
    private GlyphLayout layout;
    private Texture background;
    private Texture bookTexture;
    private TextureRegion bookRegion;
    private ShapeRenderer shapeRenderer;
    private int dialogueIndex = 0;
    private boolean showPanel = true;

    private final Color panelColor = new Color(0.25f, 0.15f, 0.07f, 0.82f);
    private final Color panelBorderColor = new Color(0.7f, 0.55f, 0.25f, 0.9f);
    private final Color namePanelColor = new Color(0.32f, 0.22f, 0.10f, 0.95f);
    private final Color nameTextColor = new Color(1f, 0.88f, 0.45f, 1f);

    private static final String[] dialogues = {
        "Ой, похоже, снова библиотека.",
        "Ого, да здесь же написано всё, что со мной случилось!",
        "Но теперь я знаю, сколько историй стоит за этими страницами.",
        "Ой, тут ещё что-то написано.",
        "Продолжение истории – в твоих руках."
    };

    public ConclusionScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Regular.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 52;
        parameter.color = new Color(1f, 0.96f, 0.85f, 1f);
        parameter.borderWidth = 2f;
        parameter.borderColor = new Color(0.1f, 0.05f, 0.02f, 0.9f);
        parameter.shadowOffsetX = 2;
        parameter.shadowOffsetY = 2;
        parameter.characters =
            "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ0123456789.,!?-—–«»\"':;()…  ";
        font = generator.generateFont(parameter);

        FreeTypeFontGenerator.FreeTypeFontParameter finalParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        finalParam.size = 64;
        finalParam.color = new Color(1f, 0.95f, 0.75f, 1f);
        finalParam.borderWidth = 3f;
        finalParam.borderColor = new Color(0.15f, 0.08f, 0.03f, 0.95f);
        finalParam.shadowOffsetX = 3;
        finalParam.shadowOffsetY = 3;
        finalParam.characters = parameter.characters;
        finalFont = generator.generateFont(finalParam);

        FreeTypeFontGenerator.FreeTypeFontParameter nameParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        nameParam.size = 58;
        nameParam.color = nameTextColor;
        nameParam.borderWidth = 2f;
        nameParam.borderColor = new Color(0.1f, 0.05f, 0.02f, 1f);
        nameParam.shadowOffsetX = 2;
        nameParam.shadowOffsetY = 2;
        nameParam.characters = parameter.characters;
        nameFont = generator.generateFont(nameParam);

        generator.dispose();

        layout = new GlyphLayout();
        shapeRenderer = new ShapeRenderer();

        background = new Texture(Gdx.files.internal("scene0picture5.png"));
        bookTexture = new Texture(Gdx.files.internal("book1.png"));
        bookRegion = new TextureRegion(bookTexture);
    }

    private float typeTimer = 0f;
    private float typeSpeed = 0.03f;
    private int visibleChars = -1;

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.86f, 0.78f, 0.62f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        String fullText = dialogues[dialogueIndex];
        typeTimer += delta;
        int target = (int) (typeTimer / typeSpeed);
        if (target > visibleChars) {
            visibleChars = Math.min(fullText.length(), target);
        }
        String displayText = fullText.substring(0, visibleChars);

        if (dialogueIndex < dialogues.length - 1 && showPanel) {
            float bookScale = Math.min(w / bookRegion.getRegionWidth(), h / bookRegion.getRegionHeight());
            float drawW = bookRegion.getRegionWidth() * bookScale;
            float drawH = bookRegion.getRegionHeight() * bookScale;
            float drawX = (w - drawW) / 2f;
            float drawY = (h - drawH) / 2f;

            game.getBatch().begin();
            if (dialogueIndex == 0) {
                if (background != null) game.getBatch().draw(background, 0, 0, w, h);
            } else {
                game.getBatch().setColor(Color.WHITE);
                game.getBatch().draw(bookRegion, drawX, drawY, drawW, drawH);
            }
            game.getBatch().end();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            float nameHeight = 60;
            float nameWidth = w * 0.55f;
            float nameX = 30;
            float nameY = 240;
            shapeRenderer.setColor(panelBorderColor);
            shapeRenderer.rect(nameX - 5, nameY - 5, nameWidth + 10, nameHeight + 10);
            shapeRenderer.setColor(namePanelColor);
            shapeRenderer.rect(nameX, nameY, nameWidth, nameHeight);

            float panelHeight = 200;
            float panelY = 20;
            shapeRenderer.setColor(panelBorderColor);
            shapeRenderer.rect(20, panelY - 10, w - 40, panelHeight + 20);
            shapeRenderer.setColor(panelColor);
            shapeRenderer.rect(30, panelY, w - 60, panelHeight);

            shapeRenderer.end();

            game.getBatch().begin();

            layout.setText(nameFont, "Алекс", nameTextColor, nameWidth - 20, Align.left, true);
            nameFont.draw(game.getBatch(), layout, nameX + 20, nameY + nameHeight - 5);

            layout.setText(font, displayText, new Color(1f, 0.96f, 0.85f, 1f), w - 100, Align.left, true);
            font.draw(game.getBatch(), layout, 50, panelY + panelHeight - 40);

            game.getBatch().end();
        }
        else if (dialogueIndex == dialogues.length - 1) {
            float bookScale = Math.min(w / bookRegion.getRegionWidth(), h / bookRegion.getRegionHeight());
            float drawW = bookRegion.getRegionWidth() * bookScale;
            float drawH = bookRegion.getRegionHeight() * bookScale;
            float drawX = (w - drawW) / 2f;
            float drawY = (h - drawH) / 2f;

            game.getBatch().begin();
            game.getBatch().setColor(Color.WHITE);
            game.getBatch().draw(bookRegion, drawX, drawY, drawW, drawH);
            game.getBatch().end();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            float textWidth = w * 0.8f;
            float textHeight = 120;
            float textX = (w - textWidth) / 2f;
            float textY = h * 0.4f;
            shapeRenderer.setColor(new Color(0.1f, 0.05f, 0.02f, 0.7f));
            shapeRenderer.rect(textX - 20, textY - 10, textWidth + 40, textHeight + 20);
            shapeRenderer.end();

            game.getBatch().begin();
            layout.setText(finalFont, fullText, new Color(1f, 0.95f, 0.75f, 1f),
                textWidth, Align.center, true);
            finalFont.draw(game.getBatch(), layout, textX + 20, textY + textHeight * 0.7f);
            game.getBatch().end();
        }

        if (Gdx.input.justTouched()) {
            float x = Gdx.input.getX();
            float y = Gdx.input.getY();

            if (x < w / 4f && y < h / 4f) {
                showPanel = !showPanel;
            } else if (x < w / 2f) {
                if (dialogueIndex > 0) {
                    dialogueIndex--;
                    visibleChars = 0;
                    typeTimer = 0f;
                }
            } else {
                if (dialogueIndex < dialogues.length - 1) {
                    dialogueIndex++;
                    visibleChars = 0;
                    typeTimer = 0f;
                } else {
                    game.setScreen(new LevelMenuScreen(game));
                }
            }
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (background != null) background.dispose();
        if (bookTexture != null) bookTexture.dispose();
        if (font != null) font.dispose();
        if (finalFont != null) finalFont.dispose();
        if (nameFont != null) nameFont.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
    }
}
