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

public class SceneScreen implements Screen {
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
    private TextureRegion spritePetya;
    private TextureRegion spriteLibrarian;
    private TextureRegion spriteTeacher;
    private TextureRegion spriteMother;
    private Texture searchIconTexture;
    private Texture videoTexture;

    private static final String[] dialogues = {
        "Звонок.",
        "Так, ребята! На следующей неделе — праздник – День города. И я хочу, чтобы каждый из вас подготовил реферат о нашем родном крае.",
        "Эх…",
        "Не надо охать! История — это не скучно! Это как приключение во времени!",
        "Да уж… приключение — делать уроки все выходные.",
        "Александр! Надеюсь, ты услышал?",
        "Да! Я как раз… думал передать атмосферу, ну, всей этой культурной глубины — или как там это называется…",
        "Очень надеюсь, что твой реферат будет не списан из Википедии.",
        "Ну всё, Курск, сейчас не отвертишься — я тебя изучу вдоль и поперёк!",

        "Интересные факты о Курской области",
        "Ну, давай, удиви меня.",
        "«Курск — административный центр Курской области… »",
        "Да ладно, это даже скучнее, чем урок.",
        "«Курская область для чайников»",
        "«Курская область основана в 1032 году… »",
        "То есть город старше почти всех школьных учебников. Впечатляет, но всё равно сухо.",
        "Может, хоть видео найду?",
        "«…расположен на юго-западе Центрального федерального округа…»",
        "Всё, я так больше не могу!",
        "Ну как тут вдохновиться? Разве кто-то может написать хороший реферат, если всё такое… скучное?",
        "Алекс, а может, попробуешь сделать реферат не только по сайтам, а с настоящими книгами?",
        "Настоящими книгами… И что, там мне вообще что-то интересное найдётся?",
        "Кто знает, может, найдётся что-то, чего в интернете нет!",
        "Хм… Я даже не знаю. Может, это всё равно будет скучно.",
        "Именно! Голова отдохнёт от экрана, а заодно узнаешь что-то новое.",
        "А вдруг там совсем ничего интересного нет? И зачем тратить время…",
        "Попробуй хотя бы один том открыть. Не понравится — вернёшься домой.",
        "Ладно… может быть. Но только одну книгу.",
        "Отлично! Вот и начало настоящего приключения.",
        "Ну… посмотрим, что там за “великие открытия”. Только не обещаю, что буду впечатлён.",

        "Библиотека… Ну да, сейчас я найду там суперсекретную энциклопедию, которая всё изменит. Может, ещё и вечный двигатель случайно изобрету.",
        "О, Саша! Куда идёшь? Футбол же скоро!",
        "Не могу, у меня миссия — написать реферат.",
        "Удачи! Только не заблудись среди этих гор бумаги.",
        "Да уж, постараюсь…",

        "Здравствуйте, юноша. Что ищем?",
        "Что-нибудь про Курск. Но чтобы интересно. Не просто «дата, цифра, площадь».",
        "Про Курск? Пойдёмте.",
        "Здесь редкий фонд. Но аккуратно, пожалуйста.",
        "Вот это да…  Не видел такой даже в интернете.",
        "Эй… Что это?",
        "Все истории оживают, стоит только захотеть их узнать по-настоящему.",
        "Что происходит?.."
    };

    private static final String[] speakers = {
        "",
        "Учитель",
        "Класс",
        "Учитель",
        "Алекс",
        "Учитель",
        "Алекс",
        "Учитель",
        "Алекс",

        "Поисковая строка",
        "Алекс",
        "Ответ",
        "Алекс",
        "Поисковая строка",
        "Ответ",
        "Алекс",
        "Алекс",
        "Голос из видео",
        "Алекс",
        "Алекс",
        "Мама",
        "Алекс",
        "Мама",
        "Алекс",
        "Мама",
        "Алекс",
        "Мама",
        "Алекс",
        "Мама",
        "Алекс",

        "Алекс",
        "Петька",
        "Алекс",
        "Петька",
        "Алекс",

        "Библиотекарь",
        "Алекс",
        "Библиотекарь",
        "Библиотекарь",
        "Алекс",
        "Алекс",
        "Таинственный голос",
        "Алекс"
    };

    private TextureRegion getSpriteForSpeaker(String name) {
        switch (name) {
            case "Алекс": return spriteHero;
            case "Петька": return spritePetya;
            case "Библиотекарь": return spriteLibrarian;
            case "Учитель": return spriteTeacher;
            case "Мама": return spriteMother;
            default: return null;
        }
    }

    private TextureRegion getSecondSpriteForSpeaker(String current, String previous) {
        if (previous != null && !previous.isEmpty() && !previous.equals(current)) {
            return getSpriteForSpeaker(previous);
        }
        return null;
    }

