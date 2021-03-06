package entity.enemy;

import entity.Ship;
import infra.Audio;
import infra.CollisionBuffer;
import infra.Entity;
import infra.View;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 *
 * @author admin
 */
public class EnemyB extends Entity {
    
    public double vy;
    public double vx;
    public Ship ship;
    public int keepY;
    
    public EnemyB(View view, Ship ship, int x, int y) {
        super(view);
        this.ship = ship;
        this.x = x;
        this.y = y;
        type = CollisionBuffer.ENEMY;
        vx = -2;
        vy = 0;
    }

    @Override
    public void start() {
        loadFrame("enemy_b_0");
        loadFrame("enemy_b_1");
        loadFrame("enemy_b_2");
    }
    
    @Override
    public void update() {
        // Para enemy , nao precisa usar o CollisionBuffer, basta mover e verificar a colisao com ship diretamente
        // ou com o ShipBullet
        
        frameIndex = 0;
        
        int dif = (int) ship.y - (int) y;
        if (dif == 0) {
            vy = 0;
            vx = -4;
            keepY = (int) (10 + 30 * Math.random());
        }
        else if (dif > 0) {
            if (keepY == 0) {
                vx = -2;
                vy = 1;
                frameIndex = 2;
            }
            else {
                keepY--;
            }
        }
        else if (dif < 0) {
            if (keepY == 0) {
                vx = -2;
                vy = -1;
                frameIndex = 1;
            }
            else {
                keepY--;
            }
        }
        
        x += vx;
        y += vy;
        
        checkCollision();

        if (x < -16) {
            destroy();
        }        
        
    }
    
    private void checkCollision() {

        // set collision buffer
        getCollisionArea(0);
        if (CollisionBuffer.get(collisionArea.x, collisionArea.y, CollisionBuffer.SHIP)
                || CollisionBuffer.get(collisionArea.x, collisionArea.y + 1, CollisionBuffer.SHIP)) {
            ship.explode();
            explode();
        }
        
        if (CollisionBuffer.get(collisionArea.x, collisionArea.y, CollisionBuffer.SHIP_BULLET_1)
                || CollisionBuffer.get(collisionArea.x, collisionArea.y + 1, CollisionBuffer.SHIP_BULLET_1)) {
            ship.destroyBullet(1);
            explode();
        }
        if (CollisionBuffer.get(collisionArea.x, collisionArea.y, CollisionBuffer.SHIP_BULLET_2)
                || CollisionBuffer.get(collisionArea.x, collisionArea.y + 1, CollisionBuffer.SHIP_BULLET_2)) {
            ship.destroyBullet(2);
            explode();
        }
        if (CollisionBuffer.get(collisionArea.x, collisionArea.y, CollisionBuffer.SHIP_BULLET_3)
                || CollisionBuffer.get(collisionArea.x, collisionArea.y + 1, CollisionBuffer.SHIP_BULLET_3)) {
            ship.destroyBullet(3);
            explode();
        }
        
        if (CollisionBuffer.get(collisionArea.x, collisionArea.y, CollisionBuffer.TERRAIN)
                || CollisionBuffer.get(collisionArea.x, collisionArea.y + 1, CollisionBuffer.TERRAIN)) {
            ship.destroy();
            explode();
        }
        
        // check collision with ship
        //if (getCollisionArea(0).equals(ship.getCollisionArea(0))
        //        || getCollisionArea(0).equals(ship.getCollisionArea(1))) {
        //    ship.destroy();
        //    destroy();
        //}        
    }


    @Override
    public void drawCollision(Graphics2D g) {
        Point p = getCollisionArea(0);
        int colX = p.x;
        int colY = p.y;
        g.setColor(Color.RED);
        g.drawRect((int) (colX * 8), (int) (colY * 8), 8, 8);
        g.drawRect((int) (colX * 8), (int) (colY * 8 + 8), 8, 8);
    }
    
    @Override
    public Point getCollisionArea(int n) {
        int colX = (int) (x + 8) >> 3; //& 0b1111_1111_1111_1000;
        int colY = (int) (y + 4) >> 3; // & 0b1111_1111_1111_1000;
        //if ((Time.frameCount % 2) == 1) {
        //    colY += 1;
        //}
        collisionArea.setLocation(colX, colY);
        return collisionArea;
    }

    @Override
    public void explode() {
        Audio.playSound("enemy_explosion");
        view.spawnEnemyExplosion((int) x, (int) y);
        destroy();
    }
    
}
