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

public class Scene2Screen implements Screen {
    private final MainGame game;
    private BitmapFont font;
    private GlyphLayout layout;
    private Texture background;
    private ShapeRenderer shapeRenderer;
    private int dialogueIndex = 0;
    private boolean showPanel = true;

    private boolean gameFromLevel2 = false;
    private BitmapFont nameFont;
    private final Color panelColor = new Color(0.25f, 0.15f, 0.07f, 0.82f);
    private final Color panelBorderColor = new Color(0.7f, 0.55f, 0.25f, 0.9f);
    private final Color namePanelColor = new Color(0.32f, 0.22f, 0.10f, 0.95f);
    private final Color nameTextColor = new Color(1f, 0.88f, 0.45f, 1f);

    private TextureRegion spriteHero;
    private TextureRegion spriteVoivode;
    private TextureRegion spriteWriter;

    private static final String[] dialogues = {
        "Где я теперь? Кажется, снова Курск, но теперь всё другое.",
        "Эй, новенький! Не стой, как изваяние!",
        "За дело! Царский указ — стену возвести до морозов. Брёвна клади ровно, чтоб крепость стояла на века.",
        "Э-э, да. Я попробую. Надо взять инструмент.",

        "По царскому указу, заложена крепость Курская, чтоб стояла она заслоном южным и хранителем земли русской…",
        "“Заложена крепость Курская”… Выходит, я и правда на строительстве.",
        "А кто говорил? Перо, бумага, серьёзный вид...",
        "Похоже, какой-то местный чиновник? Хотя нет, выглядит, скорее, как учёный.",

        "Учёный, говоришь? Нет, не ученый я — летописец.",
        "Не мудростью славен, а памятью служу. Что вижу — то в строки кладу, чтоб не кануло во тьму лет.",
        "Значит, вы записываете всё, что происходит? Звучит интересно.",
        "Всё, что стоит помнить. Не ради громких слов, а ради правды.",
        "Сегодня дерево укладывают, завтра тут будет крепость. А через сто лет — город, торг и колокольный звон.",
        "Выходит, и сейчас, прямо на глазах, рождается история?",
        "История рождается каждый день, сын мой.",
        "Гляди, они не знают, что великие. Им кажется — просто трудятся. А истинное величие — в тех, кто строит, а не рушит.",
        "Красиво сказано. Тогда и я помогу строить. Хоть немного — но заодно почувствую, каким был Курск.",
        "Надо торопиться. История сама себя не построит.",

        "Вот так! Будет стена стоять крепко. Хорошо потрудились!",
        "Здорово! Даже не верится, что я тоже приложил к этому руку.",
        "Надеюсь, премий в те времена не было, а то я даже не оформился.",
        "В какие, те, времена? А награда тебе — доброе слово да горсть каши.",
        "Так и запишем: «Возводили стену всем миром, с усердием и верой в завтрашний день.»"
    };

    private static final String[] speakers = {
        "Алекс", "Воевода", "Воевода", "Алекс",

        "Кто-то", "Алекс", "Алекс", "Алекс",

        "Летописец", "Летописец", "Алекс", "Летописец", "Летописец",
        "Алекс", "Летописец", "Летописец", "Алекс", "Алекс",

        "Воевода", "Алекс", "Алекс", "Воевода", "Летописец"
    };

    private TextureRegion getSpriteForSpeaker(String name) {
        switch (name) {
            case "Алекс": return spriteHero;
            case "Воевода": return spriteVoivode;
            case "Летописец":
            case "Кто-то":
            case "writer": return spriteWriter;
            default: return null;
        }
    }


    public Scene2Screen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Regular.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 48;
        parameter.color = new Color(1f, 0.96f, 0.85f, 1f);
        parameter.borderWidth = 2f;
        parameter.borderColor = new Color(0.1f, 0.05f, 0.02f, 0.9f);
        parameter.shadowOffsetX = 2;
        parameter.shadowOffsetY = 2;
        parameter.characters =
            "абвгдеёжзийклмнопрстуфхцчшщъыьэюя" +
                "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" +
                "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "0123456789.,!?-—–«»\"':;()… ";

        font = generator.generateFont(parameter);

        FreeTypeFontGenerator.FreeTypeFontParameter nameParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        nameParam.size = 58;
        nameParam.color = nameTextColor;
        nameParam.borderWidth = 2f;
        nameParam.borderColor = new Color(0.1f, 0.05f, 0.02f, 1f);
        nameParam.shadowOffsetX = 2;
        nameParam.shadowOffsetY = 2;
        nameParam.characters =
            "абвгдеёжзийклмнопрстуфхцчшщъыьэюя" +
                "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" +
                "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "0123456789.,!?-—–«»\"':;()… ";

        spriteHero = new TextureRegion(new Texture("characters/hero.png"));
        spriteVoivode = new TextureRegion(new Texture("characters/soldier.png"));
        spriteWriter = new TextureRegion(new Texture("characters/writer.png"));

        nameFont = generator.generateFont(nameParam);
        generator.dispose();

        layout = new GlyphLayout();
        shapeRenderer = new ShapeRenderer();