    private static final int[] backgroundChangeIndices = {
        0, 5
    };

    private float typeTimer = 0f;
    private float typeSpeed = 0.03f;
    private int visibleChars = 0;
    private String lastSearchQuery = "";

    public SceneScreen(MainGame game) {
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
        spritePetya = new TextureRegion(new Texture("characters/petya.png"));
        spriteLibrarian = new TextureRegion(new Texture("characters/librarian.png"));
        spriteTeacher = new TextureRegion(new Texture("characters/teacher.png"));
        spriteMother = new TextureRegion(new Texture("characters/mother.png"));

        searchIconTexture = new Texture(Gdx.files.internal("search_icon.png"));
        videoTexture = new Texture(Gdx.files.internal("video.jpg"));

        nameFont = generator.generateFont(nameParam);
        generator.dispose();

        layout = new GlyphLayout();
        shapeRenderer = new ShapeRenderer();

        loadBackground("scene0picture1.png");
    }

    private boolean isVideoMoment() {
        return dialogueIndex == 17 || dialogueIndex == 18;
    }

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

        boolean isSearch = "Поисковая строка".equals(speaker);
        boolean isAnswer = "Ответ".equals(speaker);
        boolean isVideoVoice = "Голос из видео".equals(speaker);
        boolean isSearchMode = isSearch || isAnswer || isVideoVoice;
        boolean isAnswerBlock = isAnswer || isVideoVoice;

        if (isSearch) {
            lastSearchQuery = fullText;
        }

        boolean hideCharactersOnLast = (dialogueIndex > 40);
        boolean hideCharacters = !showPanel || hideCharactersOnLast || isSearchMode;

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
        int targetChars = (int) (typeTimer / typeSpeed);
        visibleChars = Math.min(fullText.length(), targetChars);

