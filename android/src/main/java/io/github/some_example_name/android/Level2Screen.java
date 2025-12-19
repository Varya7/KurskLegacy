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
import com.badlogic.gdx.utils.Align;

public class Level2Screen implements Screen {

    private final MainGame game;

    private static final int GRID_WIDTH = 10;
    private static final int GRID_HEIGHT = 12;
    private static final int CELL_SIZE = 64;

    private int offsetX;
    private int offsetY;

    private int[][] grid = new int[GRID_WIDTH][GRID_HEIGHT];

    private Texture blockTex;

    private enum PieceType { I, O, T, S, Z, J, L }

    private static class Piece {
        PieceType type;
        int x, y;
        int rot;
    }

    private Piece current;
    private Piece next;

    private float fallTimer = 0f;
    private float normalFallInterval = 0.7f;
    private float fastFallInterval = 0.1f;
    private boolean fastDrop = false;

    private Vector2 touchStart = new Vector2();
    private boolean touchActive = false;
    private float moveTimer = 0f;

    private int targetLines = 5;
    private int linesCleared = 0;
    private boolean win = false;
    private boolean gameOver = false;

    private BitmapFont font;
    private GlyphLayout layout = new GlyphLayout();

    private enum State { INSTRUCTIONS, RUNNING, FINISHED }
    private State state = State.INSTRUCTIONS;

    private float lockDelay = 0.3f;
    private float lockTimer = 0f;
    private boolean onGround = false;


    public Level2Screen(MainGame game) {
        this.game = game;

        blockTex = new Texture(Gdx.files.internal("block.png"));

        offsetX = (Gdx.graphics.getWidth() - GRID_WIDTH * CELL_SIZE) / 2;
        offsetY = (Gdx.graphics.getHeight() - GRID_HEIGHT * CELL_SIZE) / 2;

        FreeTypeFontGenerator gen =
            new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter p =
            new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.size = 42;
        p.color = new Color(0.3f, 0.18f, 0.08f, 1f);
        p.borderWidth = 0f;
        p.shadowOffsetX = 0;
        p.shadowOffsetY = 0;
        p.characters =
            "абвгдеёжзийклмнопрстуфхцчшщъыьэюя" +
                "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" +
                "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "0123456789.,!?-—–«»\"':;() ";
        font = gen.generateFont(p);
        gen.dispose();

        next = randomPiece();
        spawnNewPieceFromNext();
    }

    private Piece randomPiece() {
        Piece p = new Piece();
        int r = (int)(Math.random() * 7);
        switch (r) {
            case 0: p.type = PieceType.I; break;
            case 1: p.type = PieceType.O; break;
            case 2: p.type = PieceType.T; break;
            case 3: p.type = PieceType.S; break;
            case 4: p.type = PieceType.Z; break;
            case 5: p.type = PieceType.J; break;
            default: p.type = PieceType.L; break;
        }
        p.rot = 0;
        return p;
    }

    private void spawnNewPieceFromNext() {
        current = new Piece();
        current.type = next.type;
        current.rot = 0;
        current.x = GRID_WIDTH / 2;
        current.y = GRID_HEIGHT - 2;

        if (collides(current.x, current.y, current.type, current.rot)) {
            gameOver = true;
            state = State.FINISHED;
            return;
        }
        next = randomPiece();
    }

