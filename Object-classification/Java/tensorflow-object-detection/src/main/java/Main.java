import dk.hapshapshaps.classifier.objectdetection.models.Recognition;
import dk.hapshapshaps.classifier.objectdetection.models.RectFloats;
import dk.hapshapshaps.classifier.objectdetection.CustomObjectDetector;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        File modelFile = new File("/home/jacob/andet/training/docker-training-shared/subTraining/trainingResults/frozen_inference_graph.pb");
        File labelsFile = new File("/home/jacob/andet/training/docker-training-shared/subTraining/data/object-detection.pbtxt");

        File imageFile = new File("/home/jacob/andet/training/docker-training-shared/random-test-images/image-6.jpg");


        File resultImageFile = new File("/home/jacob/andet/training/docker-training-shared/random-test-images/result.jpg");


        newRun(imageFile, resultImageFile, modelFile, labelsFile);
    }

    private static void newRun(File imageFile, File resultImageFile, File modelFile, File labelFile) throws IOException {

        BufferedImage image = ImageIO.read(imageFile);

        CustomObjectDetector objectDetector = new CustomObjectDetector(modelFile, labelFile);

        ArrayList<Recognition> recognitions = objectDetector.classifyImage(image);

        List<Box> boxes = new ArrayList<>();
        for (Recognition recognition : recognitions) {
            if(recognition.getConfidence() > 0.05f) {
                RectFloats location = recognition.getLocation();
                int x = (int) location.getX();
                int y = (int) location.getY();
                int width = (int) location.getWidth() - x;
                int height = (int) location.getHeight() - y;

                boxes.add(new Box(x, y, width, height));
            }
        }

        BufferedImage boxedImage = drawBoxes(image, boxes);

        ImageIO.write(boxedImage, "jpg", resultImageFile);

        String s = "";
    }

    private static BufferedImage drawBoxes(BufferedImage image, List<Box> boxes) {
        Graphics2D graph = image.createGraphics();
        graph.setColor(Color.green);

        for (Box box : boxes) {
            graph.drawRect(box.x, box.y, box.width, box.height);
        }

        graph.dispose();
        return image;
    }

    private static BufferedImage drawBox(BufferedImage image, int x, int y, int width, int height) {
        Graphics2D graph = image.createGraphics();
        graph.setColor(Color.BLACK);
//        graph.fill(new Rectangle(x, y, width, height));
        graph.drawRect(x, y, width, height);
//        graph.setStroke();
        graph.dispose();
        return image;
    }


}

class Box {
    final int x;
    final int y;
    final int width;
    final int height;

    Box(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