        loadBackground("scene2picture1.png");
    }

    private float typeTimer = 0f;
    private float typeSpeed = 0.03f;
    private int visibleChars = -1;

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.86f, 0.78f, 0.62f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getBatch().begin();
        if (background != null) {
            game.getBatch().draw(background, 0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        game.getBatch().end();

        String speaker = (dialogueIndex < speakers.length) ? speakers[dialogueIndex] : "";
        String fullText = dialogues[dialogueIndex];

        boolean hideCharacters = !showPanel || dialogueIndex > dialogues.length - 1;

        TextureRegion leftSprite = null;
        TextureRegion rightSprite = null;

        if (!hideCharacters) {
            String prevSpeaker = dialogueIndex > 0 ? speakers[dialogueIndex - 1] : "";
            TextureRegion currentSprite = getSpriteForSpeaker(speaker);
            TextureRegion prevSprite = getSpriteForSpeaker(prevSpeaker);

            if ("Алекс".equals(speaker) || "Алекс".equals(prevSpeaker)) {
                leftSprite = getSpriteForSpeaker("Алекс");

                if (!"Алекс".equals(speaker) && currentSprite != null) {
                    rightSprite = currentSprite;
                } else if (!"Алекс".equals(prevSpeaker) && prevSprite != null) {
                    rightSprite = prevSprite;
                }
            } else {
                leftSprite = currentSprite;
                if (prevSprite != null && !prevSpeaker.isEmpty() && !prevSpeaker.equals(speaker)) {
                    rightSprite = prevSprite;
                }
            }
        }

        if (leftSprite != null || rightSprite != null) {
            game.getBatch().begin();

            float scale = 0.7f;
            float y = 150;

            if (leftSprite != null) {
                float w = leftSprite.getRegionWidth() * scale;
                float h = leftSprite.getRegionHeight() * scale;
                float x = 40;
                game.getBatch().draw(leftSprite, x, y, w, h);
            }

            if (rightSprite != null) {
                float w = rightSprite.getRegionWidth() * scale;
                float h = rightSprite.getRegionHeight() * scale;
                float x = Gdx.graphics.getWidth() - w - 40;
                game.getBatch().draw(rightSprite, x, y, w, h);
            }

            game.getBatch().end();
        }

        typeTimer += delta;
        int target = (int) (typeTimer / typeSpeed);
        if (target > visibleChars) {
            visibleChars = Math.min(fullText.length(), target);
        }
        String displayText = fullText.substring(0, visibleChars);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if (showPanel) {
            float panelHeight = 180;
            float panelY = 20;

            shapeRenderer.setColor(panelBorderColor);
            shapeRenderer.rect(20, panelY - 10, Gdx.graphics.getWidth() - 40, panelHeight + 20);
            shapeRenderer.setColor(panelColor);
            shapeRenderer.rect(30, panelY, Gdx.graphics.getWidth() - 60, panelHeight);

            if (!speaker.isEmpty()) {
                float nameHeight = 60;
                float nameWidth = Gdx.graphics.getWidth() * 0.55f;
                float nameX = 30;
                float nameY = panelY + panelHeight + 10;

                shapeRenderer.setColor(panelBorderColor);
                shapeRenderer.rect(nameX - 5, nameY - 5, nameWidth + 10, nameHeight + 10);
                shapeRenderer.setColor(namePanelColor);
                shapeRenderer.rect(nameX, nameY, nameWidth, nameHeight);
            }
        }

        shapeRenderer.end();

        game.getBatch().begin();

        if (showPanel) {
            float panelHeight = 180;
            float panelY = 20;

            if (!speaker.isEmpty()) {
                float nameHeight = 60;
                float nameWidth = Gdx.graphics.getWidth() * 0.55f;
                float nameX = 30;
                float nameY = panelY + panelHeight + 10;

                layout.setText(nameFont, speaker, nameTextColor,
                    nameWidth - 20, Align.left, true);
                nameFont.draw(game.getBatch(), layout,
                    nameX + 20, nameY + nameHeight - 5);
            }

            layout.setText(font, displayText,
                new Color(1f, 0.96f, 0.85f, 1f),
                Gdx.graphics.getWidth() - 100,
                Align.left,
                true);
            font.draw(game.getBatch(), layout,
                50, panelY + panelHeight - 40);
        }

        game.getBatch().end();

        if (Gdx.input.justTouched()) {
            float x = Gdx.input.getX();
            float y = Gdx.input.getY();

            if (x < Gdx.graphics.getWidth() / 4f && y < Gdx.graphics.getHeight() / 4f) {
                showPanel = !showPanel;
            } else if (x < Gdx.graphics.getWidth() / 2f) {
               if (dialogueIndex > 0) {
                    dialogueIndex--;
                    visibleChars = 0;
                    typeTimer = 0f;
                    gameFromLevel2 = (dialogueIndex >= 18);
                    updateBackground();
                }
            } else {
                if (!gameFromLevel2 && dialogueIndex < 17) {
                    dialogueIndex++;
                    visibleChars = 0;
                    typeTimer = 0f;
                    updateBackground();
                } else if (gameFromLevel2 && dialogueIndex < dialogues.length - 1) {
                    dialogueIndex++;
                    visibleChars = 0;
                    typeTimer = 0f;
                    updateBackground();
                } else if (dialogueIndex == 17) {
                    game.setScreen(new Level2Screen(game, this));
                } else if (dialogueIndex == dialogues.length - 1) {
                    Scene3Screen nextScene = new Scene3Screen(game);
                    game.setScreen(new BookTransitionScreen(game, nextScene));
                }

            }
        }


    }

    public void setDialogueIndex(int index) {
        this.dialogueIndex = index;
        this.visibleChars = 0;
        this.typeTimer = 0f;
        this.gameFromLevel2 = (index >= 18);
    }


    public void setShowPanel(boolean show) {
        this.showPanel = show;
    }


    private void loadBackground(String fileName) {
        if (background != null) background.dispose();
        background = new Texture(Gdx.files.internal(fileName));
    }

    private void updateBackground() {
        loadBackground("scene2picture1.png");
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (background != null) background.dispose();
        if (font != null) font.dispose();
        if (nameFont != null) nameFont.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (spriteHero != null) spriteHero.getTexture().dispose();
        if (spriteVoivode != null) spriteVoivode.getTexture().dispose();
        if (spriteWriter != null) spriteWriter.getTexture().dispose();
    }
}
