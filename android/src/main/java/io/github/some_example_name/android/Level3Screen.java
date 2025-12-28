package io.github.some_example_name.android;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Interpolation;

public class Level3Screen implements Screen {

    private final MainGame game;
    private Screen previousScreen;
    private boolean fromMenu = false;

    private static final int GRID_WIDTH = 9;
    private static final int GRID_HEIGHT = 6;
    private static final int TARGET_SCORE = 8500;
    private static final float TIME_LIMIT = 30f;

    private int cellSize;
    private int offsetX, offsetY;

    private Texture appleTex, honeyTex, jellyTex, pieTex, porridgeTex;

    private enum ItemType { APPLE, HONEY, JELLY, PIE, PORRIDGE, EMPTY }
    private ItemType[][] grid = new ItemType[GRID_WIDTH][GRID_HEIGHT];

    private float[][] pieceX = new float[GRID_WIDTH][GRID_HEIGHT];
    private float[][] pieceY = new float[GRID_WIDTH][GRID_HEIGHT];

    private int score = 0;
    private float timeLeft = TIME_LIMIT;
    private boolean gameWon = false;

    private Vector2 touchStart = new Vector2();
    private Vector2 touchEnd = new Vector2();
    private boolean isDragging = false;

    private boolean isSwapping = false;
    private float swapTime = 0f;
    private int swapX1, swapY1, swapX2, swapY2;

    private boolean isMatchPopping = false;
    private float matchTime = 0f;
    private boolean[][] matches = new boolean[GRID_WIDTH][GRID_HEIGHT];

    private boolean isDropping = false;
    private float dropTime = 0f;

    private BitmapFont font;
    private GlyphLayout layout = new GlyphLayout();

    private enum State { INSTRUCTIONS, PLAYING, FINISHED }
    private State state = State.INSTRUCTIONS;

    public Level3Screen(MainGame game) {
        this.game = game;
        this.fromMenu = true;
        initTextures();
        initFont();
        resetGame();
    }

    public Level3Screen(MainGame game, Screen previousScreen) {
        this.game = game;
        this.previousScreen = previousScreen;
        this.fromMenu = false;
        initTextures();
        initFont();
        resetGame();
    }

    public void setFromMenu(boolean fromMenu) {
        this.fromMenu = fromMenu;
    }

    public void setPreviousScreen(Screen previousScreen) {
        this.previousScreen = previousScreen;
    }

    private void initTextures() {
        try {
            appleTex = new Texture(Gdx.files.internal("apple.png"));
            honeyTex = new Texture(Gdx.files.internal("honey.png"));
            jellyTex = new Texture(Gdx.files.internal("jelly.png"));
            pieTex = new Texture(Gdx.files.internal("pie.png"));
            porridgeTex = new Texture(Gdx.files.internal("porridge.png"));
        } catch (Exception ignored) {}
    }

    private void initFont() {
        try {
            FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Regular.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
            p.size = 42;
            p.color = new Color(0.18f, 0.08f, 0.03f, 1f);
            p.characters = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ0123456789.,!? ";
            font = gen.generateFont(p);
            gen.dispose();
        } catch (Exception ignored) {}
    }