    private int[][] cellsFor(PieceType type, int cx, int cy, int rot) {
        if (type == PieceType.I) {
            if (rot % 2 == 0) {
                return new int[][]{
                    {cx - 2, cy}, {cx - 1, cy}, {cx, cy}, {cx + 1, cy}
                };
            } else {
                return new int[][]{
                    {cx, cy - 2}, {cx, cy - 1}, {cx, cy}, {cx, cy + 1}
                };
            }
        } else if (type == PieceType.O) {
            return new int[][]{
                {cx,     cy},
                {cx + 1, cy},
                {cx,     cy + 1},
                {cx + 1, cy + 1}
            };
        } else if (type == PieceType.T) {
            if (rot == 0) {
                return new int[][]{
                    {cx - 1, cy}, {cx, cy}, {cx + 1, cy}, {cx, cy + 1}
                };
            } else if (rot == 1) {
                return new int[][]{
                    {cx, cy - 1}, {cx, cy}, {cx, cy + 1}, {cx - 1, cy}
                };
            } else if (rot == 2) {
                return new int[][]{
                    {cx - 1, cy}, {cx, cy}, {cx + 1, cy}, {cx, cy - 1}
                };
            } else {
                return new int[][]{
                    {cx, cy - 1}, {cx, cy}, {cx, cy + 1}, {cx + 1, cy}
                };
            }
        } else if (type == PieceType.S) {
            if (rot % 2 == 0) {
                return new int[][]{
                    {cx - 1, cy}, {cx, cy}, {cx, cy + 1}, {cx + 1, cy + 1}
                };
            } else {
                return new int[][]{
                    {cx, cy - 1}, {cx, cy}, {cx - 1, cy}, {cx - 1, cy + 1}
                };
            }
        } else if (type == PieceType.Z) {
            if (rot % 2 == 0) {
                return new int[][]{
                    {cx, cy}, {cx + 1, cy}, {cx - 1, cy + 1}, {cx, cy + 1}
                };
            } else {
                return new int[][]{
                    {cx - 1, cy - 1}, {cx - 1, cy}, {cx, cy}, {cx, cy + 1}
                };
            }
        } else if (type == PieceType.J) {
            if (rot == 0) {
                return new int[][]{
                    {cx - 1, cy}, {cx, cy}, {cx + 1, cy}, {cx + 1, cy + 1}
                };
            } else if (rot == 1) {
                return new int[][]{
                    {cx, cy - 1}, {cx, cy}, {cx, cy + 1}, {cx - 1, cy + 1}
                };
            } else if (rot == 2) {
                return new int[][]{
                    {cx - 1, cy - 1}, {cx - 1, cy}, {cx, cy}, {cx + 1, cy}
                };
            } else {
                return new int[][]{
                    {cx + 1, cy - 1}, {cx, cy - 1}, {cx, cy}, {cx, cy + 1}
                };
            }
        } else { // L
            if (rot == 0) {
                return new int[][]{
                    {cx - 1, cy}, {cx, cy}, {cx + 1, cy}, {cx - 1, cy + 1}
                };
            } else if (rot == 1) {
                return new int[][]{
                    {cx, cy - 1}, {cx, cy}, {cx, cy + 1}, {cx - 1, cy - 1}
                };
            } else if (rot == 2) {
                return new int[][]{
                    {cx - 1, cy}, {cx, cy}, {cx + 1, cy}, {cx + 1, cy - 1}
                };
            } else {
                return new int[][]{
                    {cx, cy - 1}, {cx, cy}, {cx, cy + 1}, {cx + 1, cy + 1}
                };
            }
        }
    }

    private boolean collides(int cx, int cy, PieceType type, int rot) {
        int[][] cells = cellsFor(type, cx, cy, rot);
        for (int[] c : cells) {
            int x = c[0];
            int y = c[1];
            if (x < 0 || x >= GRID_WIDTH || y < 0 || y >= GRID_HEIGHT) return true;
            if (grid[x][y] != 0) return true;
        }
        return false;
    }

    private void lockPiece() {
        int[][] cells = cellsFor(current.type, current.x, current.y, current.rot);
        for (int[] c : cells) {
            int x = c[0];
            int y = c[1];
            if (x >= 0 && x < GRID_WIDTH && y >= 0 && y < GRID_HEIGHT) {
                grid[x][y] = 1;
            }
        }
        clearFullLines();

        if (linesCleared >= targetLines) {
            win = true;
            state = State.FINISHED;
            return;
        }

        spawnNewPieceFromNext();
    }

