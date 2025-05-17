

import processing.core.*;

public class Mandelbrot extends PApplet {

    double zoom = 200;
    double xOffset = -400, yOffset = -300;

    public static void main(String[] args) {
        PApplet.main("Mandelbrot");
    }

    public void settings() {
        size(800, 600);
    }

    public void setup() {
        background(255);
    }

    public void draw() {
        background(0);

        for (int row = 0; row < 600; row++) {
            for (int col = 0; col < 800; col++) {
                int iterations = isMandelbrot(translateX(col), translateY(row));

                if (iterations > -1) {
                    set(col, row, color(255 - 2*iterations));
//                    if (iterations < 50) {
//                        set(col, row, color(255 - 5*iterations, 0, 0));
//                    }
//                    else set(col, row, color(0));
                }
            }
        }

        fill(255, 0, 0);
        textAlign(LEFT, TOP);
        textSize(30);
        text("Zoom = " + Math.round(zoom/2)/100, 20, 20);
    }

    private int isMandelbrot(double real, double imaginary) {
        double zReal = 0;
        double zImag = 0;
        int iterations = 0;

        for (; iterations<100; iterations++) {
            double zRealNew = zReal*zReal - zImag*zImag + real;
            double zImagNew = 2*zReal*zImag + imaginary;
            zReal = zRealNew;
            zImag = zImagNew;

            if (zReal*zReal + zImag*zImag > 4) {
                return iterations;
            }
        }
        return -1;
    }

    private double translateX(double coordX) {
        return (coordX + xOffset) / zoom;
    }

    private double translateY(double coordY) {
        return (coordY + yOffset) / zoom;
    }

    public void keyPressed() {
        double cx = translateX((double) width /2);
        double cy = translateY((double) height /2);

        if (keyCode == UP) {
            zoom *= 1.1;
        }
        else if (keyCode == DOWN) {
            zoom /= 1.1;
        }
        xOffset = (int)(cx * zoom - (double) width /2);
        yOffset = (int)(cy * zoom - (double) height /2);

        switch (key) {
            case 'a' -> xOffset -= 10;
            case 'd' -> xOffset += 10;
            case 'w' -> yOffset -= 10;
            case 's' -> yOffset += 10;
        }
    }
}
