import processing.core.*;

public class Mandelbrot extends PApplet {

    private final int FRAME_WIDTH = 500, FRAME_HEIGHT = 325;
    private final int MAX_ITERATIONS = 100;
    double zoom = 150;
    double xOffset = (double) -FRAME_WIDTH /1.5, yOffset = (double) -FRAME_HEIGHT /2;

    public static void main(String[] args) {
        PApplet.main("Mandelbrot");
    }

    private void reset() {
        zoom = 150;
        xOffset = (double) -FRAME_WIDTH /1.5;
        yOffset = (double) -FRAME_HEIGHT /2;
    }

    public void settings() {
        size(FRAME_WIDTH, FRAME_HEIGHT);
    }

    public void setup() {
        background(255);
    }

    public void draw() {
        background(0);

        for (int row = 0; row < FRAME_HEIGHT; row++) {
            for (int col = 0; col < FRAME_WIDTH; col++) {
                int iterations = getDivergingIteration(translateX(col), translateY(row));

                if (iterations > -1) {
                    set(col, row, getColour(iterations));
                }
            }
        }

        fill(255, 0, 0);
        textAlign(LEFT, TOP);
        textSize(30);
        text("Zoom = " + Math.round(zoom/2)/100, 20, 20);
    }

    public int getColour(int iterations) {
        iterations = Math.max(0, Math.min(MAX_ITERATIONS, iterations));

        RGB[] colorStops = new RGB[] {
                new RGB(75, 0, 255),     // Violet
                new RGB(255, 200, 0),   // Orange
                new RGB(0, 0, 150)     // Blue
                //new RGB(255, 255, 255)  // White

        };

        int segmentCount = colorStops.length - 1;
        double segmentLength = 300.0 / segmentCount;

        int segmentIndex = (int)(iterations / segmentLength);
        double t = (iterations % segmentLength) / segmentLength;

        if (segmentIndex >= segmentCount) {
            return color(148, 0, 211);
        }

        RGB c1 = colorStops[segmentIndex];
        RGB c2 = colorStops[segmentIndex + 1];

        int r = (int)(c1.getRed()   + (c2.getRed()   - c1.getRed())   * t);
        int g = (int)(c1.getGreen() + (c2.getGreen() - c1.getGreen()) * t);
        int b = (int)(c1.getBlue()  + (c2.getBlue()  - c1.getBlue())  * t);

        return color(r, g, b);
    }

    private int getDivergingIteration(double real, double imaginary) {
        double zReal = 0;
        double zImag = 0;
        int iterations = 0;

        for (; iterations< MAX_ITERATIONS; iterations++) {
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
            case 'r' -> reset();
        }
    }
}