    private void clearFullLines() {
        for (int y = 0; y < GRID_HEIGHT; y++) {
            boolean full = true;
            for (int x = 0; x < GRID_WIDTH; x++) {
                if (grid[x][y] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                linesCleared++;
                for (int yy = y; yy < GRID_HEIGHT - 1; yy++) {
                    for (int x = 0; x < GRID_WIDTH; x++) {
                        grid[x][yy] = grid[x][yy + 1];
                    }
                }
                for (int x = 0; x < GRID_WIDTH; x++) {
                    grid[x][GRID_HEIGHT - 1] = 0;
                }
                y--;
            }
        }
    }

    private void rotate() {
        int newRot = (current.rot + 1) % 4;
        if (!collides(current.x, current.y, current.type, newRot)) {
            current.rot = newRot;
        }
    }

    private void handleInput(float delta) {
        moveTimer += delta;

        if (Gdx.input.justTouched()) {
            touchActive = true;
            touchStart.set(Gdx.input.getX(), Gdx.input.getY());
        }

        if (touchActive && !Gdx.input.isTouched()) {
            Vector2 end = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            Vector2 diff = end.cpy().sub(touchStart);

            float absX = Math.abs(diff.x);
            float absY = Math.abs(diff.y);
            float swipeThreshold = 40f;

            if (absX < swipeThreshold && absY < swipeThreshold) {
                if (!collides(current.x, current.y, current.type, (current.rot + 1) % 4)) {
                    rotate();
                    lockTimer = 0f;
                    onGround = false;
                }
            } else if (absX > absY) {
                if (diff.x < 0 && !collides(current.x - 1, current.y, current.type, current.rot)) {
                    current.x--;
                    lockTimer = 0f;
                    onGround = false;
                } else if (diff.x > 0 && !collides(current.x + 1, current.y, current.type, current.rot)) {
                    current.x++;
                    lockTimer = 0f;
                    onGround = false;
                }
            } else {
                if (diff.y > 0) {
                    fastDrop = true;
                }
            }


            touchActive = false;
            moveTimer = 0f;
        }
    }

    private void updateGame(float delta) {
        if (state != State.RUNNING || win || gameOver) return;

        handleInput(delta);

        fallTimer += delta;
        float interval = fastDrop ? fastFallInterval : normalFallInterval;

        if (fallTimer >= interval) {
            if (!collides(current.x, current.y - 1, current.type, current.rot)) {
                current.y--;
                onGround = false;
                lockTimer = 0f;
            } else {
                onGround = true;
                lockTimer += fallTimer;
                if (lockTimer >= lockDelay) {
                    lockPiece();
                    fastDrop = false;
                    onGround = false;
                    lockTimer = 0f;
                    for (int x = 0; x < GRID_WIDTH; x++) {
                        if (grid[x][GRID_HEIGHT - 1] != 0 && !win) {
                            gameOver = true;
                            state = State.FINISHED;
                            break;
                        }
                    }
                }
            }

            fallTimer = 0f;
        }
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.90f, 0.84f, 0.70f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        if (state == State.INSTRUCTIONS) {
            game.getBatch().begin();
            String text =
                "Строй стены детинца из блоков.\n" +
                    "Фигуры I, O, T, S, Z, J, L падают вниз.\n\n" +
                    "Свайп влево/вправо — движение,\n" +
                    "свайп вниз — ускорить падение,\n" +
                    "тап — поворот.\n\n" +
                    "Справа показана следующая фигура.\n" +
                    "Собери " + targetLines + " полных рядов.\n\n" +
                    "Нажми, чтобы начать.";
            layout.setText(font, text,
                new Color(0.3f, 0.18f, 0.08f, 1f),
                w - 80, Align.center, true);
            font.draw(game.getBatch(), layout,
                40, h * 0.65f);
            game.getBatch().end();

            if (Gdx.input.justTouched()) {
                state = State.RUNNING;
            }
            return;
        }

        updateGame(delta);

        game.getBatch().begin();

        game.getBatch().setColor(0.35f, 0.22f, 0.10f, 1f);
        game.getBatch().draw(game.whitePixelTexture,
            offsetX - 4, offsetY - 4,
            GRID_WIDTH * CELL_SIZE + 8, 4);
        game.getBatch().draw(game.whitePixelTexture,
            offsetX - 4, offsetY + GRID_HEIGHT * CELL_SIZE,
            GRID_WIDTH * CELL_SIZE + 8, 4);
        game.getBatch().draw(game.whitePixelTexture,
            offsetX - 4, offsetY - 4,
            4, GRID_HEIGHT * CELL_SIZE + 8);
        game.getBatch().draw(game.whitePixelTexture,
            offsetX + GRID_WIDTH * CELL_SIZE, offsetY - 4,
            4, GRID_HEIGHT * CELL_SIZE + 8);
        game.getBatch().setColor(Color.WHITE);

        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                if (grid[x][y] != 0) {
                    game.getBatch().draw(blockTex,
                        offsetX + x * CELL_SIZE,
                        offsetY + y * CELL_SIZE,
                        CELL_SIZE, CELL_SIZE);
                }
            }
        }

