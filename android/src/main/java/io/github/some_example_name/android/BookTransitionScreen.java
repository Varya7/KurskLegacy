package io.github.some_example_name.android;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class BookTransitionScreen implements Screen {
    private final MainGame game;
    private Texture[] bookFrames;
    private Animation<TextureRegion> bookAnimation;
    private float animationTime = 0f;
    private boolean screenFinished = false;

    private final Color backgroundColor = new Color(0.22f, 0.14f, 0.08f, 1f);
    private final Screen nextScreen;

    public BookTransitionScreen(MainGame game, Screen nextScreen) {
        this.game = game;
        this.nextScreen = nextScreen;
        loadBookAnimation();
    }

    private void loadBookAnimation() {
        bookFrames = new Texture[12];
        TextureRegion[] frames = new TextureRegion[12];

        for (int i = 0; i < 12; i++) {
            String path = "bookanimation/bookanimation" + (i + 1) + ".png";
            try {
                bookFrames[i] = new Texture(Gdx.files.internal(path));
            } catch (Exception e) {
                if (i > 0 && bookFrames[0] != null) {
                    bookFrames[i] = bookFrames[0];
                }
            }
            frames[i] = new TextureRegion(bookFrames[i]);
        }

        bookAnimation = new Animation<TextureRegion>(0.083f, frames);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getBatch().setColor(Color.WHITE);
        game.getBatch().begin();

        delta = Math.min(delta, 0.016f);
        animationTime += delta;

        TextureRegion currentFrame = bookAnimation.getKeyFrame(animationTime, false);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float bookWidth = screenWidth * 0.20f;
        float bookHeight = bookWidth * (currentFrame.getRegionHeight() / (float) currentFrame.getRegionWidth());
        float x = (screenWidth - bookWidth) / 2f;
        float y = (screenHeight - bookHeight) / 2f - 30f;

        game.getBatch().draw(currentFrame, x, y, bookWidth, bookHeight);
        game.getBatch().end();

        if (animationTime >= 1.0f && !screenFinished) {
            screenFinished = true;
            Gdx.app.postRunnable(() -> {
                game.setScreen(nextScreen);
                nextScreen.show();
            });
        }

        if (Gdx.input.justTouched()) {
            game.setScreen(nextScreen);
            nextScreen.show();
        }
    }

    @Override
    public void show() {
        animationTime = 0f;
        screenFinished = false;
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (bookFrames != null) {
            for (Texture frame : bookFrames) {
                if (frame != null) frame.dispose();
            }
        }
    }
}
