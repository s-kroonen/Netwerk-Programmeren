import Common.Camera;
import Common.DebugDraw;
import gameEntities.EnemyBomber;
import gameEntities.EntityProperties.HitBoxType;
import gameEntities.Bullet;
import gameEntities.EntityProperties.GameEntity;
import gameEntities.Player;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.WeldJoint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.jfree.fx.FXGraphics2D;
import org.jfree.fx.ResizableCanvas;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


public class Eindopdracht extends Application {


    //GUI attributes
    private ResizableCanvas canvas;
    private Camera camera;
    private BufferedImage background;
    private boolean keyHeld;

    //entity attributes
    private Player player;
    private boolean playerIsAlive;
    private World world;
    private ArrayList<GameEntity> entities;

    //game loop and debug attributes
    private int hiScore;
    private boolean debugSelected;
    double timePassed;

    public void init() {

        //initialising general information
        this.timePassed = 0.0;
        this.keyHeld = false;

        try {
            background = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/space background.jpg")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //setting up bounds, player, world and entities

        Body playerBody = new Body();
        playerBody.addFixture(Geometry.createRectangle(0.4, 0.4));
        playerBody.translate(new Vector2(-0.5, 0.5));
        playerBody.setMass(MassType.NORMAL);

        Body wallBody1 = new Body();
        wallBody1.addFixture(Geometry.createRectangle(0.5, 20));
        wallBody1.translate(new Vector2(-10, 0));
        wallBody1.setMass(MassType.INFINITE);

        Body wallBody2 = new Body();
        wallBody2.addFixture(Geometry.createRectangle(20, 0.5));
        wallBody2.translate(new Vector2(0, 10));
        wallBody2.setMass(MassType.NORMAL);

        Body wallBody3 = new Body();
        wallBody3.addFixture(Geometry.createRectangle(20, 0.5));
        wallBody3.translate(new Vector2(0, -10));
        wallBody3.setMass(MassType.NORMAL);

        Body wallBody4 = new Body();
        wallBody4.addFixture(Geometry.createRectangle(0.5, 20));
        wallBody4.translate(new Vector2(10, 0));
        wallBody4.setMass(MassType.NORMAL);


        //initialising world
        this.world = new World();
        world.setGravity(new Vector2(0, -1.62));
        world.addBody(playerBody);
        world.addBody(wallBody1);
        world.addBody(wallBody2);
        world.addBody(wallBody3);
        world.addBody(wallBody4);


        //adding weld joints so the walls stay together, (demonstrated the joints by making only one of the borders have an infinite mass)
        world.addJoint(new WeldJoint(wallBody1,wallBody2,new Vector2(wallBody1.getTransform().getTranslationX(),wallBody1.getTransform().getTranslationY())));
        world.addJoint(new WeldJoint(wallBody4,wallBody2,new Vector2(wallBody4.getTransform().getTranslationX(),wallBody4.getTransform().getTranslationY())));
        world.addJoint(new WeldJoint(wallBody1,wallBody3,new Vector2(wallBody1.getTransform().getTranslationX(),wallBody1.getTransform().getTranslationY())));
        world.addJoint(new WeldJoint(wallBody4,wallBody3,new Vector2(wallBody4.getTransform().getTranslationX(),wallBody4.getTransform().getTranslationY())));


        //initialising the player
        player = new Player(playerBody, "/Fighter", 192, 1, new Vector2(0, 0), entities);
        playerIsAlive = true;

        entities = new ArrayList<>();
        entities.add(player);
    }

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane mainPane = new BorderPane();
        canvas = new ResizableCanvas(this::draw, mainPane);
        mainPane.setCenter(canvas);
        FXGraphics2D graphics = new FXGraphics2D(canvas.getGraphicsContext2D());
        this.camera = new Camera(canvas, this::draw, graphics);
        init();

        new AnimationTimer() {
            long last = -1;

            @Override
            public void handle(long now) {
                if (last == -1) {
                    last = now;
                }
                update((now - last) / 1000000000.0);
                last = now;
                draw(graphics);
            }
        }.start();

        canvas.setFocusTraversable(true);
        canvas.setOnKeyTyped(this::keyPressed);
        canvas.setOnKeyReleased(this::keyReleased);

        stage.setScene(new Scene(mainPane));
        stage.setTitle("generic space game");
        stage.setFullScreen(true);
        stage.show();
        draw(graphics);
    }

    private void keyReleased(KeyEvent keyEvent) {
        player.keyReleased(keyEvent);
        keyHeld = false;
    }

    private void keyPressed(KeyEvent keyEvent) {
        if (keyHeld)
            return;

        keyHeld = true;
        player.keyPressed(keyEvent);
        switch (keyEvent.getCharacter().toLowerCase()) {
            case " ":
                Body bulletBody = new Body();
                bulletBody.addFixture(Geometry.createRectangle(0.25, 0.25));
                bulletBody.translate(new Vector2(player.getPlayerX(), player.getPlayerY()));
                bulletBody.setMass(MassType.NORMAL);

                world.addBody(bulletBody);

                entities.add(new Bullet(
                        "/Fighter", 28, player.getAngle(),
                        1, bulletBody, new Vector2(0, 0), HitBoxType.FRIENDLY, world, entities));
                break;
            case "p":
                Body enemyBomberBody = new Body();
                enemyBomberBody.addFixture(Geometry.createRectangle(1, 0.5));
                enemyBomberBody.translate(new Vector2(-5 + (Math.random() * 10), 5));
                enemyBomberBody.setMass(MassType.NORMAL);
                enemyBomberBody.setGravityScale(0);

                world.addBody(enemyBomberBody);

                entities.add(new EnemyBomber(
                        "/Bomber", 192,
                        enemyBomberBody, 1, new Vector2(0, 0)));
                break;
            case "0":
                debugSelected = !debugSelected;
                break;
            case "1":
                if (!playerIsAlive) {
                    Body playerBody = new Body();
                    playerBody.addFixture(Geometry.createRectangle(0.4, 0.4));
                    playerBody.translate(new Vector2(-0.5, 0.5));
                    playerBody.setMass(MassType.NORMAL);
                    world.addBody(playerBody);
                    player = new Player(playerBody, "/Fighter", 192, 1, new Vector2(0, 0), entities);
                    entities.add(player);
                    playerIsAlive = true;
                }
                break;
        }
    }