        if (!win && !gameOver && current != null) {
            int[][] cells = cellsFor(current.type, current.x, current.y, current.rot);
            for (int[] c : cells) {
                int x = c[0];
                int y = c[1];
                if (x < 0 || x >= GRID_WIDTH || y < 0 || y >= GRID_HEIGHT) continue;
                game.getBatch().draw(blockTex,
                    offsetX + x * CELL_SIZE,
                    offsetY + y * CELL_SIZE,
                    CELL_SIZE, CELL_SIZE);
            }
        }

        if (next != null) {
            int previewX = offsetX + GRID_WIDTH * CELL_SIZE + 40;
            int previewY = offsetY + GRID_HEIGHT * CELL_SIZE - 3 * CELL_SIZE;

            font.draw(game.getBatch(), "Далее:", previewX, previewY + 3 * CELL_SIZE + 20);


            int[][] ncells = cellsFor(next.type, 0, 0, 0);
            for (int[] c : ncells) {
                int dx = c[0];
                int dy = c[1];
                float px = previewX + (dx + 1.5f) * (CELL_SIZE / 2f);
                float py = previewY + (dy + 1.5f) * (CELL_SIZE / 2f);
                game.getBatch().draw(blockTex, px, py,
                    CELL_SIZE / 2f, CELL_SIZE / 2f);
            }
        }

        String progress = "Рядов: " + linesCleared + " / " + targetLines;
        font.draw(game.getBatch(), progress,
            offsetX, offsetY + GRID_HEIGHT * CELL_SIZE + 40);

        if (state == State.FINISHED) {
            String result = win ? "Крепость укреплена!" : "Попробуй еще раз.";

            float panelW = w - 80;
            float panelH = 180;
            float panelX = 40;
            float panelY = h / 2f - panelH / 2f;

            game.getBatch().setColor(1f, 0.98f, 0.9f, 0.95f);
            game.getBatch().draw(game.whitePixelTexture,
                panelX, panelY, panelW, panelH);

            game.getBatch().setColor(0.4f, 0.25f, 0.1f, 1f);
            game.getBatch().draw(game.whitePixelTexture,
                panelX, panelY, panelW, 4);
            game.getBatch().draw(game.whitePixelTexture,
                panelX, panelY + panelH - 4, panelW, 4);
            game.getBatch().draw(game.whitePixelTexture,
                panelX, panelY, 4, panelH);
            game.getBatch().draw(game.whitePixelTexture,
                panelX + panelW - 4, panelY, 4, panelH);

            game.getBatch().setColor(Color.WHITE);

            layout.setText(font, result,
                new Color(0.2f, 0.12f, 0.05f, 1f),
                panelW - 40, Align.center, true);
            font.draw(game.getBatch(), layout,
                panelX + 20, panelY + panelH / 2f + 20);
        }


        game.getBatch().end();

        if (state == State.FINISHED && Gdx.input.justTouched()) {
            if (win) {
                game.setLevelCompleted(1, true);
                game.setScreen(new LevelMenuScreen(game));
            } else {
                game.setScreen(new Level2Screen(game));
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
        if (blockTex != null) blockTex.dispose();
        if (font != null) font.dispose();
    }
}

