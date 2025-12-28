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

public class Scene1Screen implements Screen {
    private final MainGame game;
    private BitmapFont font;
    private GlyphLayout layout;
    private Texture background;
    private ShapeRenderer shapeRenderer;
    private int dialogueIndex = 0;
    private boolean showPanel = true;

    private BitmapFont nameFont;
    private final Color panelColor = new Color(0.25f, 0.15f, 0.07f, 0.82f);
    private final Color panelBorderColor = new Color(0.7f, 0.55f, 0.25f, 0.9f);
    private final Color namePanelColor = new Color(0.32f, 0.22f, 0.10f, 0.95f);
    private final Color nameTextColor = new Color(1f, 0.88f, 0.45f, 1f);

    private TextureRegion spriteHero;
    private TextureRegion spriteIlia;
    public static int pendingDialogueIndex = -1;
    public static int pendingBackgroundIndex = 1;

    private static final String[] dialogues = {
        "Что… Где я?",
        "Это что, Курск? Только… Древний?",
        "Подождите, я же был в библиотеке? Или это сон?",
        "Нет, всё кажется слишком реальным.",
        "Неужели я правда попал в прошлое?",
        "Круто… Но как это возможно?",
        "Ладно, панику оставим на потом. Сначала надо разобраться, где я и кто здесь живёт.",
        "Жалко, интернет не ловит…",
        "Кажется, в конце улицы кто-то работает.",
        "Надо попробовать спросить, что происходит.",

        "Эм… Здравствуйте?",
        "Здрав будь, странник. Речь твоя чудна, одежда невидана. Кто будешь?",
        "Я… Путешественник. Можно сказать, пришёл издалека. Очень издалека.",
        "Издалека, глаголиши? Ну, коли путь твой длинен, отдохни у очага. Я Илья, гончар. Посуду делаю — чтоб людям служила.",
        "То есть всё своими руками? Без машин, без электричества?",
        "Без чего-чего? Чудные слова ты изрекаеши, добрый человек! Но коли руки умелы, то всё возможно.",
        "Да, пожалуй, ты прав.",
        "Тут столько всего: кувшины, кружки… Это всё ты сам сделал?",
        "Не всё. Я лишь помогаю. Настоящий мастер у нас староста Гаврила — он сейчас на торгу.",
        "Значит, ты его ученик?",
        "Да, подмастерье. С утра тружусь у круга, к вечеру у печи.",
        "Звучит серьёзно.",
        "А можно поближе посмотреть, что вы делаете?",
        "Ближе поглядеть?",
        "Да, мне правда интересно, какая посуда была в то вр… У вас, то есть.",
        "Конечно! Можешь заодно помочь выставить на полки новую посуду. Но поглядывай внимательно, чтобы мусор иной раз не попал.",
        "Да, буду рад помочь!",

        "Во как! Гляди‑ка, всё по местам, ни осколка лишнего. Быстро учишься, путник!",
        "Интересно, вроде простое дело, а чувствуешь, будто что-то настоящее создаёшь.",
        "Мастерство не в руках, а в сердце.",
        "Да ремесло ­– не лишь работа, а разговор человека с собой самим.",
        "Спасибо, Илья. Кажется, я понял.",
        "Вот и хорошо. Помни мои слова, путник издалёка.",
        "Путь твой, видно, дальний. Иди с миром.",
        "Спасибо, Илья. За всё. И за мудрость тоже.",
        "Глина ждёт, да и день на исходе. Бывай здоров, чужеземный гость.",
        "Мастерство не в руках, а в сердце… Надо запомнить."
    };

    private static final String[] speakers = {
        "Алекс", "Алекс", "Алекс", "Алекс", "Алекс", "Алекс", "Алекс", "Алекс", "Алекс", "Алекс",

        "Алекс", "Незнакомец", "Алекс", "Илья", "Алекс", "Илья", "Алекс", "Алекс", "Илья",
        "Алекс", "Илья", "Алекс", "Алекс", "Илья", "Алекс", "Илья", "Алекс",

        "Илья", "Алекс", "Илья", "Илья", "Алекс", "Илья", "Илья", "Алекс", "Илья", "Алекс"
    };

    private TextureRegion getSpriteForSpeaker(String name) {
        switch (name) {
            case "Алекс": return spriteHero;
            case "Илья":
            case "Незнакомец": return spriteIlia;
            default: return null;
        }
    }

    private TextureRegion getSecondSpriteForSpeaker(String current, String previous) {
        if (previous != null && !previous.isEmpty() && !previous.equals(current)) {
            return getSpriteForSpeaker(previous);
        }
        return null;
    }

    public Scene1Screen(MainGame game) {
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
                "0123456789.,!?-—–…«»\"':;()…  ";

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
        spriteIlia = new TextureRegion(new Texture("characters/ilia.png"));

        nameFont = generator.generateFont(nameParam);
        generator.dispose();

        layout = new GlyphLayout();
        shapeRenderer = new ShapeRenderer();


        if (pendingDialogueIndex != -1) {
            setDialogueIndex(pendingDialogueIndex);
            pendingDialogueIndex = -1;

            loadBackground(pendingBackgroundIndex == 2 ? "scene1picture2.png" : "scene1picture1.png");
            pendingBackgroundIndex = 1;
            return;
        }

        loadBackground("scene1picture1.png");
    }

    private float typeTimer = 0f;
    private float typeSpeed = 0.03f;
    private int visibleChars = -1;

    @Override
    public void render(float delta) {
        game.getBatch().setColor(Color.WHITE);
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
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
                    updateBackground();
                }
            } else {
                if (dialogueIndex < dialogues.length - 1) {
                    if (dialogueIndex == 26) {
                        Scene1Screen.pendingDialogueIndex = 27;
                        Scene1Screen.pendingBackgroundIndex = 2;
                        game.setScreen(new Level1Screen(game, false));
                    } else {
                        dialogueIndex++;
                        visibleChars = 0;
                        typeTimer = 0f;
                        updateBackground();
                    }
                } else {
                    Scene2Screen nextScene = new Scene2Screen(game);
                    game.setScreen(new BookTransitionScreen(game, nextScene));
                }
            }
        }

    }

    private void loadBackground(String fileName) {
        if (background != null) background.dispose();
        background = new Texture(Gdx.files.internal(fileName));
    }

    private void updateBackground() {
        if (dialogueIndex <= 16) {
            loadBackground("scene1picture1.png");
        } else {
            loadBackground("scene1picture2.png");
        }
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
        if (spriteIlia != null) spriteIlia.getTexture().dispose();
    }

    public void setDialogueIndex(int index) {
        this.dialogueIndex = index;
        this.visibleChars = 0;
        this.typeTimer = 0f;
        updateBackground();
        showPanel = true;
    }

}
