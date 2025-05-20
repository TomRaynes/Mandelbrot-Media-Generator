import processing.core.PApplet;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

public class MediaGenerator {

    private Mandelbrot mandelbrot;
    private final int FRAME_WIDTH, FRAME_HEIGHT;
    private BufferedImage savedFrame;
    private String mediaPath;
    private int frameIndex = 0;
    private final String storageFolderPath;
    private final String frameFolderPath;
    private double MAX_ZOOM;
    private int numFrames;
    private int numSteps;
    private int completedSteps;
    private boolean generatingGif = false;
    private boolean gifThreadExecuting = false;



    public MediaGenerator(Mandelbrot mandelbrot) {
        this.mandelbrot = mandelbrot;
        FRAME_WIDTH = mandelbrot.getWidth();
        FRAME_HEIGHT = mandelbrot.getHeight();
        storageFolderPath = Paths.get("output").toAbsolutePath().toString();
        frameFolderPath = storageFolderPath + File.separator + "frames";
        mediaPath = storageFolderPath + File.separator + "media";

    }

    public void generateFrames() {
        if (frameIndex == 0 && !generatingGif) {
            MAX_ZOOM = mandelbrot.getZoom(); // 4 * Math.pow(10, 4); // 16
            mandelbrot.zoomOut();
            numSteps = calculateNumSteps();
            completedSteps = 0;
            deleteOldFrames();
        }

        if (mandelbrot.getZoom() > MAX_ZOOM && !generatingGif) {
            generatingGif = true;
        }
        if (generatingGif) {
            if (!gifThreadExecuting) {
                gifThreadExecuting = true;
                Thread gifGeneration = new Thread(this::generateGif);
                gifGeneration.start();
            }
            drawGifProgressBar();
        }
        else {
            generateFrame(false);
            frameIndex++;
            mandelbrot.incrementZoom();
            drawFramesProgressBar();
        }
    }

    private int calculateNumSteps() {
        double currentZoom = 93;
        numFrames = (int) (Math.log(MAX_ZOOM / currentZoom) / Math.log(1.1));
        return (int) (25*numFrames*Math.log10(currentZoom) +
                (25.0/2)*numFrames*(numFrames-1)*Math.log10(1.1) + 50*numFrames);
    }

    private void drawFramesProgressBar() {
        completedSteps += mandelbrot.calculateMaxIterations();
        float progress = (float) completedSteps / numSteps;
        drawProcessBar("Generating Frames", progress);
    }

    private void drawGifProgressBar() {
        float progress = (float) frameIndex / numFrames;
        drawProcessBar("Generating Gif", progress);
    }

    private void drawProcessBar(String text, float progress) {
        mandelbrot.push();

        // text
        mandelbrot.fill(255);
        mandelbrot.textAlign(PApplet.CENTER);
        mandelbrot.textSize(30);
        mandelbrot.text(text, (float) FRAME_WIDTH /2, 240);

        // progress bar
        mandelbrot.imageMode(PApplet.CORNER);
        mandelbrot.rect(100, 360, 600, 20);
        mandelbrot.fill(0);
        mandelbrot.rect(104, 364, Math.min(592*progress, 592), 12);

        mandelbrot.pop();
    }

    public void reset() {
        mandelbrot.setRecordMedia(false);
        frameIndex = 0;
        generatingGif = false;
        gifThreadExecuting = false;
    }

    public void generateFrame(boolean screenshot) {
        savedFrame = new BufferedImage(FRAME_WIDTH, FRAME_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        int maxIterations = mandelbrot.getMaxIterations();
        String outputPath;

        for (int row = 0; row < FRAME_HEIGHT; row++) {
            for (int col = 0; col < FRAME_WIDTH; col++) {
                double iterations = mandelbrot.getDivergingIteration(col, row);

                if (iterations > -1) {
                    float hue = PApplet.map((float) iterations, 0, maxIterations, 192, 0);
                    savedFrame.setRGB(col, row, mandelbrot.color(hue, 255, 255));
                }
                else savedFrame.setRGB(col, row, 0xFF000000);
            }
        }
        if (screenshot) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH-mm-ss");
            outputPath = storageFolderPath + File.separator +
                    "images" + File.separator + "image" + now.format(formatter) + ".png";
        }
        else outputPath = frameFolderPath + File.separator + "frame" + frameIndex + ".png";

        try {
            ImageIO.write(savedFrame, "png", new File(outputPath));
        } catch (IOException e) {
            e.printStackTrace();
            e.getMessage();
            throw new RuntimeException(e);
        }
    }

    private int getMediaNumber() {
        try {
            File logFile = Paths.get("output" + File.separator + ".log").toAbsolutePath().toFile();
            FileReader reader = new FileReader(logFile);
            BufferedReader buffReader = new BufferedReader(reader);
            String mediaNumberString = buffReader.readLine();
            int mediaNumber = Integer.parseInt(mediaNumberString);
            FileWriter writer = new FileWriter(logFile);
            BufferedWriter buffWriter = new BufferedWriter(writer);
            buffWriter.write(Integer.toString(mediaNumber + 1));
            buffWriter.newLine();
            buffWriter.close();
            return mediaNumber;
        }
        catch (IOException e) {
            e.printStackTrace();
            e.getMessage();
            throw new RuntimeException();
        }
    }

    private void deleteOldFrames() {
        Path framesFolderPath = Paths.get("output" + File.separator + "frames");

        try {
            for (Path frame : Files.newDirectoryStream(framesFolderPath, "*.png")) {
                Files.delete(frame);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            e.getMessage();
            throw new RuntimeException(e);
        }
    }

    public void generateGif() {
        File frames = new File(frameFolderPath);
        File[] frameFiles = frames.listFiles((_, name) -> name.matches("frame\\d+\\.png"));

        if (frameFiles == null || frameFiles.length == 0) {
            System.err.println("Frame files not found");
            reset();
            return;
        }

        // Sort frames
        Arrays.sort(frameFiles, Comparator.comparingInt(file -> {
            String name = file.getName().replaceAll("\\D", "");
            return Integer.parseInt(name);
        }));

        try {
            BufferedImage firstImage = ImageIO.read(frameFiles[0]);
            File outputFile = new File(mediaPath + File.separator + "media" + getMediaNumber() + ".gif");
            ImageOutputStream output = new FileImageOutputStream(outputFile);

            GifGenerator writer = new GifGenerator(output, firstImage.getType());
            frameIndex = 0;

            for (File frame : frameFiles) {
                frameIndex++;
                BufferedImage nextImage = ImageIO.read(frame);
                writer.writeToSequence(nextImage);
            }

            writer.close();
            output.close();
            reset();
        }
        catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
            throw new RuntimeException();
        }
    }
}
