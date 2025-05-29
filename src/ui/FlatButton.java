package ui;

import javax.swing.*;
import java.awt.*;

/** Black rectangular button with white outline and sharp edges. */
public class FlatButton extends JButton {

    private static final Color BLACK   = new Color(0, 0, 0);
    private static final Color HOVER   = new Color(30, 30, 30);
    private static final Color PRESS   = new Color(60, 60, 60);
    private static final Color BORDER  = Color.WHITE;
    private static final Color TEXT    = Color.WHITE;

    public FlatButton(String text) {
        super(text);

        this.setFocusPainted(false);
        this.setBorderPainted(false);
        this.setContentAreaFilled(false);
        this.setForeground(TEXT);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.setFont(getFont().deriveFont(16f));
        this.setMargin(new Insets(10, 28, 10, 28));
    }

    public String getCaption() {
        return super.getText();
    }

    @Override protected void paintComponent(Graphics g) {
        Color c = getModel().isPressed()  ? PRESS
                 : getModel().isRollover()? HOVER
                 : BLACK;

        g.setColor(c);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(BORDER);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        super.paintComponent(g);
    }
}