        String displayText = fullText.substring(0, visibleChars);
        String searchDisplayText = isSearch ? fullText.substring(0, visibleChars) : lastSearchQuery;
        String answerDisplayText = isAnswerBlock ? fullText.substring(0, visibleChars) : "";

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if (isSearchMode) {
            float screenWidth = Gdx.graphics.getWidth();
            float screenHeight = Gdx.graphics.getHeight();

            float barY = screenHeight - browserBarHeight;
            float fieldWidth = screenWidth * 0.8f;
            float fieldX = (screenWidth - fieldWidth) / 2f;
            float fieldY = barY + (browserBarHeight - searchFieldHeight) / 2f;

            shapeRenderer.setColor(browserBarColor);
            shapeRenderer.rect(0, barY, screenWidth, browserBarHeight);

            shapeRenderer.setColor(browserBorderColor);
            shapeRenderer.rect(0, barY, screenWidth, 3);

            shapeRenderer.setColor(searchFieldColor);
            shapeRenderer.rect(fieldX, fieldY, fieldWidth, searchFieldHeight);
            shapeRenderer.setColor(searchFieldBorderColor);
            shapeRenderer.rect(fieldX, fieldY + searchFieldHeight - 2, fieldWidth, 2);

            if (isAnswerBlock) {
                float answerWidth = fieldWidth;
                float answerHeight = isVideoMoment() ? (screenHeight * 0.55f) : 180f;
                float answerX = fieldX;
                float answerY = fieldY - answerHeight - 20f;

                shapeRenderer.setColor(1f, 1f, 1f, 1f);
                shapeRenderer.rect(answerX, answerY, answerWidth, answerHeight);
                shapeRenderer.setColor(browserBorderColor);
                shapeRenderer.rect(answerX, answerY, answerWidth, 2);
            }
        } else if (showPanel) {
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

        if (isSearchMode) {
            float screenWidth = Gdx.graphics.getWidth();
            float screenHeight = Gdx.graphics.getHeight();

            float barY = screenHeight - browserBarHeight;
            float fieldWidth = screenWidth * 0.8f;
            float fieldX = (screenWidth - fieldWidth) / 2f;
            float fieldY = barY + (browserBarHeight - searchFieldHeight) / 2f;

            float iconSize = 50f;
            float iconX = fieldX + 16f;
            float iconY = fieldY + (searchFieldHeight - iconSize) / 2f;

            if (searchIconTexture != null) {
                game.getBatch().draw(searchIconTexture, iconX, iconY, iconSize, iconSize);
            }

            float textOffsetX = (iconX - fieldX) + iconSize + 8f;

            layout.setText(font, searchDisplayText,
                browserTextColor,
                fieldWidth - textOffsetX - searchFieldHorizontalPadding,
                Align.left,
                true);
            font.draw(game.getBatch(), layout,
                fieldX + textOffsetX,
                fieldY + searchFieldHeight - searchFieldVerticalPadding);

            if (isAnswerBlock) {
                if (isVideoMoment()) {
                    float answerWidth = fieldWidth;
                    float answerHeight = screenHeight * 0.55f;
                    float answerX = fieldX;
                    float answerY = fieldY - answerHeight - 20f;

                    if (videoTexture != null) {
                        float paddingSides = 24f;
                        float paddingTop = 24f;
                        float paddingBottom = 24f;
                        float paddingBetween = 16f;

                        float texW = videoTexture.getWidth();
                        float texH = videoTexture.getHeight();

                        float maxVideoWidth = answerWidth - 2 * paddingSides;
                        float maxVideoHeight = answerHeight - paddingTop - paddingBottom - 80f;

                        float scale = Math.min(maxVideoWidth / texW, maxVideoHeight / texH);
                        float drawW = texW * scale;
                        float drawH = texH * scale;

                        float drawX = answerX + (answerWidth - drawW) / 2f;
                        float drawY = answerY + answerHeight - paddingTop - drawH;

                        game.getBatch().draw(videoTexture, drawX, drawY, drawW, drawH);

                        float textTopY = drawY - paddingBetween;
                        float textAreaWidth = answerWidth - 2 * paddingSides;

                        layout.setText(font, answerDisplayText,
                            browserTextColor,
                            textAreaWidth,
                            Align.left,
                            true);
                        font.draw(game.getBatch(), layout,
                            answerX + paddingSides,
                            textTopY);
                    } else {
                        layout.setText(font, answerDisplayText,
                            browserTextColor,
                            answerWidth - 40f,
                            Align.left,
                            true);
                        font.draw(game.getBatch(), layout,
                            answerX + 20f,
                            answerY + answerHeight - 20f);
                    }
                } else {
                    float answerWidth = fieldWidth;
                    float answerHeight = 180f;
                    float answerX = fieldX;
                    float answerY = fieldY - answerHeight - 20f;

                    layout.setText(font, answerDisplayText,
                        browserTextColor,
                        answerWidth - 40f,
                        Align.left,
                        true);
                    font.draw(game.getBatch(), layout,
                        answerX + 20f,
                        answerY + answerHeight - 20f);
                }
            }
        } else if (showPanel) {
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
                if (!isSearchMode) {
                    showPanel = !showPanel;
                    if (showPanel) {
                        visibleChars = 0;
                        typeTimer = 0f;
                    }
                }
            } else if (x < Gdx.graphics.getWidth() / 2f) {
                if (dialogueIndex > 0) {
                    dialogueIndex--;
                    visibleChars = 0;
                    typeTimer = 0f;
                    updateBackground();
                }
            } else {
                if (dialogueIndex < dialogues.length - 1) {
                    dialogueIndex++;
                    visibleChars = 0;
                    typeTimer = 0f;
                    updateBackground();
                } else {
                    game.setIntroCompleted(true);
                    Scene1Screen nextScene = new Scene1Screen(game);
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
        if (dialogueIndex <= 8) {
            loadBackground("scene0picture1.png");
        } else if (dialogueIndex <= 29) {
            loadBackground("scene0picture2.png");
        } else if (dialogueIndex <= 34) {
            loadBackground("scene0picture3.png");
        } else if (dialogueIndex <= 39) {
            loadBackground("scene0picture4.png");
        } else if (dialogueIndex <= 40) {
            loadBackground("scene0picture5.png");
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
        if (searchIconTexture != null) searchIconTexture.dispose();
        if (videoTexture != null) videoTexture.dispose();
        if (spriteHero != null) spriteHero.getTexture().dispose();
        if (spritePetya != null) spritePetya.getTexture().dispose();
        if (spriteLibrarian != null) spriteLibrarian.getTexture().dispose();
        if (spriteTeacher != null) spriteTeacher.getTexture().dispose();
        if (spriteMother != null) spriteMother.getTexture().dispose();
    }

    private final Color browserBarColor = new Color(0.95f, 0.95f, 0.97f, 1f);
    private final Color browserBorderColor = new Color(0.75f, 0.75f, 0.8f, 1f);
    private final Color searchFieldColor = new Color(1f, 1f, 1f, 1f);
    private final Color searchFieldBorderColor = new Color(0.8f, 0.8f, 0.85f, 1f);
    private final Color browserTextColor = new Color(0.1f, 0.1f, 0.1f, 1f);

    private float browserBarHeight = 90f;
    private float searchFieldHeight = 80f;
    private float searchFieldHorizontalPadding = 24f;
    private float searchFieldVerticalPadding = 16f;
}
