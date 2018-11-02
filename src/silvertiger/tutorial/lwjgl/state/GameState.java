/*
 * The MIT License (MIT)
 *
 * Copyright © 2015-2016, Heiko Brumme
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package silvertiger.tutorial.lwjgl.state;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import silvertiger.tutorial.lwjgl.core.FixedTimestepGame;
import silvertiger.tutorial.lwjgl.core.Game;
import silvertiger.tutorial.lwjgl.graphic.Color;
import silvertiger.tutorial.lwjgl.graphic.Renderer;
import silvertiger.tutorial.lwjgl.graphic.Texture;
import silvertiger.tutorial.lwjgl.graphic.Window;
import silvertiger.tutorial.lwjgl.math.Matrix4f;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;

/**
 * This class contains a simple game.
 *
 * @author Heiko Brumme
 */
public class GameState implements State {

    private final Renderer renderer;
    private Texture texture;
    private int playerScore;
    private int opponentScore;
    private int gameWidth;
    private int gameHeight;
    private float anglePerSecond = 60F;
    private float angle;
    private float previousAngle;
    private final float ratio = FixedTimestepGame.window.width / (float) FixedTimestepGame.window.height;

    public GameState(Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void input() {
    }

    @Override
    public void update(float delta) {
        previousAngle = angle;
        angle += delta * anglePerSecond;
    }

    @Override
    public void render(float alpha) {
        /* Clear drawing area */

        Texture texture = Texture.loadTexture("G:\\360D\\icon.png");

        float lerpAngle = (1f - alpha) * previousAngle + alpha * angle;
        glClear(GL_COLOR_BUFFER_BIT);
        Matrix4f rotate = Matrix4f.translate(200,200,0).multiply(Matrix4f.rotate(lerpAngle,0,0,20000));
        renderer.program.setUniform(renderer.uniModel, rotate);
        renderer.begin();
        renderer.drawTexture(texture, 0, 0);
        renderer.end();
        /* Draw score */
        Matrix4f normal = Matrix4f.orthographic(-ratio, ratio, -1f, 2f, -2f, 90f);
        glViewport(0,0,1920,1080);
        renderer.program.setUniform(renderer.uniModel, normal);
        String scoreText = "Score";
        int scoreTextWidth = renderer.getTextWidth(scoreText);
        int scoreTextHeight = renderer.getTextHeight(scoreText);
        float scoreTextX = (gameWidth - scoreTextWidth) / 2f;
        float scoreTextY = gameHeight - scoreTextHeight - 5;
        renderer.drawText(scoreText, scoreTextX, scoreTextY, Color.BLACK);

        String playerText = "Player | " + playerScore;
        int playerTextWidth = renderer.getTextWidth(playerText);
        int playerTextHeight = renderer.getTextHeight(playerText);
        float playerTextX = gameWidth / 2f - playerTextWidth - 50;
        float playerTextY = scoreTextY - playerTextHeight;
        renderer.drawText(playerText, playerTextX, playerTextY, Color.BLACK);

        String opponentText = opponentScore + " | Opponent";
        int opponentTextHeight = renderer.getTextHeight(playerText);
        float opponentTextX = gameWidth / 2f + 50;
        float opponentTextY = scoreTextY - opponentTextHeight;
        renderer.drawText(opponentText, opponentTextX, opponentTextY, Color.BLACK);
    }

    @Override
    public void enter() {
        /* Get width and height of framebuffer */
        int width, height;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            long window = GLFW.glfwGetCurrentContext();
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            GLFW.glfwGetFramebufferSize(window, widthBuffer, heightBuffer);
            width = widthBuffer.get();
            height = heightBuffer.get();
        }

        /* Load texture */
        texture = Texture.loadTexture("F:\\Game\\JustShapesAndBeats_en\\JSB_Data\\StreamingAssets\\SaveIcon.png");
        Texture texture2 = Texture.loadTexture("G:\\PS\\640.webp.jpg");
        renderer.drawTexture(texture2, 20, 20);
        /* Initialize game objects */
        float speed = 250f;

        /* Initialize variables */
        playerScore = 0;
        opponentScore = 0;
        gameWidth = width;
        gameHeight = height;

        /* Set clear color to gray */
        glClearColor(0.5f, 0.5f, 0.5f, 1f);
    }

    @Override
    public void exit() {
        texture.delete();
    }

}
