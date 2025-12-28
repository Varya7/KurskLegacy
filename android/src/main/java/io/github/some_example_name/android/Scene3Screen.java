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

public class Scene3Screen implements Screen {
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
    private TextureRegion spriteMerchant;
    private TextureRegion spriteAvdotia;
    private TextureRegion spriteBarker;
    private boolean gameFromLevel3 = false;

    private static final String[] dialogues = {

        "Кажется, путешествие продолжается.",
        "О, крепость уже достроили!",
        "Ох, день добрый, дорогой гость! Вид у тебя странный, но время такое, что всяк на ярмарку спешит. Откуда будешь?",
        "Скажем так, дорога была длинной. Похоже, как раз на ярмарку попал?",
        "Точно подметил. Весь город на ногах: кто продаёт, кто покупает, кто просто поглазеть пришёл.",
        "А я – Авдотья, хозяйка этого двора.",
        "А я Алекс… Александр, то есть. А вы что-то к ярмарке готовите?",
        "Ещё бы! Ткани у нас добрые, да утварь крепкая. Возьмёшь раз — и долгие годы в доме служит.",
        "Авдотья! Опять ты гостей у ворот задерживаешь?",
        "Семён, не ворчи. Человек ещё пыль с дороги не отряхнул.",
        "Издалече, говоришь? Ну, гостей мы любим. Только нынче не до разговоров — ярмарка на носу, а дел невпроворот.",
        "Если нужно, могу помочь, всё равно планов никаких нет.",
        "Вот это нам сейчас и надо. Товар свой я от прилавка не отпущу, Авдотья от двора не отойдёт, а закупки делать кому-то нужно.",
        "Верно, а то у нас и к столу поставить нечего.",
        "Ступай, да не хватай первое попавшееся, приглядывайся к товарам.",

        "Вот это масштаб! В прошлый раз тут брёвна таскали, а теперь целый фестиваль!",
        "Подходи, честной народ! Всё найдёте: мёд сладкий да железо крепкое!",
        "И вправду всё подряд: еда, ткани, посуда…",
        "Эй, паренёк, впервые у нас? Гляди вокруг: куда ни посмотри — Курск теперь центр всей округи. Тут кто не торгует, тот покупает.",
        "То есть, все на ярмарку едут?",
        "А как же! Кто в путь собирается — здесь запасётся, кто товар везёт — тут сбыть старается.",
        "Вот это да!",
        "А в учебнике, наверное, просто написано «Курск стал важным торговым центром».",
        "Ладно, меня ещё покупки ждут. Пора приступать!",

        "Фух, кажется, всё купил.",
        "Дай-ка гляну! Яблоки наливные, пироги румяные, варенье ягодное… Вот это размах! Молодец!",
        "Во как! Теперь хоть царский приём устроить можем!",
        "Садись к нам за стол, путешественник, отдохнёшь.",
        "Спасибо, не откажусь. Но потом сразу в дорогу.",
        "Хочется успеть вернуться туда, где всё началось, и рассказать, какой Курск я увидел своими глазами.",
        "Теперь это место для меня полно истории!"
    };

    private static final String[] speakers = {

        "Алекс", "Алекс", "Хозяйка", "Алекс", "Авдотья", "Авдотья", "Алекс", "Авдотья",
        "Купец", "Авдотья", "Семён", "Алекс", "Семён", "Авдотья", "Семён",

        "Алекс", "Зазывала", "Алекс", "Зазывала", "Алекс", "Зазывала", "Алекс", "Алекс", "Алекс",

        "Алекс", "Авдотья", "Семён", "Авдотья", "Алекс", "Алекс", "Алекс"
    };

    private TextureRegion getSpriteForSpeaker(String name) {
        switch (name) {
            case "Алекс": return spriteHero;
            case "Купец":
            case "Семён": return spriteMerchant;
            case "Хозяйка":
            case "Авдотья": return spriteAvdotia;
            case "Зазывала": return spriteBarker;
            default: return null;
        }
    }

    private TextureRegion getSecondSpriteForSpeaker(String current, String previous) {
        if (previous != null && !previous.isEmpty() && !previous.equals(current)) {
            return getSpriteForSpeaker(previous);
        }
        return null;
    }

    public Scene3Screen(MainGame game) {
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
                "0123456789.,!?-—–«»\"':;()…  ";

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
                "0123456789.,!?-—–«»\"':;()…  ";

        spriteHero = new TextureRegion(new Texture("characters/hero.png"));
        spriteMerchant = new TextureRegion(new Texture("characters/merchant.png"));
        spriteAvdotia = new TextureRegion(new Texture("characters/avdotia.png"));
        spriteBarker = new TextureRegion(new Texture("characters/barker.png"));

        nameFont = generator.generateFont(nameParam);
        generator.dispose();

        layout = new GlyphLayout();
        shapeRenderer = new ShapeRenderer();

        loadBackground("scene3picture1.png");
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
                    gameFromLevel3 = (dialogueIndex >= 24);
                    updateBackground();
                }
            } else {
                if (!gameFromLevel3 && dialogueIndex < 23) {
                    dialogueIndex++;
                    visibleChars = 0;
                    typeTimer = 0f;
                    updateBackground();
                } else if (gameFromLevel3 && dialogueIndex < dialogues.length - 1) {
                    dialogueIndex++;
                    visibleChars = 0;
                    typeTimer = 0f;
                    updateBackground();
                } else if (dialogueIndex == 23) {

                    game.setScreen(new Level3Screen(game, this));
                } else if (dialogueIndex == dialogues.length - 1) {
                    ConclusionScreen nextScene = new ConclusionScreen(game);
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
        if (dialogueIndex <= 15) {
            loadBackground("scene3picture1.png");
        } else if (dialogueIndex <= 22) {
            loadBackground("scene3picture2.png");
        } else {
            loadBackground("scene3picture1.png");
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}


    public void setDialogueIndex(int index) {
        this.dialogueIndex = index;
        this.visibleChars = 0;
        this.typeTimer = 0f;
        this.gameFromLevel3 = (index >= 24);
    }

    public void setShowPanel(boolean show) {
        this.showPanel = show;
    }


    @Override
    public void dispose() {
        if (background != null) background.dispose();
        if (font != null) font.dispose();
        if (nameFont != null) nameFont.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (spriteHero != null) spriteHero.getTexture().dispose();
        if (spriteMerchant != null) spriteMerchant.getTexture().dispose();
        if (spriteAvdotia != null) spriteAvdotia.getTexture().dispose();
        if (spriteBarker != null) spriteBarker.getTexture().dispose();
    }
}
