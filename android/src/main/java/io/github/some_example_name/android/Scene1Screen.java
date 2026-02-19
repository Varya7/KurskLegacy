package io.github.some_example_name.android;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene1Screen implements Screen, InputProcessor {
    public static int pendingDialogueIndex = -1;
    public static int pendingBackgroundIndex = 1;

    private MainGame game;
    private OrthographicCamera camera;
    private OrthographicCamera uiCamera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private BitmapFont font;
    private BitmapFont nameFont;
    private GlyphLayout layout;

    private final Color panelColor = new Color(0.25f, 0.15f, 0.07f, 0.82f);
    private final Color panelBorderColor = new Color(0.7f, 0.55f, 0.25f, 0.9f);
    private final Color namePanelColor = new Color(0.32f, 0.22f, 0.10f, 0.95f);
    private final Color nameTextColor = new Color(1f, 0.88f, 0.45f, 1f);
    private final Color dialogueTextColor = new Color(1f, 0.96f, 0.85f, 1f);

    private TextureRegion heroIdleForward;
    private TextureRegion heroIdleBackward;
    private TextureRegion heroIdleLeft;
    private TextureRegion heroIdleRight;

    private Animation<TextureRegion> walkForwardAnimation;
    private Animation<TextureRegion> walkBackwardAnimation;

    private Animation<TextureRegion> walkLeftAnimation;
    private Animation<TextureRegion> walkRightAnimation;

    private float playerX;
    private float playerY;
    private float playerWidth = 120;
    private float playerHeight = 180;
    private float playerSpeed = 200;
    private float playerSpeedBoost = 400;
    private boolean speedBoostActive = false;
    private float stateTime = 0;
    private Vector2 moveDirection = new Vector2();
    private String lastDirection = "up";

    private Circle joystick;
    private Vector2 joystickCenter;
    private float joystickRadius = 120;
    private boolean joystickTouched = false;
    private int joystickPointer = -1;

    private Circle interactButton;
    private Vector2 interactButtonCenter;
    private float interactButtonRadius = 100;
    private boolean interactButtonVisible = false;

    private Circle speedButton;
    private Vector2 speedButtonCenter;
    private float speedButtonRadius = 90;

    private float worldWidth;
    private float worldHeight;

    private static final float FEET_HEIGHT = 30f;
    private static final float SKY_HEIGHT_RATIO = 0.15f;

    private Map<String, Boolean> missingTextures = new HashMap<>();

    private static class MapObject {
        Rectangle bounds;
        String name;
        String description;
        TextureRegion texture;
        boolean isPath;
        boolean isSolid;

        MapObject(float x, float y, float width, float height, String name, String description,
                  TextureRegion texture, boolean isPath, boolean isSolid) {
            this.bounds = new Rectangle(x, y, width, height);
            this.name = name;
            this.description = description;
            this.texture = texture;
            this.isPath = isPath;
            this.isSolid = isSolid;
        }
    }

    private static class Animal {
        Rectangle bounds;
        TextureRegion texture;
        Rectangle area;
        String type;
        boolean isSolid;

        Animal(float x, float y, float width, float height, TextureRegion tex, Rectangle area, String type, boolean isSolid) {
            this.bounds = new Rectangle(x, y, width, height);
            this.texture = tex;
            this.area = area;
            this.type = type;
            this.isSolid = isSolid;
        }
    }

    private List<MapObject> mapObjects = new ArrayList<>();
    private List<Animal> animals = new ArrayList<>();
    private MapObject currentNearObject = null;

    // ТЕКСТУРЫ
    private TextureRegion textureBake;
    private TextureRegion textureBarrel;
    private TextureRegion textureBush1;
    private TextureRegion textureBush2;
    private TextureRegion textureCart;
    private TextureRegion textureCock1;
    private TextureRegion textureCock2;
    private TextureRegion textureCow;
    private TextureRegion textureDog1;
    private TextureRegion textureDog2;
    private TextureRegion textureFirewood1;
    private TextureRegion textureFirewood2;
    private TextureRegion textureHay;
    private TextureRegion textureHouse1;
    private TextureRegion textureHouse2;
    private TextureRegion textureHouse3;
    private TextureRegion texturePeasant1;
    private TextureRegion texturePeasant2;
    private TextureRegion texturePeasant3;
    private TextureRegion texturePeasant4;
    private TextureRegion texturePeasant5;
    private TextureRegion texturePit;
    private TextureRegion texturePottersWheel;
    private TextureRegion textureSheep1;
    private TextureRegion textureSheep2;
    private TextureRegion textureStone;
    private TextureRegion textureStump1;
    private TextureRegion textureStump2;
    private TextureRegion textureSunflower1;
    private TextureRegion textureSunflower2;
    private TextureRegion textureTree;
    private TextureRegion textureTrough;

    private TextureRegion textureWater;
    private TextureRegion textureFlowers;
    private TextureRegion textureGrass;
    private TextureRegion textureMushrooms;
    private TextureRegion textureBridge;
    private TextureRegion textureCloud1;
    private TextureRegion textureCloud2;
    private TextureRegion textureSun;
    private TextureRegion textureMountains;
    private TextureRegion textureForest;

    private Color skyColor = new Color(0.5f, 0.7f, 0.9f, 1);
    private Color groundColor = new Color(0.3f, 0.5f, 0.2f, 1);
    private Color pathColor = new Color(0.85f, 0.75f, 0.55f, 1);
    private Color waterColor = new Color(0.2f, 0.6f, 0.9f, 0.9f);

    private List<Rectangle> pathRectangles = new ArrayList<>();
    private List<DecorativeObject> decorations = new ArrayList<>();

    private boolean showDialogue = false;

    private static class DecorativeObject {
        Rectangle bounds;
        TextureRegion texture;
        boolean isSolid;

        DecorativeObject(float x, float y, float width, float height, TextureRegion texture, boolean isSolid) {
            this.bounds = new Rectangle(x, y, width, height);
            this.texture = texture;
            this.isSolid = isSolid;
        }
    }

    public Scene1Screen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        worldWidth = screenWidth * 4;
        worldHeight = screenHeight * 2;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth, screenHeight);

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, screenWidth, screenHeight);

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        initFonts();
        loadAllTextures();
        loadHeroAnimations();
        createPaths();
        createMapObjects();
        createAnimals();
        createDecorations();



        playerX = worldWidth * 0.1f;
        playerY = worldHeight * 0.45f;

        updateCamera();
        camera.update();

        joystickCenter = new Vector2(joystickRadius + 60, joystickRadius + 60);
        joystick = new Circle(joystickCenter, joystickRadius);

        interactButtonCenter = new Vector2(
            screenWidth - interactButtonRadius - 60,
            interactButtonRadius + 60
        );
        interactButton = new Circle(interactButtonCenter, interactButtonRadius);

        speedButtonCenter = new Vector2(
            screenWidth - 250,
            screenHeight - 120
        );
        speedButton = new Circle(speedButtonCenter, speedButtonRadius);

        Gdx.input.setInputProcessor(this);
    }

    private void initFonts() {
        try {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Regular.ttf"));

            FreeTypeFontGenerator.FreeTypeFontParameter textParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
            textParam.size = 48;
            textParam.color = dialogueTextColor;
            textParam.borderWidth = 2f;
            textParam.borderColor = new Color(0.1f, 0.05f, 0.02f, 0.9f);
            textParam.shadowOffsetX = 2;
            textParam.shadowOffsetY = 2;
            textParam.shadowColor = new Color(0, 0, 0, 0.5f);
            textParam.characters = getAllCharacters();

            font = generator.generateFont(textParam);

            FreeTypeFontGenerator.FreeTypeFontParameter nameParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
            nameParam.size = 48;
            nameParam.color = nameTextColor;
            nameParam.borderWidth = 2f;
            nameParam.borderColor = new Color(0.1f, 0.05f, 0.02f, 1f);
            nameParam.shadowOffsetX = 2;
            nameParam.shadowOffsetY = 2;
            nameParam.shadowColor = new Color(0, 0, 0, 0.7f);
            nameParam.characters = getAllCharacters();

            nameFont = generator.generateFont(nameParam);
            generator.dispose();

        } catch (Exception e) {
            font = new BitmapFont();
            nameFont = new BitmapFont();
            font.getData().setScale(2.0f);
            nameFont.getData().setScale(2.0f);
            font.setColor(dialogueTextColor);
            nameFont.setColor(nameTextColor);
        }

        layout = new GlyphLayout();
    }

    private String getAllCharacters() {
        return "абвгдеёжзийклмнопрстуфхцчшщъыьэюя" +
            "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" +
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "0123456789.,!?-—–«»\"':;()… ";
    }

    private void loadHeroAnimations() {
        String path = "characters/";

        heroIdleForward = loadHeroTexture(path + "heroforward.png", "heroforward");
        heroIdleBackward = loadHeroTexture(path + "herobackward.png", "herobackward");
        heroIdleLeft = loadHeroTexture(path + "heroleft.png", "heroleft");
        heroIdleRight = loadHeroTexture(path + "heroright.png", "heroright");

        TextureRegion forward1 = loadHeroTexture(path + "heroforward1.png", "heroforward1");
        TextureRegion forward2 = loadHeroTexture(path + "heroforward2.png", "heroforward2");
        if (forward1 != null && forward2 != null) {
            walkForwardAnimation = new Animation<>(0.2f, forward1, forward2);
        }

        TextureRegion backward1 = loadHeroTexture(path + "herobackward1.png", "herobackward1");
        TextureRegion backward2 = loadHeroTexture(path + "herobackward2.png", "herobackward2");
        if (backward1 != null && backward2 != null) {
            walkBackwardAnimation = new Animation<>(0.2f, backward1, backward2);
        }

        TextureRegion left1 = loadHeroTexture(path + "heroleft1.png", "heroleft1");
        TextureRegion leftCenter = heroIdleLeft;
        TextureRegion left2 = loadHeroTexture(path + "heroleft2.png", "heroleft2");
        if (left1 != null && leftCenter != null && left2 != null) {
            walkLeftAnimation = new Animation<>(0.15f, left1, leftCenter, left2, leftCenter);
        } else if (left1 != null && left2 != null) {
            walkLeftAnimation = new Animation<>(0.2f, left1, left2);
        }

        TextureRegion right1 = loadHeroTexture(path + "heroright1.png", "heroright1");
        TextureRegion rightCenter = heroIdleRight;
        TextureRegion right2 = loadHeroTexture(path + "heroright2.png", "heroright2");
        if (right1 != null && rightCenter != null && right2 != null) {
            walkRightAnimation = new Animation<>(0.15f, right1, rightCenter, right2, rightCenter);
        } else if (right1 != null && right2 != null) {
            walkRightAnimation = new Animation<>(0.2f, right1, right2);
        }

        createFallbackHeroTextures();
    }

    private TextureRegion loadHeroTexture(String path, String name) {
        try {
            if (Gdx.files.internal(path).exists()) {
                return new TextureRegion(new Texture(path));
            } else {
                missingTextures.put(name, true);
                return null;
            }
        } catch (Exception e) {
            missingTextures.put(name + " (error)", true);
            return null;
        }
    }

    private void createFallbackHeroTextures() {
        if (heroIdleForward == null) {
            heroIdleForward = createFallbackTextureRegion(Color.RED, 80, 120);
        }
        if (heroIdleBackward == null) {
            heroIdleBackward = createFallbackTextureRegion(Color.BLUE, 80, 120);
        }
        if (heroIdleLeft == null) {
            heroIdleLeft = createFallbackTextureRegion(Color.GREEN, 80, 120);
        }
        if (heroIdleRight == null) {
            heroIdleRight = createFallbackTextureRegion(Color.YELLOW, 80, 120);
        }

        if (walkForwardAnimation == null) {
            TextureRegion f1 = createFallbackTextureRegion(Color.RED, 80, 120);
            TextureRegion f2 = createFallbackTextureRegion(Color.ORANGE, 80, 120);
            walkForwardAnimation = new Animation<>(0.2f, f1, f2);
        }
        if (walkBackwardAnimation == null) {
            TextureRegion b1 = createFallbackTextureRegion(Color.BLUE, 80, 120);
            TextureRegion b2 = createFallbackTextureRegion(Color.CYAN, 80, 120);
            walkBackwardAnimation = new Animation<>(0.2f, b1, b2);
        }
        if (walkLeftAnimation == null) {
            TextureRegion l1 = createFallbackTextureRegion(Color.GREEN, 80, 120);
            TextureRegion l2 = createFallbackTextureRegion(Color.LIME, 80, 120);
            walkLeftAnimation = new Animation<>(0.2f, l1, l2);
        }
        if (walkRightAnimation == null) {
            TextureRegion r1 = createFallbackTextureRegion(Color.YELLOW, 80, 120);
            TextureRegion r2 = createFallbackTextureRegion(Color.GOLD, 80, 120);
            walkRightAnimation = new Animation<>(0.2f, r1, r2);
        }
    }

    private void loadAllTextures() {
        try {
            String path = "chapter1/";

            textureBake = loadTexture(path + "bake.png", "bake");
            textureBarrel = loadTexture(path + "barrel.png", "barrel");
            textureBush1 = loadTexture(path + "bush1.png", "bush1");
            textureBush2 = loadTexture(path + "bush2.png", "bush2");
            textureCart = loadTexture(path + "cart.png", "cart");
            textureCock1 = loadTexture(path + "cock1.png", "cock1");
            textureCock2 = loadTexture(path + "cock2.png", "cock2");
            textureCow = loadTexture(path + "cow.png", "cow");
            textureDog1 = loadTexture(path + "dog1.png", "dog1");
            textureDog2 = loadTexture(path + "dog2.png", "dog2");
            textureFirewood1 = loadTexture(path + "firewood1.png", "firewood1");
            textureFirewood2 = loadTexture(path + "firewood2.png", "firewood2");
            textureHay = loadTexture(path + "hay.png", "hay");
            textureHouse1 = loadTexture(path + "house1.png", "house1");
            textureHouse2 = loadTexture(path + "house2.png", "house2");
            textureHouse3 = loadTexture(path + "house3.png", "house3");
            texturePeasant1 = loadTexture(path + "peasant1.png", "peasant1");
            texturePeasant2 = loadTexture(path + "peasant2.png", "peasant2");
            texturePeasant3 = loadTexture(path + "peasant3.png", "peasant3");
            texturePeasant4 = loadTexture(path + "peasant4.png", "peasant4");
            texturePeasant5 = loadTexture(path + "peasant5.png", "peasant5");
            texturePit = loadTexture(path + "pit.png", "pit");
            texturePottersWheel = loadTexture(path + "potterswheel.png", "potterswheel");
            textureSheep1 = loadTexture(path + "sheep1.png", "sheep1");
            textureSheep2 = loadTexture(path + "sheep2.png", "sheep2");
            textureStone = loadTexture(path + "stone.png", "stone");
            textureStump1 = loadTexture(path + "stump1.png", "stump1");
            textureStump2 = loadTexture(path + "stump2.png", "stump2");
            textureSunflower1 = loadTexture(path + "sunflower1.png", "sunflower1");
            textureSunflower2 = loadTexture(path + "sunflower2.png", "sunflower2");
            textureTree = loadTexture(path + "tree.png", "tree");
            textureTrough = loadTexture(path + "trough.png", "trough");

            textureWater = createFallbackTextureRegion(new Color(0.2f, 0.6f, 0.9f, 1f), 200, 80);
            textureFlowers = createFallbackTextureRegion(new Color(1f, 0.9f, 0.7f, 1f), 100, 50);
            textureGrass = createFallbackTextureRegion(new Color(0.3f, 0.8f, 0.3f, 1f), 100, 60);
            textureMushrooms = createFallbackTextureRegion(new Color(0.9f, 0.6f, 0.5f, 1f), 80, 50);
            textureBridge = createFallbackTextureRegion(new Color(0.7f, 0.5f, 0.3f, 1f), 300, 60);

            textureCloud1 = createFallbackTextureRegion(new Color(1f, 1f, 1f, 0.9f), 250, 80);
            textureCloud2 = createFallbackTextureRegion(new Color(1f, 1f, 1f, 0.9f), 300, 70);
            textureSun = createFallbackTextureRegion(new Color(1f, 0.9f, 0.3f, 1f), 120, 120);
            textureMountains = createFallbackTextureRegion(new Color(0.4f, 0.3f, 0.2f, 1f), 400, 150);
            textureForest = createFallbackTextureRegion(new Color(0.2f, 0.5f, 0.1f, 1f), 500, 180);

        } catch (Exception e) {
            Gdx.app.error("Scene1Screen", "Ошибка загрузки текстур: " + e.getMessage());
        }
    }

    private TextureRegion loadTexture(String path, String name) {
        try {
            if (Gdx.files.internal(path).exists()) {
                return new TextureRegion(new Texture(path));
            } else {
                missingTextures.put(name, true);
                return createFallbackTextureRegion(Color.PURPLE, 64, 64);
            }
        } catch (Exception e) {
            missingTextures.put(name + " (error)", true);
            return createFallbackTextureRegion(Color.PURPLE, 64, 64);
        }
    }

    private TextureRegion createFallbackTextureRegion(Color color, int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegion(texture);
    }

    private void createPaths() {
        float roadY = worldHeight * 0.45f;
        float roadWidth = 300;

        pathRectangles.add(new Rectangle(0, roadY - roadWidth/2, worldWidth, roadWidth));

        pathRectangles.add(new Rectangle(worldWidth * 0.1f - 100, roadY - 250, 300, 200));
        pathRectangles.add(new Rectangle(worldWidth * 0.1f - 100, roadY + 50, 300, 200));

        pathRectangles.add(new Rectangle(worldWidth * 0.35f - 100, roadY - 300, 300, 200));
        pathRectangles.add(new Rectangle(worldWidth * 0.35f - 100, roadY + 100, 300, 200));

        pathRectangles.add(new Rectangle(worldWidth * 0.6f - 100, roadY - 280, 300, 200));
        pathRectangles.add(new Rectangle(worldWidth * 0.6f - 100, roadY + 80, 300, 200));

        pathRectangles.add(new Rectangle(worldWidth * 0.85f - 100, roadY - 270, 300, 200));
        pathRectangles.add(new Rectangle(worldWidth * 0.85f - 100, roadY + 90, 300, 200));

        pathRectangles.add(new Rectangle(worldWidth * 1.1f - 100, roadY - 290, 300, 200));
        pathRectangles.add(new Rectangle(worldWidth * 1.1f - 100, roadY + 70, 300, 200));

        pathRectangles.add(new Rectangle(worldWidth * 0.2f - 50, roadY - 150, 200, 100));
        pathRectangles.add(new Rectangle(worldWidth * 0.45f - 50, roadY + 120, 200, 100));
        pathRectangles.add(new Rectangle(worldWidth * 0.7f - 50, roadY - 180, 200, 100));
        pathRectangles.add(new Rectangle(worldWidth * 0.95f - 50, roadY + 100, 200, 100));
    }

    private void createMapObjects() {
        float roadY = worldHeight * 0.45f;

        mapObjects.clear();

        mapObjects.add(new MapObject(worldWidth * 0.12f - 150, roadY + 200, 320, 290, "Дом лесника",
            "Уютный дом на опушке леса. Здесь живет лесник.",
            textureHouse1, false, true));

        mapObjects.add(new MapObject(worldWidth * 0.37f - 150, roadY + 180, 330, 300, "Дом кузнеца",
            "Крепкий дом, где живет кузнец.",
            textureHouse2, false, true));

        mapObjects.add(new MapObject(worldWidth * 0.4f - 150, roadY - 200, 140, 160, "Колодец",
            "Старый колодец. Вода очень вкусная.",
            texturePit, true, true));

        mapObjects.add(new MapObject(worldWidth * 0.62f - 150, roadY + 190, 340, 310, "Дом старосты",
            "Самый большой дом.",
            textureHouse3, false, true));

        mapObjects.add(new MapObject(worldWidth * 0.65f - 150, roadY - 180, 100, 80, "Печь",
            "Печь для выпечки хлеба.",
            textureBake, true, true));

        mapObjects.add(new MapObject(worldWidth * 0.87f - 150, roadY + 170, 320, 290, "Дом пекаря",
            "Из трубы идет дым.",
            textureHouse1, false, true));

        mapObjects.add(new MapObject(worldWidth * 0.9f - 150, roadY - 190, 160, 130, "Телега",
            "Старая телега с сеном.",
            textureCart, true, true));

        mapObjects.add(new MapObject(worldWidth * 1.12f - 150, roadY + 200, 360, 330, "Дом гончара Ильи",
            "Здесь живет и работает Илья.",
            textureHouse3, false, true));

        mapObjects.add(new MapObject(worldWidth * 1.15f - 150, roadY - 100, 170, 190, "Илья-гончар",
            "Привет, путник! Я Илья, местный гончар.",
            texturePottersWheel, true, true));

        mapObjects.add(new MapObject(worldWidth * 0.05f, roadY + 450, 160, 210, "Дуб",
            "Могучий дуб.", textureTree, false, true));
        mapObjects.add(new MapObject(worldWidth * 0.3f, roadY - 450, 150, 200, "Береза",
            "Белоствольная береза.", textureTree, false, true));
        mapObjects.add(new MapObject(worldWidth * 0.55f, roadY + 440, 160, 210, "Клен",
            "Раскидистый клен.", textureTree, false, true));
        mapObjects.add(new MapObject(worldWidth * 0.8f, roadY - 460, 150, 200, "Сосна",
            "Высокая сосна.", textureTree, false, true));
        mapObjects.add(new MapObject(worldWidth * 1.05f, roadY + 430, 160, 210, "Липа",
            "Цветущая липа.", textureTree, false, true));

        mapObjects.add(new MapObject(worldWidth * 0.18f, roadY - 280, 70, 70, "Малина",
            "Куст малины.", textureBush1, true, false));
        mapObjects.add(new MapObject(worldWidth * 0.43f, roadY + 300, 75, 75, "Смородина",
            "Куст смородины.", textureBush2, true, false));
        mapObjects.add(new MapObject(worldWidth * 0.68f, roadY - 300, 70, 70, "Шиповник",
            "Куст шиповника.", textureBush1, true, false));

        mapObjects.add(new MapObject(worldWidth * 0.22f, roadY - 200, 80, 140, "Иван",
            "Крестьянин.", texturePeasant1, true, false));
        mapObjects.add(new MapObject(worldWidth * 0.52f, roadY + 250, 80, 140, "Марья",
            "Крестьянин.", texturePeasant2, true, false));
        mapObjects.add(new MapObject(worldWidth * 0.82f, roadY - 220, 80, 140, "Матвей",
            "Старик.", texturePeasant3, true, false));

        mapObjects.add(new MapObject(worldWidth * 0.25f, roadY + 350, 60, 130, "Подсолнух",
            "Яркий подсолнух.", textureSunflower1, true, false));
        mapObjects.add(new MapObject(worldWidth * 0.7f, roadY - 350, 60, 130, "Подсолнух",
            "Подсолнух с семечками.", textureSunflower2, true, false));

        mapObjects.add(new MapObject(worldWidth * 0.1f, roadY - 400, 70, 50, "Дрова",
            "Аккуратно сложенные дрова.", textureFirewood1, true, true));
        mapObjects.add(new MapObject(worldWidth * 0.5f, roadY + 400, 60, 45, "Пень",
            "Старый пень.", textureStump1, true, true));
    }

    private void createAnimals() {
        float roadY = worldHeight * 0.45f;

        if (textureCock1 != null) {
            animals.add(new Animal(worldWidth * 0.3f, roadY + 150, 60, 60, textureCock1, null, "cock", true));
            animals.add(new Animal(worldWidth * 0.33f, roadY + 170, 60, 60, textureCock2, null, "cock", true));
        }

        if (textureSheep1 != null) {
            animals.add(new Animal(worldWidth * 0.45f, roadY + 380, 80, 60, textureSheep1, null, "sheep", true));
            animals.add(new Animal(worldWidth * 0.5f, roadY + 400, 80, 60, textureSheep2, null, "sheep", true));
        }

        if (textureDog1 != null) {
            animals.add(new Animal(worldWidth * 0.75f, roadY - 180, 70, 60, textureDog1, null, "dog", true));
        }

        if (textureCow != null) {
            animals.add(new Animal(worldWidth * 0.95f, roadY + 350, 100, 80, textureCow, null, "cow", true));
        }
    }

    private void createDecorations() {
        float roadY = worldHeight * 0.45f;

        decorations.add(new DecorativeObject(worldWidth * 0.1f, worldHeight * 0.85f, 250, 80, textureCloud1, false));
        decorations.add(new DecorativeObject(worldWidth * 0.4f, worldHeight * 0.9f, 300, 70, textureCloud2, false));
        decorations.add(new DecorativeObject(worldWidth * 0.7f, worldHeight * 0.87f, 280, 75, textureCloud1, false));
        decorations.add(new DecorativeObject(worldWidth * 1.0f, worldHeight * 0.83f, 270, 80, textureCloud2, false));

        decorations.add(new DecorativeObject(worldWidth * 1.5f, worldHeight * 0.9f, 120, 120, textureSun, false));

        decorations.add(new DecorativeObject(worldWidth * 0.0f, worldHeight * 0.65f, 400, 150, textureMountains, false));
        decorations.add(new DecorativeObject(worldWidth * 0.3f, worldHeight * 0.7f, 500, 180, textureForest, false));
        decorations.add(new DecorativeObject(worldWidth * 0.7f, worldHeight * 0.68f, 450, 160, textureForest, false));

        decorations.add(new DecorativeObject(worldWidth * 0.4f - 200, roadY - 550, 400, 80, textureWater, true));
        decorations.add(new DecorativeObject(worldWidth * 0.5f - 150, roadY - 520, 300, 60, textureBridge, false));

        decorations.add(new DecorativeObject(worldWidth * 0.15f, roadY + 500, 100, 50, textureFlowers, false));
        decorations.add(new DecorativeObject(worldWidth * 0.55f, roadY - 600, 120, 50, textureFlowers, false));
    }

    private TextureRegion getCurrentHeroFrame() {
        boolean isMoving = moveDirection.len() > 0.1f;

        if (isMoving) {
            if (Math.abs(moveDirection.x) > Math.abs(moveDirection.y)) {
                if (moveDirection.x > 0) {
                    lastDirection = "right";
                    if (walkRightAnimation != null) {
                        return walkRightAnimation.getKeyFrame(stateTime, true);
                    }
                } else {
                    lastDirection = "left";
                    if (walkLeftAnimation != null) {
                        return walkLeftAnimation.getKeyFrame(stateTime, true);
                    }
                }
            } else {
                if (moveDirection.y > 0) {
                    lastDirection = "down";
                    if (walkBackwardAnimation != null) {
                        return walkBackwardAnimation.getKeyFrame(stateTime, true);
                    }
                } else {
                    lastDirection = "up";
                    if (walkForwardAnimation != null) {
                        return walkForwardAnimation.getKeyFrame(stateTime, true);
                    }
                }
            }
        }

        switch (lastDirection) {
            case "up": return heroIdleForward != null ? heroIdleForward : heroIdleBackward;
            case "down": return heroIdleBackward != null ? heroIdleBackward : heroIdleForward;
            case "left": return heroIdleLeft != null ? heroIdleLeft : heroIdleForward;
            case "right": return heroIdleRight != null ? heroIdleRight : heroIdleForward;
            default: return heroIdleForward;
        }
    }

    @Override
    public void render(float delta) {
        stateTime += delta;
        update(delta);

        Gdx.gl.glClearColor(skyColor.r, skyColor.g, skyColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        drawWorld();
        drawDecorations();
        drawMapObjects();

        batch.begin();
        batch.setColor(Color.WHITE);
        TextureRegion currentFrame = getCurrentHeroFrame();
        if (currentFrame != null) {
            batch.draw(currentFrame, playerX, playerY, playerWidth, playerHeight);
        }
        batch.end();

        uiCamera.update();
        batch.setProjectionMatrix(uiCamera.combined);
        shapeRenderer.setProjectionMatrix(uiCamera.combined);

        drawUI();

        if (showDialogue && currentNearObject != null) {
            drawDialoguePanel();
        }
    }

    private void drawWorld() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(skyColor);
        shapeRenderer.rect(0, worldHeight * (1 - SKY_HEIGHT_RATIO), worldWidth, worldHeight * SKY_HEIGHT_RATIO);

        shapeRenderer.setColor(groundColor);
        shapeRenderer.rect(0, 0, worldWidth, worldHeight * (1 - SKY_HEIGHT_RATIO));

        shapeRenderer.setColor(pathColor);
        for (Rectangle path : pathRectangles) {
            shapeRenderer.rect(path.x, path.y, path.width, path.height);
        }

        shapeRenderer.end();
    }

    private void drawDecorations() {
        batch.begin();
        for (DecorativeObject dec : decorations) {
            if (dec.texture != null) {
                batch.setColor(Color.WHITE);
                batch.draw(dec.texture, dec.bounds.x, dec.bounds.y, dec.bounds.width, dec.bounds.height);
            }
        }
        batch.end();
    }

    private void drawMapObjects() {
        List<Drawable> allDrawables = new ArrayList<>();

        for (MapObject obj : mapObjects) {
            if (obj.texture != null) {
                allDrawables.add(new Drawable(obj));
            }
        }

        for (Animal animal : animals) {
            if (animal.texture != null) {
                allDrawables.add(new Drawable(animal));
            }
        }

        allDrawables.sort((d1, d2) -> Float.compare(d2.y, d1.y));

        batch.begin();
        for (Drawable d : allDrawables) {
            if (d.texture != null) {
                if (d.isAnimal) {
                    batch.setColor(Color.WHITE);
                } else if (d.obj == currentNearObject) {
                    batch.setColor(1.2f, 1.2f, 1.0f, 1f);
                } else {
                    batch.setColor(Color.WHITE);
                }

                float x = d.x;
                float y = d.y;
                float width = d.width;
                float height = d.height;

                batch.draw(d.texture, x, y, width, height);
            }
        }
        batch.end();
    }

    private class Drawable {
        float x, y, width, height;
        TextureRegion texture;
        MapObject obj;
        boolean isAnimal;

        Drawable(MapObject obj) {
            this.obj = obj;
            this.isAnimal = false;
            this.texture = obj.texture;
            this.width = obj.bounds.width;
            this.height = obj.bounds.height;
            this.x = obj.bounds.x;
            this.y = obj.bounds.y;
        }

        Drawable(Animal animal) {
            this.isAnimal = true;
            this.texture = animal.texture;
            this.x = animal.bounds.x;
            this.y = animal.bounds.y;
            this.width = animal.bounds.width;
            this.height = animal.bounds.height;
        }
    }

    private void drawUI() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if (!showDialogue) {
            shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 0.7f);
            shapeRenderer.circle(joystickCenter.x, joystickCenter.y, joystickRadius);
            shapeRenderer.setColor(0.4f, 0.4f, 0.4f, 0.8f);
            shapeRenderer.circle(joystickCenter.x, joystickCenter.y, joystickRadius * 0.7f);

            if (joystickTouched && moveDirection.len() > 0.1f) {
                shapeRenderer.setColor(0.8f, 0.8f, 0.8f, 1f);
                float stickX = joystickCenter.x + moveDirection.x * joystickRadius * 0.5f;
                float stickY = joystickCenter.y + moveDirection.y * joystickRadius * 0.5f;
                shapeRenderer.circle(stickX, stickY, joystickRadius * 0.3f);
            }

            if (speedBoostActive) {
                shapeRenderer.setColor(1f, 0.7f, 0.2f, 0.9f);
            } else {
                shapeRenderer.setColor(0.3f, 0.3f, 0.8f, 0.8f);
            }
            shapeRenderer.circle(speedButtonCenter.x, speedButtonCenter.y, speedButtonRadius);
            shapeRenderer.setColor(0.9f, 0.9f, 0.9f, 1f);
            shapeRenderer.circle(speedButtonCenter.x, speedButtonCenter.y, speedButtonRadius * 0.6f);

            if (interactButtonVisible) {
                if (currentNearObject != null && currentNearObject.name.contains("Илья")) {
                    shapeRenderer.setColor(0.1f, 0.5f, 0.1f, 0.8f);
                } else {
                    shapeRenderer.setColor(0.1f, 0.3f, 0.6f, 0.8f);
                }
                shapeRenderer.circle(interactButtonCenter.x, interactButtonCenter.y, interactButtonRadius);
                shapeRenderer.setColor(0.9f, 0.9f, 0.9f, 1f);
                shapeRenderer.circle(interactButtonCenter.x, interactButtonCenter.y, interactButtonRadius * 0.7f);
                shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.9f);
                shapeRenderer.circle(interactButtonCenter.x, interactButtonCenter.y, interactButtonRadius * 0.5f);
            }
        }

        shapeRenderer.end();

        if (nameFont != null) {
            batch.begin();

            nameFont.setColor(Color.WHITE);
            layout.setText(nameFont, "⚡");
            float textX = speedButtonCenter.x - layout.width/2;
            float textY = speedButtonCenter.y + layout.height/2;
            nameFont.draw(batch, layout, textX, textY);

            if (interactButtonVisible && !showDialogue) {
                nameFont.setColor(Color.WHITE);
                layout.setText(nameFont, "E");
                textX = interactButtonCenter.x - layout.width/2;
                textY = interactButtonCenter.y + layout.height/2;
                nameFont.draw(batch, layout, textX, textY);
            }

            batch.end();
        }

        if (currentNearObject != null && !showDialogue && nameFont != null) {
            batch.begin();
            nameFont.setColor(nameTextColor);
            String hintText = "Нажмите E";
            layout.setText(nameFont, hintText);
            float hintX = Gdx.graphics.getWidth() / 2 - layout.width / 2;
            float hintY = Gdx.graphics.getHeight() - 80;
            nameFont.draw(batch, layout, hintX, hintY);
            batch.end();
        }
    }

    private void drawDialoguePanel() {
        float w = Gdx.graphics.getWidth();

        float panelX = 40;
        float panelY = 40;
        float panelWidth = w - 80;
        float panelHeight = 240;

        float namePanelHeight = 70;
        float namePanelY = panelY + panelHeight + 15;
        float namePanelWidth = panelWidth;
        float namePanelX = panelX;

        float btnWidth = 200;
        float btnHeight = 70;
        float btnX = panelX + panelWidth - btnWidth - 20;
        float btnY = panelY + 25;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(panelColor);
        shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);
        shapeRenderer.setColor(panelBorderColor);
        shapeRenderer.rect(panelX - 2, panelY - 2, panelWidth + 4, 2);
        shapeRenderer.rect(panelX - 2, panelY + panelHeight, panelWidth + 4, 2);
        shapeRenderer.rect(panelX - 2, panelY - 2, 2, panelHeight + 4);
        shapeRenderer.rect(panelX + panelWidth, panelY - 2, 2, panelHeight + 4);

        shapeRenderer.setColor(namePanelColor);
        shapeRenderer.rect(namePanelX, namePanelY, namePanelWidth, namePanelHeight);
        shapeRenderer.setColor(panelBorderColor);
        shapeRenderer.rect(namePanelX - 2, namePanelY - 2, namePanelWidth + 4, 2);
        shapeRenderer.rect(namePanelX - 2, namePanelY + namePanelHeight, namePanelWidth + 4, 2);
        shapeRenderer.rect(namePanelX - 2, namePanelY - 2, 2, namePanelHeight + 4);
        shapeRenderer.rect(namePanelX + namePanelWidth, namePanelY - 2, 2, namePanelHeight + 4);

        shapeRenderer.setColor(0.38f, 0.28f, 0.15f, 1f);
        shapeRenderer.rect(btnX, btnY, btnWidth, btnHeight);
        shapeRenderer.setColor(0.6f, 0.5f, 0.35f, 1f);
        shapeRenderer.rect(btnX, btnY + btnHeight - 6, btnWidth, 6);

        shapeRenderer.end();

        batch.begin();

        if (nameFont != null && currentNearObject != null) {
            nameFont.setColor(nameTextColor);
            String nameText = currentNearObject.name;
            float nameX = namePanelX + 15;
            layout.setText(nameFont, nameText);
            float nameY = namePanelY + (namePanelHeight + layout.height) / 2f - 2;
            float maxTextWidth = namePanelWidth - 30;
            nameFont.draw(batch, nameText, nameX, nameY, 0, nameText.length(), maxTextWidth, Align.left, true);
        }

        if (font != null && currentNearObject != null) {
            font.setColor(dialogueTextColor);
            layout.setText(font, currentNearObject.description, dialogueTextColor, panelWidth - 60, Align.left, true);
            font.draw(batch, layout, panelX + 30, panelY + panelHeight - 50);
        }

        if (nameFont != null) {
            nameFont.setColor(Color.WHITE);
            layout.setText(nameFont, "Далее");
            float btnTextX = btnX + (btnWidth - layout.width) / 2f;
            float btnTextY = btnY + btnHeight / 2f + layout.height / 3f;
            nameFont.draw(batch, layout, btnTextX, btnTextY);
        }

        batch.end();
    }

    private boolean isOnPath(float x, float y) {
        Rectangle feetRect = new Rectangle(x, y - FEET_HEIGHT/2, playerWidth, FEET_HEIGHT);

        for (Rectangle path : pathRectangles) {
            if (feetRect.overlaps(path)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSolidObjectAt(float x, float y) {
        Rectangle playerRect = new Rectangle(x, y, playerWidth, playerHeight);

        for (MapObject obj : mapObjects) {
            if (obj.isSolid && playerRect.overlaps(obj.bounds)) {
                return true;
            }
        }

        for (Animal animal : animals) {
            if (animal.isSolid && playerRect.overlaps(animal.bounds)) {
                return true;
            }
        }

        for (DecorativeObject dec : decorations) {
            if (dec.isSolid && playerRect.overlaps(dec.bounds)) {
                return true;
            }
        }

        return false;
    }

    private Rectangle getCurrentPathArea(float x, float y) {
        Rectangle feetRect = new Rectangle(x, y - FEET_HEIGHT/2, playerWidth, FEET_HEIGHT);

        for (Rectangle path : pathRectangles) {
            if (feetRect.overlaps(path)) {
                return path;
            }
        }

        return new Rectangle(x - 100, y - 100, playerWidth + 200, playerHeight + 200);
    }

    private void updateCamera() {
        float targetX = playerX + playerWidth/2;
        float targetY = playerY + playerHeight/2;

        float minX = camera.viewportWidth / 2;
        float maxX = worldWidth - camera.viewportWidth / 2;
        float minY = camera.viewportHeight * 0.2f;
        float maxY = worldHeight - camera.viewportHeight * 0.2f;

        camera.position.x = Math.max(minX, Math.min(targetX, maxX));
        camera.position.y = Math.max(minY, Math.min(targetY, maxY));
    }

    private void update(float delta) {
        if (joystickTouched && !showDialogue) {
            float currentSpeed = speedBoostActive ? playerSpeedBoost : playerSpeed;

            float newX = playerX + moveDirection.x * currentSpeed * delta;
            float newY = playerY + moveDirection.y * currentSpeed * delta;

            newX = Math.max(0, Math.min(newX, worldWidth - playerWidth));
            newY = Math.max(0, Math.min(newY, worldHeight - playerHeight));

            if (!isSolidObjectAt(newX, playerY) && isOnPath(newX, playerY)) {
                playerX = newX;
            }

            if (!isSolidObjectAt(playerX, newY) && isOnPath(playerX, newY)) {
                playerY = newY;
            }

            playerX = Math.max(0, Math.min(playerX, worldWidth - playerWidth));
            playerY = Math.max(0, Math.min(playerY, worldHeight - playerHeight));
        }

        updateCamera();
        checkObjectInteraction();
    }

    private void checkObjectInteraction() {
        currentNearObject = null;
        Rectangle playerBounds = new Rectangle(playerX, playerY, playerWidth, playerHeight);

        for (MapObject obj : mapObjects) {
            Rectangle detectionBounds = new Rectangle(
                obj.bounds.x - 100,
                obj.bounds.y - 100,
                obj.bounds.width + 200,
                obj.bounds.height + 200
            );

            if (playerBounds.overlaps(detectionBounds)) {
                currentNearObject = obj;
                break;
            }
        }

        interactButtonVisible = (currentNearObject != null && !showDialogue);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        float y = Gdx.graphics.getHeight() - screenY;

        if (joystick.contains(screenX, y) && !showDialogue && joystickPointer == -1) {
            joystickTouched = true;
            joystickPointer = pointer;
            updateMoveDirection(screenX - joystickCenter.x, y - joystickCenter.y);
            return true;
        }

        if (speedButton.contains(screenX, y) && !showDialogue) {
            speedBoostActive = !speedBoostActive;
            return true;
        }

        if (interactButtonVisible && interactButton.contains(screenX, y) && !showDialogue) {
            showDialogue = true;
            return true;
        }

        if (showDialogue) {
            float w = Gdx.graphics.getWidth();
            float panelX = 40;
            float panelWidth = w - 80;
            float btnWidth = 200;
            float btnHeight = 70;
            float btnX = panelX + panelWidth - btnWidth - 20;
            float btnY = 40 + 25;

            if (screenX >= btnX && screenX <= btnX + btnWidth &&
                y >= btnY && y <= btnY + btnHeight) {

                showDialogue = false;
                if (currentNearObject != null && currentNearObject.name.contains("Илья")) {
                    pendingDialogueIndex = 27;
                    pendingBackgroundIndex = 2;
                    game.setScreen(new Level1Screen(game, false));
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (pointer == joystickPointer) {
            joystickTouched = false;
            joystickPointer = -1;
            moveDirection.set(0, 0);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        float y = Gdx.graphics.getHeight() - screenY;
        if (joystickTouched && pointer == joystickPointer && !showDialogue) {
            updateMoveDirection(screenX - joystickCenter.x, y - joystickCenter.y);
            return true;
        }
        return false;
    }

    private void updateMoveDirection(float dx, float dy) {
        float distance = (float)Math.sqrt(dx * dx + dy * dy);
        if (distance > joystickRadius) {
            dx = (dx / distance) * joystickRadius;
            dy = (dy / distance) * joystickRadius;
        }
        if (distance > 10) {
            moveDirection.set(dx / joystickRadius, dy / joystickRadius);
        } else {
            moveDirection.set(0, 0);
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        uiCamera.setToOrtho(false, width, height);
        updateCamera();
        camera.update();

        joystickCenter.set(joystickRadius + 60, joystickRadius + 60);
        interactButtonCenter.set(width - interactButtonRadius - 60, interactButtonRadius + 60);
        speedButtonCenter.set(width - 250, height - 120);
        speedButton = new Circle(speedButtonCenter, speedButtonRadius);
    }

    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        if (font != null) font.dispose();
        if (nameFont != null) nameFont.dispose();
    }

    public void setDialogueIndex(int index) {}
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
}
