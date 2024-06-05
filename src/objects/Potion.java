package objects;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import main.GamePanel;

public class Potion extends GameObject {

    private String[] types = {"Empty", "Heal", "Increase Max Health", "Increase Max Shield", "Mana", "Increase Max Mana"};
    private int typeProbability = this.randomNum(1, 10);
    // 10% empty | 30% heal | 30% maxHealth increase | 30% maxShield increase
    private String type = (this.typeProbability == 1) ? this.types[0] : (this.typeProbability <= 4) ? this.types[1] : (this.typeProbability <= 7) ? this.types[2] : this.types[3];

    private int potionBenefit = this.randomNum(25, 125);
    private String level;

    public Potion(GamePanel gamePanel, int worldX, int worldY) {
        this.gamePanel = gamePanel;
        this.worldX = worldX;
        this.worldY = worldY;

        this.width = this.gamePanel.tileSize/4;
        this.height = this.gamePanel.tileSize/4;

        if (this.potionBenefit <= 50) this.level = "I";
        else if (this.potionBenefit <= 75) this.level = "II";
        else if (this.potionBenefit <= 100) this.level = "III";
        else if (this.potionBenefit <= 125) this.level = "IV";

        switch (this.type) {
            case "Empty" -> { this.image = this.gamePanel.assetManager.loadImage("../assets/potions/empty.png"); this.level = ""; }
            case "Heal" -> this.image = this.gamePanel.assetManager.loadImage("../assets/potions/heal.png");
            case "Increase Max Health" -> this.image = this.gamePanel.assetManager.loadImage("../assets/potions/healthIncrease.png");
            case "Increase Max Shield" -> this.image = this.gamePanel.assetManager.loadImage("../assets/potions/shieldIncrease.png");
            case "Increase Max Mana" -> this.image = this.gamePanel.assetManager.loadImage("../assets/potions/shieldIncrease.png");
            case "Mana" -> this.image = this.gamePanel.assetManager.loadImage("../assets/potions/shieldIncrease.png");
            default -> this.image = this.gamePanel.assetManager.loadImage("../assets/potions/empty.png");
        }

    }

    @Override
    public void interact() {
        switch (this.type) {
            case "Heal" -> this.gamePanel.player.regenerateHealth(this.potionBenefit);
            case "Mana" -> this.gamePanel.player.regenerateMana(this.potionBenefit);
            case "Increase Max Health" -> { this.gamePanel.player.increaseHealth(this.potionBenefit); this.gamePanel.player.regenerateHealth(this.potionBenefit); }
            case "Increase Max Shield" -> this.gamePanel.player.increaseShield(this.potionBenefit);
            case "Increase Max Mana" -> { this.gamePanel.player.increaseMana(this.potionBenefit); this.gamePanel.player.regenerateMana(this.potionBenefit); }
        }
        this.pickup();
    }

    @Override
    public void draw(Graphics2D g2) {
        super.draw(g2);
        super.drawName(g2);
    }

    @Override
    public String toString() {
        return this.type + " Potion Level " + this.level;
    }

    private int randomNum(int min, int max) { // Inclusive
        return (int) (Math.random() * ((max - min) + 1)) + min;
    }
}
