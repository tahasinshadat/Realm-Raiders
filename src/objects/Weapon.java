package objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import components.Entity;
import components.KeyHandler;
import components.MouseInteractions;
import components.Projectile;
import elements.Enemy;
import main.GamePanel;

public class Weapon extends GameObject {
    
    // GamePanel gamePanel;
    KeyHandler keyHandler;
    MouseInteractions mouse;
    
    // public double screenX;
    // public double screenY;
    public double angle;
    // public int width;
    // public int height;
    private boolean flipped = false;

    // stats
    public int weaponAttackSpeed = 5; // per second
    public double weaponProjectileSpeed = 10;
    public double weaponDamage = 20;
    public String weaponClass;
    public String weaponRarity;
    public String weaponName;
    public Color textColor;

    public Entity owner;
    private ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
    private int cooldown;

    public Weapon(GamePanel gamePanel, KeyHandler keyHandler, MouseInteractions mouse, Entity owner) {
        this.gamePanel = gamePanel;
        this.keyHandler = keyHandler;
        this.mouse = mouse;

        this.owner = owner;
        
        // x and y of player on screen
        this.screenX = this.gamePanel.screenWidth / 2 - this.gamePanel.tileSize / 2;
        this.screenY = this.gamePanel.screenHeight / 2 - this.gamePanel.tileSize / 2;
    }

    public void setData(double weaponAttackSpeed, double weaponProjectileSpeed, double weaponDamage) {
        this.weaponAttackSpeed = (int) weaponAttackSpeed;
        this.weaponProjectileSpeed = weaponProjectileSpeed;
        this.weaponDamage = weaponDamage;
        this.setMetaData();
        this.setImage();
    }

    public String getWeaponProperties() {
        StringBuilder properties = new StringBuilder();
        properties.append("WeaponClass: ").append(this.weaponClass).append("\n");
        properties.append("WeaponRarity: ").append(this.weaponRarity).append("\n");
        properties.append("WeaponAttackSpeed: ").append(this.weaponAttackSpeed).append("\n");
        properties.append("WeaponProjectileSpeed: ").append(this.weaponProjectileSpeed).append("\n");
        properties.append("WeaponDamage: ").append(this.weaponDamage).append("\n");
        properties.append("WeaponName: ").append(this.weaponName).append("\n");
        return properties.toString();  
    }

    public void setWeaponPropertiesFromString(String propertiesString) {
        String[] lines = propertiesString.split("\n");
        for (String line : lines) {
            if (line.startsWith("WeaponClass: ")) {
                this.weaponClass = line.substring(13);
            } else if (line.startsWith("WeaponRarity: ")) {
                this.weaponRarity = line.substring(15);
            } else if (line.startsWith("WeaponAttackSpeed: ")) {
                this.weaponAttackSpeed = Integer.parseInt(line.substring(19));
            } else if (line.startsWith("WeaponProjectileSpeed: ")) {
                this.weaponProjectileSpeed = Double.parseDouble(line.substring(23));
            } else if (line.startsWith("WeaponDamage: ")) {
                this.weaponDamage = Double.parseDouble(line.substring(14));
            } else if (line.startsWith("WeaponName: ")) {
                this.weaponName = line.substring(12);
            }
        }
        this.setData(this.weaponAttackSpeed, this.weaponProjectileSpeed, this.weaponDamage);
        this.setTextColor();  // Set text color based on weapon rarity
        this.setImage();
    }
    

    public double[] getData() {
        return new double[]{this.weaponAttackSpeed, this.weaponProjectileSpeed, this.weaponDamage};
    }

    public double getAngleToMouse() {
        double deltaX = mouse.getMouseX() - this.screenX;
        double deltaY = this.screenY - mouse.getMouseY();

        double radians = Math.atan(deltaY / deltaX);
        double degrees = Math.toDegrees(radians);
        
        // System.out.println("deltaX: " + deltaX + ", deltaY: " + deltaY);

        if (deltaX < 0) {
            // offsets to give positive angle where initial side is to the right (0 degrees)
            // Quadrant 2 - degrees returns -90 to 0, this offsets negative
            // Quadrant 3 - degrees returns 0 to 90. this offsets 180 degrees
            degrees += 180; 
        } else if (deltaY < 0) {
            // offsets to give positive angle where initial side is to the right (0 degrees)
            // Quadrant 4 - degrees returns -90 to 0, this offsets negative and 270 degrees
            degrees = 360 + degrees; // (degrees is negative)
        }

        // System.out.println(degrees + " degrees");
        return degrees;
    }

