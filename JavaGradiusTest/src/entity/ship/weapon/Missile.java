package entity.ship.weapon;


import entity.Ship;
import infra.CollisionBuffer;
import infra.Entity;
import infra.Terrain;
import infra.View;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 *
 * @author admin
 */
public class Missile extends Entity {
    
    public int index;
    public boolean free = true;
    public double vx;
    public double vy;
    public Ship ship;
    public Terrain terrain;

    public Missile(View view, Terrain terrain, Ship ship, int index) {
        super(view);
        this.terrain = terrain;
        this.ship = ship;
        this.index = index;
        switch (index) {
            case 1:
                type = CollisionBuffer.SHIP_BULLET_1;
                break;
            case 2:
                type = CollisionBuffer.SHIP_BULLET_2;
                break;
            case 3:
                type = CollisionBuffer.SHIP_BULLET_3;
                break;
            default:
                break;
        }
    }

    @Override
    public void start() {
        loadFrame("weapon_missile_0");
        loadFrame("weapon_missile_1");
    }
    
    public void fire(int sx, int sy) {
        x = sx + 16;
        y = sy + 8;
        vx = 2;
        vy = 4;
        free = false;
        frameIndex = 0;
    }

    @Override
    public void update() {
        if (free) return;

        // clear collision buffer
        CollisionBuffer.clear(collisionArea.x, collisionArea.y, type);
        CollisionBuffer.clear(collisionArea.x + 1, collisionArea.y, type);
        
        if (x > 320 || y > 200) {
            free = true;
            return;
        }
        
        x += vx;
        y += vy;

        checkCollisionWithTerrain();
        
        // set collision buffer
        getCollisionArea(0);
        CollisionBuffer.set(collisionArea.x, collisionArea.y, type);
        CollisionBuffer.set(collisionArea.x + 1, collisionArea.y, type);
        
        if (CollisionBuffer.get(collisionArea.x, collisionArea.y, CollisionBuffer.TERRAIN)
                || CollisionBuffer.get(collisionArea.x + 1, collisionArea.y, CollisionBuffer.TERRAIN)) {
            destroy();
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (!free) {
            super.draw(g);
        }
    }

    @Override
    public void drawCollision(Graphics2D g) {
        //Point p = getCollisionArea(0);
        //int colX = p.x;
        //int colY = p.y;
        //g.setColor(Color.RED);
        //g.drawRect((int) (colX * 8), (int) (colY * 8), 8, 8);
    }
    
    @Override
    public Point getCollisionArea(int n) {
        int colX = (int) (x + 2) >> 3; //& 0b1111_1111_1111_1000;
        int colY = (int) (y + 2) >> 3; // & 0b1111_1111_1111_1000;
        collisionArea.setLocation(colX, colY);
        return collisionArea;
    }
    
    
    public void free() {
        free = true;
        // clear collision buffer
        CollisionBuffer.clear(collisionArea.x, collisionArea.y, type);
        CollisionBuffer.clear(collisionArea.x + 1, collisionArea.y, type);
    }
    
    public void checkCollisionWithTerrain() {
        boolean collides = true;
        while (collides && vy > 0) {
            int sx = (int) (x + 4);
            int sy = (int) (y + 10);
            if (terrain.isWall(sx, sy)) {
                y--;
                frameIndex = 1;
                vy = 0;
                vx = 4;
            }
            else {
                collides = false;
            }
        }

        if (vy == 0) {
            // hit subida, logo destroi
            int sx = (int) (x + 8);
            int sy = (int) (y + 4);
            if (terrain.isWall(sx, sy)) {
                free();
                return;
            }

            // keep down
            sx = (int) (x + 4);
            sy = (int) (y + 10);
            if (!terrain.isWall(sx, sy)) {
                vx = 2;
                vy = 4;
                frameIndex = 0;
            }
        }
    }
    
}