    private void update(double deltaTime) {
        if (playerIsAlive) {
            world.update(deltaTime);
            timePassed += deltaTime;
            player.updateEntities(entities);


            //updating all game entities and checking wether they are alive
            ArrayList<GameEntity> deadEntities = new ArrayList<>();
            for (GameEntity entity : entities) {
                entity.update();
                if (entity.getHealth() <= 0)
                    deadEntities.add(entity);
            }


            //removing dead game entities
            if (deadEntities.contains(player))
                playerIsAlive= false;
            for (GameEntity deadEntity : deadEntities) {
                if (deadEntity.getHitBoxType().equals(HitBoxType.ENEMY))
                    player.addPoints();
                entities.remove(deadEntity);
                world.removeBody(deadEntity.getBody());
            }


            //spawning in new enemies every 2 seconds
            if (timePassed > 2) {

                Body enemyBomberBody = new Body();
                enemyBomberBody.addFixture(Geometry.createRectangle(1, 0.5));
                enemyBomberBody.translate(new Vector2(-5 + (Math.random() * 10), -5 + (Math.random() * 10)));
                enemyBomberBody.setMass(MassType.NORMAL);
                enemyBomberBody.setGravityScale(0);

                world.addBody(enemyBomberBody);

                entities.add(new EnemyBomber(
                        "/Bomber", 192,
                        enemyBomberBody, 1, new Vector2(0, 0)));

                timePassed -= 2;
            }

            if (player.getPoints() >= hiScore)
                hiScore = player.getPoints();
        }
        else if (!entities.isEmpty()){
            for (GameEntity entity : entities) {
                world.removeBody(entity.getBody());
            }
            entities = new ArrayList<>();
        }
    }

    private void draw(FXGraphics2D graphics) {
        if (playerIsAlive) {
            graphics.transform(new AffineTransform());
            graphics.setBackground(Color.BLACK);
            graphics.clearRect(0, 0, (int) canvas.getWidth(), (int) canvas.getHeight());
            AffineTransform originalTransform = graphics.getTransform();


            graphics.setTransform(camera.getTransform((int) canvas.getWidth(), (int) canvas.getHeight()));
            graphics.drawImage(background, -500, -500, 1000, 1000, null);
            graphics.scale(0.5, -0.5);

            for (GameEntity entity : entities) {
                entity.draw(graphics);
            }

            if (debugSelected) {
                graphics.setColor(Color.white);
                DebugDraw.draw(graphics, world, 100);
            }


            graphics.setTransform(originalTransform);


            //showing all game information like health, score, highscore etc.
            AffineTransform tx = new AffineTransform();
            tx.translate(50, 50);
            Font font = new Font("ariel", Font.PLAIN, 50);
            Shape score = font.createGlyphVector(graphics.getFontRenderContext(), "sore : " + player.getPoints()).getOutline();
            Shape health = font.createGlyphVector(graphics.getFontRenderContext(), "Health : " + player.getHealth()).getOutline();
            Shape hiScoreBoard = font.createGlyphVector(graphics.getFontRenderContext(),  "Hi-Score : " +hiScore).getOutline();
            font = new Font("ariel", Font.PLAIN, 25);
            Shape instruction1 = font.createGlyphVector(graphics.getFontRenderContext(), "to fire press the Space button").getOutline();
            Shape instruction2 = font.createGlyphVector(graphics.getFontRenderContext(), "to move use the W-A-S-D keys").getOutline();
            Shape instruction3 = font.createGlyphVector(graphics.getFontRenderContext(), "to open debug draw press 0").getOutline();
            Shape instruction4 = font.createGlyphVector(graphics.getFontRenderContext(), "to spawn more enemies press P ").getOutline();
            graphics.setColor(Color.white);
            graphics.fill(tx.createTransformedShape(score));
            tx.translate(canvas.getWidth()/2, 0);
            graphics.fill(tx.createTransformedShape(health));
            tx.translate(canvas.getWidth()/2 - 500, 0);
            graphics.fill(tx.createTransformedShape(hiScoreBoard));
            tx.translate(-canvas.getWidth() + 500, 50 );
            graphics.fill(tx.createTransformedShape(instruction1));
            tx.translate(0, 50);
            graphics.fill(tx.createTransformedShape(instruction2));
            tx.translate(0, 50);
            graphics.fill(tx.createTransformedShape(instruction3));
            tx.translate(0, 50);
            graphics.fill(tx.createTransformedShape(instruction4));
        } else {

            //death screen
            graphics.transform(new AffineTransform());
            graphics.setBackground(Color.BLACK);
            graphics.clearRect(0, 0, (int) canvas.getWidth(), (int) canvas.getHeight());

            AffineTransform tx = new AffineTransform();
            tx.translate(50, 50);
            Font font = new Font("ariel", Font.PLAIN, 50);
            Shape score = font.createGlyphVector(graphics.getFontRenderContext(), "press 1 to try again").getOutline();
            graphics.setColor(Color.white);
            graphics.fill(tx.createTransformedShape(score));
        }
    }

}
