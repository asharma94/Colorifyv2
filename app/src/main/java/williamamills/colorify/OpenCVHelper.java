package williamamills.colorify;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Ankit on 4/18/2016.
 */
public class OpenCVHelper extends AsyncTask<String, Void, String[]> {

    Context ctx;

    public enum myColors {
        RED, YELLOW, GREEN, CYAN, BLUE, PURPLE
    }

    public OpenCVHelper(Context c) {
        ctx = c;
    }

    protected String[] doInBackground(String... url){
        Mat img = null;
        try {
            img = Utils.loadResource(ctx, R.drawable.lena, Imgcodecs.CV_LOAD_IMAGE_COLOR); // Load image
        } catch (IOException e) {
            e.printStackTrace();
        }
        int k = 2;
        Mat clusters = cluster(img, k).get(0); // Perform k-means algorithm

        Imgproc.cvtColor(clusters, clusters, Imgproc.COLOR_BGR2RGB); // Convert to RGB Color Space

        Bitmap b = Bitmap.createBitmap(clusters.width(), clusters.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(clusters,b); // Write to bitmap

//        File file = new File(ctx.getFilesDir(), "test2.png");
//        // Imgcodecs.imwrite(file.getPath(), clusters);
//        System.out.print("Writing\n");
//
//        try {
//            OutputStream outStream = new FileOutputStream(file);
//            b.compress(Bitmap.CompressFormat.PNG, 100, outStream);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

        String[] colors = getColors(b); // Place colors of image into buckets

        System.out.println(colors[0]);
        System.out.println(colors[1]);


        return colors;
    }
    public static List<Mat> cluster(Mat cutout, int k) {
        Mat samples = cutout.reshape(1, cutout.cols() * cutout.rows());
        Mat samples32f = new Mat();
        samples.convertTo(samples32f, CvType.CV_32F, 1.0 / 255.0);
        Mat labels = new Mat();
        TermCriteria criteria = new TermCriteria(TermCriteria.COUNT, 100, 1);
        Mat centers = new Mat();
        Core.kmeans(samples32f, k, labels, criteria, 1, Core.KMEANS_PP_CENTERS, centers);
        return showClusters(cutout, labels, centers);
    }
    private static List<Mat> showClusters (Mat cutout, Mat labels, Mat centers) {
        centers.convertTo(centers, CvType.CV_8UC1, 255.0);
        centers.reshape(3);
        List<Mat> clusters = new ArrayList<Mat>();
        for(int i = 0; i < centers.rows(); i++) {
            clusters.add(Mat.zeros(cutout.size(), cutout.type()));
        }
        Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
        for(int i = 0; i < centers.rows(); i++) counts.put(i, 0);
        int rows = 0;
        for(int y = 0; y < cutout.rows(); y++) {
            for(int x = 0; x < cutout.cols(); x++) {
                int label = (int)labels.get(rows, 0)[0];
                int r = (int)centers.get(label, 2)[0];
                int g = (int)centers.get(label, 1)[0];
                int b = (int)centers.get(label, 0)[0];
                clusters.get(label).put(y, x, b, g, r);
                rows++;
            }
        }
        return clusters;
    }

    private static String[] getColors(Bitmap b) {

        int c1 = b.getPixel(30,30);
        int c2 = 0;

        next:
        for (int y = 20; y < b.getHeight()-20; y++)
        {
            for (int x = 20; x < b.getWidth()-20; x++) {
                int tempColor = b.getPixel(x, y);
                if (tempColor != c1) {
                    c2 = tempColor;
                    break next;
                }
            }
        }

        String hexColor1 = String.format("#%06X", (0xFFFFFF & c1));
        String hexColor2 = String.format("#%06X", (0xFFFFFF & c2));

        System.out.println(c1);
        System.out.println(hexColor1);
        System.out.println(c2);
        System.out.println(hexColor2);

        float[] hsv1 = new float[3];
        float[] hsv2 = new float[3];

        Color.colorToHSV(c1,hsv1);
        Color.colorToHSV(c2,hsv2);

        hsv1[1] = (float) 0.5;
        hsv1[2] = (float) 0.5;
        hsv2[1] = (float) 0.5;
        hsv2[2] = (float) 0.5;

        String[] colors = new String[2];

        if(c1 <= -16777200){
            colors[0] = "BLACK";
            String s2 = getBucket(hsv2[0]);
            colors[1] = s2;
        } else if(c2 <= -16777200){
            colors[1] = "BLACK";
            String s1 = getBucket(hsv1[0]);
            colors[0] = s1;
        } else {
            String s1 = getBucket(hsv1[0]);
            String s2 = getBucket(hsv2[0]);
            colors[0] = s1;
            colors[1] = s2;
        }
        return colors;
    }

    private static String getBucket(float hue){
        if(hue > 320 || hue <= 25){
            return myColors.RED.name();
        } else if(hue > 25 && hue <= 70){
            return myColors.YELLOW.name();
        } else if(hue > 70 && hue <= 140){
            return myColors.GREEN.name();
        } else if(hue > 140 && hue <= 205){
            return myColors.CYAN.name();
        } else if(hue > 205 && hue <= 250){
            return myColors.BLUE.name();
        } else if(hue > 250 && hue <= 320){
            return myColors.PURPLE.name();
        }
        return myColors.YELLOW.name();
    }

}