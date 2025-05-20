import processing.core.PApplet;

public class Mandelbrot extends PApplet {

    private final int FRAME_WIDTH = 800, FRAME_HEIGHT = 600;
    private double zoom = 150;
    private int MAX_ITERATIONS = (int) (50 + Math.log10(zoom) * 25);
    private double xOffset = (double) -FRAME_WIDTH /1.5, yOffset = (double) -FRAME_HEIGHT /2;
    private boolean showZoom = false;
    private boolean centreOnCursor = false;
    private MediaGenerator generator;
    private boolean recordMedia = false;

    public static void main(String[] args) {
        PApplet.main("Mandelbrot");
    }

    private void initialiseZoom() {
        zoom = 150;
        xOffset = (double) -FRAME_WIDTH /1.5;
        yOffset = (double) -FRAME_HEIGHT /2;
    }

    private void updateMaxIterations() {
        MAX_ITERATIONS = calculateMaxIterations();
    }

    public int calculateMaxIterations() {
        return (int) (50 + Math.log10(zoom) * 25);
    }

    public int getMaxIterations() {
        return MAX_ITERATIONS;
    }

    public void settings() {
        size(FRAME_WIDTH, FRAME_HEIGHT);
    }

    public void setup() {
        textAlign(LEFT, TOP);
        textSize(25);
        generator = new MediaGenerator(this);
    }

    public void draw() {
        background(0);
        colorMode(HSB, 255);

        if (centreOnCursor) {
            snapToCursor();
        }

        if (recordMedia) {
            generator.generateFrames();
        }
        else for (int row = 0; row < FRAME_HEIGHT; row++) {
            for (int col = 0; col < FRAME_WIDTH; col++) {
                double iterations = getDivergingIteration(col, row);

                if (iterations > -1) {
                    float hue = map((float) iterations, 0, MAX_ITERATIONS, 192, 0);
                    set(col, row, color(hue, 255, 255));
                }
            }
        }
        if (showZoom) {
            fill(0);
            rect(20, 20, 400, 19);
            fill(255);
            text("Zoom = " + Math.round(zoom/2)/100, 20, 20);
        }
    }

    public int getWidth() {
        return FRAME_WIDTH;
    }

    public int getHeight() {
        return FRAME_HEIGHT;
    }

    public double getDivergingIteration(double col, double row) {
        double real = translateX(col);
        double imaginary = translateY(row);
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

    public void incrementZoom() {
        adjustZoom(() -> zoom *= 1.1);
    }

    public void decrementZoom() {
        adjustZoom(() -> zoom /= 1.1);
    }

    public void zoomOut() {
        adjustZoom(() -> zoom = 93);
    }

    private void adjustZoom(Runnable zoomOperation) {
        double cx = translateX((double) width /2);
        double cy = translateY((double) height /2);

        zoomOperation.run();

        xOffset = (cx * zoom - (double) width /2);
        yOffset = (cy * zoom - (double) height /2);

        updateMaxIterations();
    }

    private void snapToCursor() {
        xOffset -= ((double) FRAME_WIDTH/2 - mouseX) / 10;
        yOffset -= ((double) FRAME_HEIGHT/2 - mouseY) / 10;
    }

    public void keyPressed() {
        if (recordMedia) {
            if (keyCode == ENTER) {
                generator.reset();
            }
            return;
        }

        if (keyCode == UP) {
            incrementZoom();
        }
        else if (keyCode == DOWN) {
            decrementZoom();
        }
        else if (keyCode == ENTER) {
            recordMedia = true;
        }
        else switch (key) {
            case 'a' -> xOffset -= 10;
            case 'd' -> xOffset += 10;
            case 'w' -> yOffset -= 10;
            case 's' -> yOffset += 10;
            case 'r' -> initialiseZoom();
            case 'z' -> showZoom = !showZoom;
            case 'c' -> centreOnCursor = !centreOnCursor;
            case 'i' -> generator.generateFrame(true);
        }
    }

    public double getZoom() {
        return zoom;
    }

    public void setRecordMedia(boolean value) {
        recordMedia = value;
    }
}
