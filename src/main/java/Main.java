

import processing.core.*;

public class Main extends PApplet {

    private final int FRAME_WIDTH = 800, FRAME_HEIGHT = 600;
    double zoom = 150;
    private int MAX_ITERATIONS = (int) (50 + Math.log10(zoom) * 25);;
    double xOffset = (double) -FRAME_WIDTH /1.5, yOffset = (double) -FRAME_HEIGHT /2;
    boolean showZoom = false;

    public static void main(String[] args) {
        PApplet.main("Main");
    }

    private void initialiseZoom() {
        zoom = 150;
        xOffset = (double) -FRAME_WIDTH /1.5;
        yOffset = (double) -FRAME_HEIGHT /2;
        System.out.println(map((float) 100, 0, MAX_ITERATIONS, 192, 0));
    }

    private void updateMaxIterations() {
        MAX_ITERATIONS = (int) (50 + Math.log10(zoom) * 25);
    }

    public void settings() {
        size(FRAME_WIDTH, FRAME_HEIGHT);
    }

    public void setup() {
        textAlign(LEFT, TOP);
        fill(255);
        textSize(25);
        //frameRate(2);
    }

    public void draw() {
        background(0);
        colorMode(HSB, 255);

        for (int row = 0; row < FRAME_HEIGHT; row++) {
            for (int col = 0; col < FRAME_WIDTH; col++) {
                double iterations = getDivergingIteration(translateX(col), translateY(row));

                if (iterations > -1) {
                    float hue = map((float) iterations, 0, MAX_ITERATIONS, 192, 0);
                    set(col, row, color(hue, 255, 255));
                }
            }
        }
        if (showZoom) text("Zoom = " + Math.round(zoom/2)/100, 20, 20);
    }

    private double getDivergingIteration(double real, double imaginary) {
        double zReal = 0;
        double zImag = 0;
        int iterations = 0;

        for (; iterations< MAX_ITERATIONS; iterations++) {
            double zRealNew = zReal*zReal - zImag*zImag + real;
            double zImagNew = 2*zReal*zImag + imaginary;
            zReal = zRealNew;
            zImag = zImagNew;

            if (zReal*zReal + zImag*zImag > 4) {
                break;
            }
        }
        if (iterations == MAX_ITERATIONS) {
            return -1;
        }

        double mag = Math.sqrt(zReal*zReal + zImag*zImag);
        double logZn = Math.log(mag) / Math.log(2);
        double nu = Math.log(logZn) / Math.log(2);
        return iterations + 1 - nu;
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
        xOffset = (cx * zoom - (double) width /2);
        yOffset = (cy * zoom - (double) height /2);

        switch (key) {
            case 'a' -> xOffset -= 10;
            case 'd' -> xOffset += 10;
            case 'w' -> yOffset -= 10;
            case 's' -> yOffset += 10;
            case 'r' -> initialiseZoom();
            case 'z' -> showZoom = !showZoom;
        }
        updateMaxIterations();
    }
}