    public void update() {
        // updateSpeed();
        ArrayList<Projectile> toRemove = new ArrayList<Projectile>();
        this.angle = getAngleToMouse();
        
        for (Projectile projectile : projectiles) {
            projectile.update();

            if (projectile.active == false) {
                toRemove.add(projectile);
            }
        }
        projectiles.removeAll(toRemove);
        this.cooldown++;
    }

    // public void updateSpeed() {
    //     this.weaponProjectileSpeed *= (double) this.prevTileSize / this.gamePanel.tileSize;
    //     // System.out.println(this.weaponProjectileSpeed);
    //     this.prevTileSize = this.gamePanel.tileSize;
    // }

    public void setImage() {
        try {
            this.image = ImageIO.read(getClass().getResourceAsStream("../assets/weapons/" + this.weaponClass + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        // System.out.println("On ground?: " + this.onGround);

        if (!this.onGround) {
            g2.setColor(Color.WHITE);
            int tileSize = this.gamePanel.tileSize;

            this.updateValuesOnZoom();

            // x and y of player on screen
            this.screenX = this.gamePanel.player.screenX + tileSize/2;
            this.screenY = this.gamePanel.player.screenY + tileSize/2;
            
            double deltaX = mouse.getMouseX() - this.screenX;

            flipped = deltaX <= 0; // Keep image upright
            // g2.setColor(Color.WHITE);
            // g2.setColor(Color.RED);

            // rectangle with center that is colinear to y=0 and left side is at x=0 so that it orbits player when rotating
            // Rectangle rect2 = new Rectangle(tileSize/2, -this.height/2, this.width, this.height); 
            // g2.draw(rect2);
            // g2.fill(rect2);
            
            g2.translate(screenX, screenY); // translates origin and therefore weapon origin to center of player
            // g2.drawString(this.weaponRarity + " " + this.weaponClass, tileSize, tileSize);
            
            g2.rotate(-Math.toRadians(angle)); // rotated so y=0 aims at mouse
            g2.drawImage(this.image, 
                        tileSize/2, ((this.flipped) ? -1 : 1) * (int) -this.height/2, 
                        this.width, ((this.flipped) ? -1 : 1) * this.height, 
                        null);
            
            g2.rotate(Math.toRadians(angle)); // rotate back
            g2.translate(-screenX, -screenY); // translate back

            // Display weapon name
            g2.setColor(this.textColor);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            FontMetrics metrics = g2.getFontMetrics(g2.getFont());
            
            int strWidth = metrics.stringWidth(this.toString());
            g2.drawString(this.toString(), this.gamePanel.screenWidth-(strWidth + 10), this.gamePanel.screenHeight-10);
            
            g2.setFont(new Font("Arial", Font.BOLD, 12)); // reset g2
            g2.setColor(Color.WHITE);
            // drawProjectiles(g2);
        } else {
            // Display weapon name
            g2.setColor(this.textColor);
            super.draw(g2);
            super.drawName(g2);
            g2.setColor(Color.WHITE);
        }
    }

    public void setAsMeleeWeapon() {
        this.width = this.gamePanel.tileSize * 2;
        this.height = this.gamePanel.tileSize;
    }

    public void drawProjectiles(Graphics2D g2) {
        for (int i = 0; i < projectiles.size(); i++) {
            projectiles.get(i).draw(g2);
        }
    }

    public void shoot() {
        if (this.cooldown > this.gamePanel.FPS / this.weaponAttackSpeed) {
            
            if (this.owner == this.gamePanel.player) { // decrease mana for player
                this.gamePanel.player.mana--;
                if (this.gamePanel.player.mana <= 0) return;
            }

            projectiles.add(new Projectile(gamePanel, angle, this, this.owner));
            this.cooldown = 0;

            if (this.weaponClass == "shotgun") {
                for (int i = 0; i < 5; i++) {
                    projectiles.add(new Projectile(gamePanel, angle + this.randomNum(-15, 15), this, this.owner));   
                }
            }
        }
    }

    public void shoot(BufferedImage image) { 
        if (this.cooldown > this.gamePanel.FPS / this.weaponAttackSpeed) {

            if (this.owner == this.gamePanel.player) { // decrease mana for player
                this.gamePanel.player.mana--;
                if (this.gamePanel.player.mana <= 0) return;
            }

            projectiles.add(new Projectile(gamePanel, angle, this, this.owner, image));
            this.cooldown = 0;

            if (this.weaponClass == "shotgun") {
                for (int i = 0; i < 5; i++) {
                    projectiles.add(new Projectile(gamePanel, angle + this.randomNum(-15, 15), this, this.owner, image));   
                }
            }

            if (this.owner instanceof Enemy enemy) {
                if (enemy.type == 3 || enemy.type == 0) {
                    for (int i = 15; i < 360; i += 15) {
                        projectiles.add(new Projectile(gamePanel, angle + i, this, this.owner, image));
                    }
                }
            }

        }
    }

    public void initializeAsRandomWeapon() {
        this.weaponProjectileSpeed = this.randomNum(10, 20);

        // Determine Class based off of shooting speed
        this.weaponAttackSpeed = this.randomNum(1, 15);
        this.setWeaponClass();

        // Determine Rarity based off of Damage & Class
        int[] dmgRange = this.getClassDamageRange();
        this.weaponDamage = this.randomNum(dmgRange[0], dmgRange[1]);
        this.setWeaponRarity(dmgRange);

        this.setImage();
        this.setTextColor();
    }
    
    private void setWeaponClass() {
        if (this.weaponAttackSpeed <= 2) this.weaponClass = "shotgun";
        else if (this.weaponAttackSpeed <= 4) this.weaponClass = "pistol";
        else if (this.weaponAttackSpeed <= 8) this.weaponClass = "machine gun";
        else if (this.weaponAttackSpeed <= 10) this.weaponClass = "assault rifle";
        else if (this.weaponAttackSpeed <= 12) this.weaponClass = "sub-machine gun";
        else this.weaponClass = "minigun";
    }

    private void setWeaponRarity(int[] dmgRange) {
        if (this.weaponDamage <= getRarityThreshold(dmgRange, 1)) this.weaponRarity = "common";
        else if (this.weaponDamage <= getRarityThreshold(dmgRange, 2)) this.weaponRarity = "uncommon";
        else if (this.weaponDamage <= getRarityThreshold(dmgRange, 3)) this.weaponRarity = "rare";
        else if (this.weaponDamage <= getRarityThreshold(dmgRange, 4)) this.weaponRarity = "epic";
        else this.weaponRarity = "mythical";
    }

    private void setMetaData() {
        // Only to be called by setData()
        // Determine Class based off of shooting speed
        this.setWeaponClass();
        
        // Determine Rarity based off of Damage & Class
        int[] dmgRange = this.getClassDamageRange();
        this.setWeaponRarity(dmgRange);

        this.setTextColor();
    }

    private void setTextColor() {
        switch (this.weaponRarity) {
            case "common":
                this.textColor = Color.WHITE;
                break;
            case "uncommon":
                this.textColor = Color.GREEN;
                break;
            case "rare": 
                this.textColor = Color.BLUE;
                break;
            case "epic":
                this.textColor = Color.YELLOW;
                break;
            case "mythical":
                this.textColor = Color.MAGENTA;
                break;
            default:
                break;
        }
    }

    @Override
    public String toString() {
        return this.weaponRarity + " " + this.weaponClass;
    }

    private int getRarityThreshold(int[] dmgRange, int rarity) {
        return (dmgRange[0] + rarity * ((dmgRange[1] - dmgRange[0])/5));
    }

    private int[] getClassDamageRange() {
        int[] range = {0, 0};
        switch (this.weaponClass) {
            case "shotgun" -> range = new int[]{5, 10};
            case "pistol" -> range = new int[]{10, 20};
            case "machine gun" -> range = new int[]{15, 20};
            case "assault rifle" -> range = new int[]{12, 22};
            case "sub-machine gun" -> range = new int[]{8, 13};
            case "minigun" -> range = new int[]{3, 12};
        }
        return range;
    }

    private int randomNum(int min, int max) { // Inclusive
        return (int) (Math.random() * ((max - min) + 1)) + min;
    }

    public void clearProjectiles() {
        projectiles.clear();
    }
}
