package gameEntities.EntityProperties;

import org.dyn4j.dynamics.Body;

import java.awt.*;

public interface GameEntity {
    void draw(Graphics2D graphics);
    void update();
    void initialiseAnimations(String folderName, int spriteDimentions);
    HitBoxType getHitBoxType();
    Body getBody();

    boolean checkContact(GameEntity entityToCheck);

    int getHealth();

    void damage();
}
