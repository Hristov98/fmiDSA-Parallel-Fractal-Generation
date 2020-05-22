package task18;

import org.apache.commons.math3.complex.Complex;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MandelbrotSet {
    public static int width;
    public static int height;
    public static float realUpperLimit;
    public static float realLowerLimit;
    public static float imaginaryUpperLimit;
    public static float imaginaryLowerLimit;
    public static int numberOfThreads;
    public static String outputName;
    public static boolean isQuiet;
    public static final int ITERATIONS = 500;
    public static BufferedImage buffer;

    MandelbrotSet() {
        width = 640;
        height = 480;
        realUpperLimit = 2.0f;
        realLowerLimit = -2.0f;
        imaginaryUpperLimit = 2.0f;
        imaginaryLowerLimit = -2.0f;
        numberOfThreads = 4;
        outputName = "zad18.png";
        isQuiet = false;

        buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public void renderImageWithCoarseGranularity() {
        int rowsPerThread = MandelbrotSet.height / numberOfThreads;
        int rowsLeftUnassigned = height % rowsPerThread;

        Thread[] threads = new Thread[numberOfThreads];
        int threadNumber = 0;
        for (; threadNumber < numberOfThreads; threadNumber++) {
            threads[threadNumber] = new Thread(new MandelbrotRunnable(threadNumber, rowsPerThread));
            threads[threadNumber].setName("Thread " + threadNumber);

            threads[threadNumber].start();
        }

        for (int i = 0; i < numberOfThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                System.out.println(threads[i].getName() + " has thrown InterruptedException.");
            }
        }

        if (rowsLeftUnassigned != 0) {
            System.out.printf("Filling in the last %d rows...\n", rowsLeftUnassigned);
            for (int imaginaryCoordinate = height - rowsLeftUnassigned; imaginaryCoordinate < height; imaginaryCoordinate++) {
                for (int realCoordinate = 0; realCoordinate < width; realCoordinate++) {
                    float constantReal = getConstantReal(realCoordinate);
                    float constantImaginary = getConstantImaginary(imaginaryCoordinate);
                    int color = calculateColor(constantReal, constantImaginary);

                    buffer.setRGB(realCoordinate, imaginaryCoordinate, color);
                }
            }
        }
    }

    public void  renderRowsWithMediumGranularity(){

    }

    public void  renderRowsWithFineGranularity(){

    }

    public static float getConstantReal(int realCoordinate) {
        return realCoordinate * (MandelbrotSet.realUpperLimit - MandelbrotSet.realLowerLimit) / MandelbrotSet.width
                + MandelbrotSet.realLowerLimit;
    }

    public static float getConstantImaginary(int imaginaryCoordinate) {
        return imaginaryCoordinate * (MandelbrotSet.imaginaryUpperLimit - MandelbrotSet.imaginaryLowerLimit) / MandelbrotSet.height
                + MandelbrotSet.imaginaryLowerLimit;
    }

    public static int calculateColor(float real, float imaginary) {
        Complex constant = new Complex(real, imaginary);
        Complex zFunction = Complex.ZERO;

        for (int currentIterations = 0; currentIterations < MandelbrotSet.ITERATIONS; currentIterations++) {
            zFunction = constant.multiply(zFunction.cos());

            if (isOutOfBounds(zFunction)) {
                return Color.HSBtoRGB((float) currentIterations / MandelbrotSet.ITERATIONS, 0.67f, 1);
            }
        }

        return 0x00000000;
    }

    public static boolean isOutOfBounds(Complex z) {
        return z.getReal() * z.getReal() + z.getImaginary() * z.getImaginary() > 4;
    }

    public void saveImage() {
        try {
            ImageIO.write(buffer, "png", new File(outputName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setImageSize(int height, int width) {
        MandelbrotSet.height = height;
        MandelbrotSet.width = width;
    }

    public void setRectangleBoundaries(float realLowerLimit, float realUpperLimit,
                                       float imaginaryLowerLimit, float imaginaryUpperLimit) {
        MandelbrotSet.realLowerLimit = realLowerLimit;
        MandelbrotSet.realUpperLimit = realUpperLimit;
        MandelbrotSet.imaginaryLowerLimit = imaginaryLowerLimit;
        MandelbrotSet.imaginaryUpperLimit = imaginaryUpperLimit;
    }

    public void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public void setOutputName(String outputName) {
        MandelbrotSet.outputName = outputName;
    }

    public void setQuiet() {
        isQuiet = true;
    }
}