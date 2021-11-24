import javax.swing.*;
import java.awt.*;

public class VisualAudio extends JComponent {
    int x1_4 = 50;
    int x1_5 = 60;
    int x1_6 = 70;
    int x1_7 = 80;
    int x1_8 = 90;
    int x1_9 = 100;
    int x1_10 = 110;
    int x1_1 = 120;
    int x1_2 = 130;
    int x1_3 = 140;
    int x1_11 = 150;
    int x1_12 = 160;
    int x1_13 = 170;
    int x1_14 = 180;
    int x1_15 = 190;
    int x1_16 = 200;


    int y1 = 100;
    int y2;
    int y3;
    int y4;
    int y5;
    int y6;
    int y7;
    int y8;
    int y9;
    int y10;
    int y11;
    int y12;
    int y13;
    int y14;
    int y15;
    int y16;
    int y17;

    public void setLine(int y2, int y3, int y4, int y5, int y6, int y7, int y8, int y9, int y10, int y11, int y12, int y13, int y14, int y15, int y16, int y17) {
        this.y2 = y2;
        this.y3 = y3;
        this.y4 = y4;
        this.y5 = y5;
        this.y6 = y6;
        this.y7 = y7;
        this.y8 = y8;
        this.y9 = y9;
        this.y10 = y10;
        this.y11 = y11;
        this.y12 = y12;
        this.y13 = y13;
        this.y14 = y14;
        this.y15 = y15;
        this.y16 = y16;
        this.y17 = y17;
        repaint();
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        // draw and display the line
        g.setColor(Color.blue);
        g.drawLine(x1_1, y1, x1_1, y2);
        g.drawLine(x1_2, y1, x1_2, y3);
        g.drawLine(x1_3, y1, x1_3, y4);
        g.drawLine(x1_4, y1, x1_4, y5);
        g.drawLine(x1_5, y1, x1_5, y6);
        g.drawLine(x1_6, y1, x1_6, y7);
        g.drawLine(x1_7, y1, x1_7, y8);
        g.drawLine(x1_8, y1, x1_8, y9);
        g.drawLine(x1_9, y1, x1_9, y10);
        g.drawLine(x1_10, y1, x1_10, y11);
        g.drawLine(x1_11, y1, x1_11, y12);
        g.drawLine(x1_12, y1, x1_12, y13);
        g.drawLine(x1_13, y1, x1_13, y14);
        g.drawLine(x1_14, y1, x1_14, y15);
        g.drawLine(x1_15, y1, x1_15, y16);
        g.drawLine(x1_16, y1, x1_16, y17);
    }
}
