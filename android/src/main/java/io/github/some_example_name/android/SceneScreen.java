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
    private final Color panelColor = new Color(0.25f, 0.15f, 0.07f, 0.82f); // шоколадный
    private final Color panelBorderColor = new Color(0.7f, 0.55f, 0.25f, 0.9f); // золотистый
    private final Color namePanelColor = new Color(0.32f, 0.22f, 0.10f, 0.95f);
    private final Color nameTextColor = new Color(1f, 0.88f, 0.45f, 1f);

    private TextureRegion[] characterSprites;
    private float characterScale = 0.7f;

    private TextureRegion spriteHero;
    private TextureRegion spritePetya;
    private TextureRegion spriteLibrarian;
    private TextureRegion spriteTeacher;
    private TextureRegion spriteMother;


    private static final String[] dialogues = {
        "*звонок*",
        "Так, ребята! На следующей неделе — праздник – День города. И я хочу, чтобы каждый из вас подготовил реферат о нашем родном крае.",
        "(Класс дружно вздыхает.)",
        "Не надо охать! История — это не скучно! Это как приключение во времени!",
        "(Алекс, сидящий на задней парте, зевает, подкладывая тетрадь под голову.)",
        "Да уж… приключение — делать уроки все выходные.",
        "Александр! Надеюсь, ты услышал?",
        "Да! Я как раз… думал передать атмосферу, ну, всей этой культурной глубины — или как там это называется…",
        "(Класс тихо хихикает.)",
        "Очень надеюсь, что твой реферат будет не списан из Википедии.",
        "(Звонок. Все вскакивают. Алекс вздыхает, собирает рюкзак.)",
        "Ну всё, Курск, сейчас не отвертишься — я тебя изучу вдоль и поперёк!",


        "(Алекс сидит за ноутбуком, набирает: «Интересные факты о Курской области». На экране — унылые ссылки.)",
        "«Курск — административный центр Курской области...» — да ладно, это даже скучнее, чем урок.",
        "(Кликает следующую ссылку.)",
        "«Основана в 1032 году...» — То есть город старше почти всех школьных учебников. Впечатляет!",
        "(Пауза. Он задумчиво листает дальше.)",
        "Может, хоть видео найду?",
        "(включает видео, где голос за кадром монотонно рассказывает)",
        "«...расположен на юго-западе Центрального федерального округа...»",
        "Всё, выключаю!",
        "(Закрывает ноутбук, ложится на диван.)",
        "Ну как тут вдохновиться? Разве кто-то может написать интересный реферат, если всё такое… скучное?",
        "Алекс, а может, попробуешь сделать реферат не только по сайтам, а с настоящими книгами?",
        "Настоящими книгами… И что, там мне вообще что-то интересное найдётся?",
        "Кто знает, может, найдётся что-то, чего в интернете нет!",
        "Хм… Я даже не знаю… Может, это всё равно будет скучно.",
        "Именно! Голова отдохнёт от экрана, а заодно узнаешь что-то новое.",
        "А вдруг там совсем ничего интересного нет? И зачем тратить время…",
        "Попробуй хотя бы один том открыть. Не понравится — вернёшься домой.",
        "Ладно… может быть. Но только одну книгу.",
        "Отлично! Вот и начало настоящего приключения.",
        "Ну… посмотрим, что там за “великие открытия”. Только не обещаю, что буду впечатлён.",


        "Библиотека… Ну да, сейчас я найду там суперсекретную энциклопедию, которая всё изменит.",
        "Может, ещё и вечный двигатель случайно изобрету.",
        "(Мимо пробегает его одноклассник Петька.)",
        "О, Саша! Куда идёшь? Футбол же скоро!",
        "Не могу, у меня миссия — написать реферат.",
        "Удачи! Только не заблудись среди этих гор бумаги.",
        "Да уж, постараюсь…",
        "(Он толкает дверь, колокольчик звенит.)",


        "Здравствуйте, юноша. Что ищем?",
        "Что-нибудь… про Курск. Но чтобы интересно. Не просто «дата, цифра, площадь».",
        "Интересно… Про Курск… Пойдёмте.",
        "(Она ведёт его между стеллажами, останавливается у старого шкафа.)",
        "Здесь редкий фонд. Но аккуратно, пожалуйста.",
        "(Уходит. Алекс смотрит на полки. Между книг виднеется странный, потрёпанный том с золотыми буквами, которые слегка мерцают: «Хроники Курского края».)",
        "Вот это да... Не видел такой даже в интернете.",
        "Эй… что это?..",
        "Все истории оживают, стоит только захотеть их узнать по-настоящему.",
        "(Белая вспышка. Экран темнеет.)"
    };


    private static final String[] speakers = {
        "",
        "Учитель",
        "",
        "Учитель",
        "",
        "Алекс",
        "Учитель",
        "Алекс",
        "",
        "Учитель",
        "",
        "Алекс",

        "",
        "Алекс",
        "",
        "Алекс",
        "",
        "Алекс",
        "Голос из видео",
        "Алекс",
        "",
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

        "Алекс",
        "Алекс",
        "",
        "Петька",
        "Алекс",
        "Петька",
        "Алекс",
        "",

        "Библиотекарь",
        "Алекс",
        "Библиотекарь",
        "",
        "Библиотекарь",
        "",
        "Алекс",
        "Алекс",
        "Таинственный голос",
        ""
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


    private static final int[] backgroundChangeIndices = {
        0, 5
    };

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
                "0123456789.,!?-—–«»\"':;() ";

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
                "0123456789.,!?-—–«»\"':;() ";


        spriteHero = new TextureRegion(new Texture("characters/hero.png"));
        spritePetya = new TextureRegion(new Texture("characters/petya.png"));
        spriteLibrarian = new TextureRegion(new Texture("characters/librarian.png"));
        spriteTeacher = new TextureRegion(new Texture("characters/teacher.png"));
        spriteMother = new TextureRegion(new Texture("characters/mother.png"));


        nameFont = generator.generateFont(nameParam);

        generator.dispose();


        layout = new GlyphLayout();
        shapeRenderer = new ShapeRenderer();

        loadBackground("scene0picture1.png");


    }

    @Override
    public void render(float delta) {

        // -------------------- ФОН --------------------
        Gdx.gl.glClearColor(0.86f, 0.78f, 0.62f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getBatch().begin();
        if (background != null)
            game.getBatch().draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.getBatch().end();

// -------------------- СПРАЙТ ПЕРСОНАЖА --------------------
        String speaker = speakers[dialogueIndex];
        TextureRegion currentSprite = getSpriteForSpeaker(speaker);

        if (currentSprite != null) {
            float scale = 0.7f;

            float w = currentSprite.getRegionWidth() * scale;
            float h = currentSprite.getRegionHeight() * scale;

            float x = 40;
            float y = 150; // выше диалоговой панели

            game.getBatch().begin();
            game.getBatch().draw(currentSprite, x, y, w, h);
            game.getBatch().end();
        }




        // -------------------- КОНЕЦ ИСТОРИИ --------------------
        if (dialogueIndex >= dialogues.length) {
            float panelY = 220;

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(panelBorderColor);
            shapeRenderer.rect(40, panelY - 10, Gdx.graphics.getWidth() - 80, 200);
            shapeRenderer.setColor(panelColor);
            shapeRenderer.rect(50, panelY, Gdx.graphics.getWidth() - 100, 180);
            shapeRenderer.end();

            game.getBatch().begin();
            layout.setText(font, "— Конец истории —",
                Color.WHITE,
                Gdx.graphics.getWidth(),
                Align.center,
                true);
            font.draw(game.getBatch(), layout, 0, panelY + 110);
            game.getBatch().end();

            return;
        }

        // -------------------- ФЛАГ ВИДИМОСТИ ПАНЕЛИ --------------------
        if (showPanel) {

            float panelHeight = 180;
            float panelY = 20;

            // ---------- ПАНЕЛЬ ДИАЛОГА ----------
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(panelBorderColor);
            shapeRenderer.rect(20, panelY - 10, Gdx.graphics.getWidth() - 40, panelHeight + 20);
            shapeRenderer.setColor(panelColor);
            shapeRenderer.rect(30, panelY, Gdx.graphics.getWidth() - 60, panelHeight);
            shapeRenderer.end();

            // ---------- ПАНЕЛЬ ИМЕНИ ----------
            speaker = (dialogueIndex < speakers.length) ? speakers[dialogueIndex] : "";

            if (!speaker.isEmpty()) {
                float nameHeight = 60;
                float nameWidth = Gdx.graphics.getWidth() * 0.55f;
                float nameX = 30;
                float nameY = panelY + panelHeight + 10;

                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(panelBorderColor);
                shapeRenderer.rect(nameX - 5, nameY - 5, nameWidth + 10, nameHeight + 10);
                shapeRenderer.setColor(namePanelColor);
                shapeRenderer.rect(nameX, nameY, nameWidth, nameHeight);
                shapeRenderer.end();

                game.getBatch().begin();
                layout.setText(nameFont, speaker, nameTextColor, nameWidth - 20, Align.left, true);
                nameFont.draw(game.getBatch(), layout, nameX + 20, nameY + nameHeight - 5);
                game.getBatch().end();
            }

            // ---------- ТЕКСТ ДИАЛОГА ----------
            String text = dialogues[dialogueIndex];
            game.getBatch().begin();
            layout.setText(font, text,
                new Color(1f, 0.96f, 0.85f, 1f),
                Gdx.graphics.getWidth() - 100,
                Align.left,
                true);
            font.draw(game.getBatch(), layout, 50, panelY + panelHeight - 40);
            game.getBatch().end();
        }

        // -------------------- УПРАВЛЕНИЕ --------------------
        if (Gdx.input.justTouched()) {
            float x = Gdx.input.getX();
            float y = Gdx.input.getY();

            if (x < Gdx.graphics.getWidth() / 4f && y < Gdx.graphics.getHeight() / 4f) {
                showPanel = !showPanel;
            }
            else if (x < Gdx.graphics.getWidth() / 2f) {
                if (dialogueIndex > 0) {
                    dialogueIndex--;
                    updateBackground();
                }
            }
            else {
                dialogueIndex++;
                updateBackground();
            }
        }
    }



    private void loadBackground(String fileName) {
        if (background != null) background.dispose();
        background = new Texture(Gdx.files.internal(fileName));
    }

    private void updateBackground() {

        if (dialogueIndex <= 11) { // Сцена 1 — школа
            loadBackground("scene0picture1.png");
        } else if (dialogueIndex <= 28) { // Сцена 2 — дома
            loadBackground("scene0picture2.png");
        } else if (dialogueIndex <= 33) { // Сцена 3 — улица
            loadBackground("scene0picture3.png");
        } else if (dialogueIndex <= 41) { // Сцена 4 — библиотека
            loadBackground("scene0picture4.png");
        } else if (dialogueIndex <= dialogues.length - 1) { // Финал
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
        if (shapeRenderer != null) shapeRenderer.dispose();
    }
}