    private void updateLayout() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        cellSize = Math.min((int)(w * 0.88f / GRID_WIDTH), (int)(h * 0.82f / GRID_HEIGHT));
        offsetX = (int)((w - GRID_WIDTH * cellSize) / 2f);
        offsetY = (int)(h * 0.09f);
        syncPiecePositions();
    }

    private void resetGame() {
        score = 0;
        timeLeft = TIME_LIMIT;
        gameWon = false;
        state = State.INSTRUCTIONS;
        isSwapping = isMatchPopping = isDropping = false;
        initGrid();
    }

    private void initGrid() {
        updateLayout();
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                if (Math.random() < 0.12f) {
                    grid[x][y] = ItemType.EMPTY;
                } else {
                    grid[x][y] = ItemType.values()[(int)(Math.random() * 5)];
                }
            }
        }
        syncPiecePositions();
        removeInitialMatches();
    }

    private void syncPiecePositions() {
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                pieceX[x][y] = offsetX + x * cellSize + cellSize * 0.5f;
                pieceY[x][y] = offsetY + y * cellSize + cellSize * 0.5f;
            }
        }
    }

    private Texture getTexture(ItemType type) {
        if (type == ItemType.EMPTY) return null;
        switch (type) {
            case APPLE: return appleTex;
            case HONEY: return honeyTex;
            case JELLY: return jellyTex;
            case PIE: return pieTex;
            case PORRIDGE: return porridgeTex;
            default: return null;
        }
    }

    private int getCellX(float screenX) {
        return MathUtils.clamp((int)((screenX - offsetX) / cellSize), 0, GRID_WIDTH - 1);
    }

    private int getCellY(float screenY) {
        return MathUtils.clamp((int)((screenY - offsetY) / cellSize), 0, GRID_HEIGHT - 1);
    }

    private void handleInput() {
        if (state != State.PLAYING || isSwapping || isMatchPopping || isDropping) return;

        if (Gdx.input.justTouched()) {
            touchStart.set(Gdx.input.getX(), Gdx.graphics.getHeight() - 1 - Gdx.input.getY());
            isDragging = true;
            return;
        }

        if (isDragging && Gdx.input.isTouched()) {
            touchEnd.set(Gdx.input.getX(), Gdx.graphics.getHeight() - 1 - Gdx.input.getY());
        }

        if (isDragging && !Gdx.input.isTouched()) {
            isDragging = false;

            float dx = touchEnd.x - touchStart.x;
            float dy = touchEnd.y - touchStart.y;

            int startX = getCellX(touchStart.x);
            int startY = getCellY(touchStart.y);

            if (grid[startX][startY] == ItemType.EMPTY) return;

            int endX = getCellX(touchEnd.x);
            int endY = getCellY(touchEnd.y);

            if (Math.abs(endX - startX) + Math.abs(endY - startY) == 1) {
                startSwap(startX, startY, endX, endY);
            }
        }
    }

    private void startSwap(int x1, int y1, int x2, int y2) {
        isSwapping = true;
        swapTime = 0f;
        swapX1 = x1; swapY1 = y1;
        swapX2 = x2; swapY2 = y2;

        ItemType temp = grid[x1][y1];
        grid[x1][y1] = grid[x2][y2];
        grid[x2][y2] = temp;
    }

    private void updateSwap(float delta) {
        if (!isSwapping) return;

        swapTime += delta * 12f;
        if (swapTime >= 1f) {
            isSwapping = false;

            findMatches();
            if (hasMatches()) {
                isMatchPopping = true;
                matchTime = 0f;
            } else {
                ItemType temp = grid[swapX1][swapY1];
                grid[swapX1][swapY1] = grid[swapX2][swapY2];
                grid[swapX2][swapY2] = temp;
                syncPiecePositions();
            }
        }
    }

    private void findMatches() {
        clearMatches();
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x <= GRID_WIDTH - 3; x++) {
                ItemType type = grid[x][y];
                if (type != ItemType.EMPTY && type == grid[x+1][y] && type == grid[x+2][y]) {
                    matches[x][y] = matches[x+1][y] = matches[x+2][y] = true;
                }
            }
        }
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y <= GRID_HEIGHT - 3; y++) {
                ItemType type = grid[x][y];
                if (type != ItemType.EMPTY && type == grid[x][y+1] && type == grid[x][y+2]) {
                    matches[x][y] = matches[x][y+1] = matches[x][y+2] = true;
                }
            }
        }
    }

    private boolean hasMatches() {
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                if (matches[x][y]) return true;
            }
        }
        return false;
    }

    private void clearMatches() {
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                matches[x][y] = false;
            }
        }
    }

    private void updateMatchPopping(float delta) {
        if (!isMatchPopping) return;

        matchTime += delta * 6f;
        if (matchTime >= 1f) {
            int points = 0;
            for (int x = 0; x < GRID_WIDTH; x++) {
                for (int y = 0; y < GRID_HEIGHT; y++) {
                    if (matches[x][y]) {
                        grid[x][y] = ItemType.EMPTY;
                        points += 150;
                    }
                }
            }
            score += points;
            clearMatches();
            isMatchPopping = false;
            startDrop();
        }
    }

    private void startDrop() {
        isDropping = true;
        dropTime = 0f;
        syncPiecePositions();
        applyGravity();
        syncPiecePositions();
    }

    private void updateDrop(float delta) {
        if (!isDropping) return;

        dropTime += delta * 2.5f;
        if (dropTime >= 1f) {
            isDropping = false;
            syncPiecePositions();
            findMatches();
            if (hasMatches()) {
                isMatchPopping = true;
                matchTime = 0f;
            }
            return;
        }

        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                if (grid[x][y] != ItemType.EMPTY) {
                    float targetY = offsetY + y * cellSize + cellSize * 0.5f;
                    pieceY[x][y] -= 100 * delta;
                    if (pieceY[x][y] < targetY) {
                        pieceY[x][y] = targetY;
                    }
                }
            }
        }
    }

    private void applyGravity() {
        for (int x = 0; x < GRID_WIDTH; x++) {
            int writeY = 0;
            for (int readY = 0; readY < GRID_HEIGHT; readY++) {
                if (grid[x][readY] != ItemType.EMPTY) {
                    if (writeY != readY) {
                        grid[x][writeY] = grid[x][readY];
                        grid[x][readY] = ItemType.EMPTY;
                    }
                    writeY++;
                }
            }
            for (int y = writeY; y < GRID_HEIGHT; y++) {
                if (Math.random() < 0.85f) {
                    grid[x][y] = ItemType.values()[(int)(Math.random() * 5)];
                }
            }
        }
    }

    private void removeInitialMatches() {
        boolean hadMatches;
        do {
            hadMatches = false;
            findMatches();
            if (hasMatches()) {
                hadMatches = true;
                for (int x = 0; x < GRID_WIDTH; x++) {
                    for (int y = 0; y < GRID_HEIGHT; y++) {
                        if (matches[x][y]) grid[x][y] = ItemType.EMPTY;
                    }
                }
                applyGravity();
            }
        } while (hadMatches);
        syncPiecePositions();
    }

    private void updateGame(float delta) {
        if (state != State.PLAYING) return;

        timeLeft -= delta;
        if (timeLeft <= 0 || score >= TARGET_SCORE) {
            state = State.FINISHED;
            gameWon = score >= TARGET_SCORE;
            return;
        }

        if (isSwapping) updateSwap(delta);
        if (isMatchPopping) updateMatchPopping(delta);
        if (isDropping) updateDrop(delta);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.95f, 0.90f, 0.82f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateLayout();
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        if (state == State.INSTRUCTIONS) {
            game.getBatch().begin();

            Color topColor = new Color(0.98f, 0.94f, 0.86f, 1f);
            Color bottomColor = new Color(0.90f, 0.84f, 0.76f, 1f);

            game.getBatch().setColor(topColor);
            game.getBatch().draw(game.whitePixelTexture, 0, h*0.6f, w, h*0.4f);
            game.getBatch().setColor(bottomColor);
            game.getBatch().draw(game.whitePixelTexture, 0, 0, w, h*0.6f);
            game.getBatch().setColor(Color.WHITE);

            if (font != null) {
                layout.setText(font, "ЯРМАРКА КУРСКА");
                font.draw(game.getBatch(), layout, (w - layout.width) * 0.5f, h * 0.82f);

                layout.setText(font, "СВАЙПНИ товар к соседу!");
                font.draw(game.getBatch(), layout, (w - layout.width) * 0.5f, h * 0.70f);

                layout.setText(font, "3+ в ряд = 150 монет");
                font.draw(game.getBatch(), layout, (w - layout.width) * 0.5f, h * 0.60f);

                layout.setText(font, "Цель: 8500 за 30 сек");
                font.draw(game.getBatch(), layout, (w - layout.width) * 0.5f, h * 0.50f);

                layout.setText(font, "Свайпни чтобы играть!");
                font.draw(game.getBatch(), layout, (w - layout.width) * 0.5f, h * 0.40f);
            }
            game.getBatch().end();

            if (Gdx.input.justTouched()) state = State.PLAYING;
            return;
        }

        handleInput();
        updateGame(delta);

        game.getBatch().begin();

        Color topColor = new Color(0.98f, 0.94f, 0.86f, 1f);
        Color bottomColor = new Color(0.90f, 0.84f, 0.76f, 1f);

        game.getBatch().setColor(topColor);
        game.getBatch().draw(game.whitePixelTexture, 0, h*0.6f, w, h*0.4f);
        game.getBatch().setColor(bottomColor);
        game.getBatch().draw(game.whitePixelTexture, 0, 0, w, h*0.6f);
        game.getBatch().setColor(Color.WHITE);

        game.getBatch().setColor(0.85f, 0.76f, 0.66f, 1f);
        game.getBatch().draw(game.whitePixelTexture,
            offsetX - 30, offsetY - 30,
            GRID_WIDTH * cellSize + 60, GRID_HEIGHT * cellSize + 60);
        game.getBatch().setColor(Color.WHITE);

        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                Texture tex = getTexture(grid[x][y]);
                if (tex == null) continue;

                float drawX = pieceX[x][y] - cellSize * 0.4f;
                float drawY = pieceY[x][y] - cellSize * 0.4f;
                float alpha = 1f;

                if (isSwapping && ((x == swapX1 && y == swapY1) || (x == swapX2 && y == swapY2))) {
                    float t = Interpolation.sineOut.apply(swapTime);
                    if (x == swapX1 && y == swapY1) {
                        drawX = MathUtils.lerp(pieceX[x][y], pieceX[swapX2][swapY2], t) - cellSize * 0.4f;
                        drawY = MathUtils.lerp(pieceY[x][y], pieceY[swapX2][swapY2], t) - cellSize * 0.4f;
                    } else {
                        drawX = MathUtils.lerp(pieceX[x][y], pieceX[swapX1][swapY1], t) - cellSize * 0.4f;
                        drawY = MathUtils.lerp(pieceY[x][y], pieceY[swapX1][swapY1], t) - cellSize * 0.4f;
                    }
                }

                if (isMatchPopping && matches[x][y]) {
                    alpha = 1f - matchTime;
                }

                game.getBatch().setColor(1f, 1f, 1f, alpha);
                game.getBatch().draw(tex, drawX, drawY, cellSize * 0.8f, cellSize * 0.8f);
            }
        }

        game.getBatch().setColor(Color.WHITE);

        game.getBatch().setColor(0.98f, 0.96f, 0.90f, 0.95f);
        game.getBatch().draw(game.whitePixelTexture,
            offsetX - 40, offsetY + GRID_HEIGHT * cellSize + 30,
            GRID_WIDTH * cellSize + 80, 90);
        game.getBatch().setColor(0.65f, 0.52f, 0.42f, 1f);
        game.getBatch().draw(game.whitePixelTexture,
            offsetX - 35, offsetY + GRID_HEIGHT * cellSize + 33,
            GRID_WIDTH * cellSize + 70, 10);
        game.getBatch().draw(game.whitePixelTexture,
            offsetX - 35, offsetY + GRID_HEIGHT * cellSize + 100,
            GRID_WIDTH * cellSize + 70, 10);
        game.getBatch().setColor(Color.WHITE);

        if (font != null) {
            String scoreText = score + " / " + TARGET_SCORE;
            layout.setText(font, scoreText);
            font.draw(game.getBatch(), layout, offsetX + 50, offsetY + GRID_HEIGHT * cellSize + 85);

            String timeText = String.format("%.0fs", Math.max(0, timeLeft));
            layout.setText(font, timeText);
            font.draw(game.getBatch(), layout,
                offsetX + GRID_WIDTH * cellSize - layout.width - 45,
                offsetY + GRID_HEIGHT * cellSize + 85);
        }

        if (state == State.FINISHED) {
            String result = gameWon ? "ПОБЕДА!" : "ВРЕМЯ КОНЧИЛОСЬ!";
            float panelW = w * 0.85f;
            float panelH = 220;
            float panelX = (w - panelW) * 0.5f;
            float panelY = (h - panelH) * 0.5f;

            game.getBatch().setColor(1f, 0.98f, 0.95f, 0.98f);
            game.getBatch().draw(game.whitePixelTexture, panelX, panelY, panelW, panelH);

            game.getBatch().setColor(0.72f, 0.60f, 0.50f, 1f);
            game.getBatch().draw(game.whitePixelTexture, panelX + 10, panelY + 10, panelW - 20, 15);
            game.getBatch().draw(game.whitePixelTexture, panelX + 10, panelY + panelH - 25, panelW - 20, 15);
            game.getBatch().draw(game.whitePixelTexture, panelX + 10, panelY + 10, 15, panelH - 20);
            game.getBatch().draw(game.whitePixelTexture, panelX + panelW - 25, panelY + 10, 15, panelH - 20);
            game.getBatch().setColor(Color.WHITE);

            if (font != null) {
                layout.setText(font, result);
                font.draw(game.getBatch(), layout, panelX + (panelW - layout.width) * 0.5f, panelY + panelH * 0.7f);

                layout.setText(font, "ТАПНИ ДАЛЬШЕ");
                font.draw(game.getBatch(), layout, panelX + (panelW - layout.width) * 0.5f, panelY + panelH * 0.3f);
            }
        }

        game.getBatch().end();
            if (state == State.FINISHED && Gdx.input.justTouched()) {
            game.setLevelCompleted(2, gameWon);

            if (fromMenu || previousScreen == null) {
                game.setScreen(new LevelMenuScreen(game));
            } else {
                if (previousScreen instanceof Scene3Screen) {
                    Scene3Screen scene = (Scene3Screen) previousScreen;
                    scene.setDialogueIndex(24);
                    scene.setShowPanel(true);
                    game.setScreen(scene);
                } else {
                    game.setScreen(previousScreen);
                }
            }
        }
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) { updateLayout(); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (appleTex != null) appleTex.dispose();
        if (honeyTex != null) honeyTex.dispose();
        if (jellyTex != null) jellyTex.dispose();
        if (pieTex != null) pieTex.dispose();
        if (porridgeTex != null) porridgeTex.dispose();
        if (font != null) font.dispose();
    }
}

