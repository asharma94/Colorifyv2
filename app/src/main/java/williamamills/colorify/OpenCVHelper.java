package williamamills.colorify;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
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


public class OpenCVHelper extends AsyncTask<String, Void, ArrayList<String[]>> {

    Context ctx;
    ArrayList<Photo> photoList;
    String colorToSearch;

    public enum myColors {
        RED, YELLOW, GREEN, CYAN, BLUE, PURPLE
    }

    public OpenCVHelper(Context c, ArrayList<Photo> _photoList, String _colorToSearch) {
        ctx = c;
        photoList = _photoList;
        colorToSearch  = _colorToSearch;
    }

    protected ArrayList<String[]> doInBackground(String... params){

        ArrayList<String[]> allColors = new ArrayList<>();

        for(int j = 0; j < photoList.size(); j += 1) {
            Mat img = new Mat();
            try {
                //Bitmap original = BitmapFactory.decodeStream(ctx.openFileInput(ctx.getResources().getString(R.string.image_path) + j));
                img = Imgcodecs.imread(ctx.getFilesDir().getPath() + "/thumbImage" + j);
                                //img = new Mat(original.getWidth(), original.getHeight(), CvType.CV_8U);
                /*int vvvv = original.getWidth();
                int vv = original.getHeight();
                Utils.bitmapToMat(original, img); // Load image*/
               // original.recycle();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            int k = 2; // number of centroids
            Mat clusters = cluster(img, k).get(0); // Perform k-means algorithm
            Imgproc.cvtColor(clusters, clusters, Imgproc.COLOR_BGR2RGB); // Convert to Mat to RGB Color Space
            Bitmap b = Bitmap.createBitmap(clusters.width(), clusters.height(), Bitmap.Config.ARGB_8888); // Create bitmap
            Utils.matToBitmap(clusters, b); // Write to bitmap
            String[] colors = getColorBuckets(b); // Place colors of image into buckets
            allColors.add(colors);
            /*File file = new File(android.os.Environment.DIRECTORY_DCIM, "test"+j+".png");
            Imgcodecs.imwrite(file.getPath(),clusters);
            try{
                MediaStore.Images.Media.insertImage(ctx.getContentResolver(), b, "Photo" , "What a great photo");
            }catch(Exception e){
                Toast.makeText(ctx.getApplicationContext(), ctx.getResources().getString(R.string.image_not_saved), Toast.LENGTH_SHORT).show();
            }*/
            b.recycle();
            clusters.release();
        }
        return allColors;
    }
    protected void onPostExecute(ArrayList<String[]> allColors){
        ListIterator<Photo> it = photoList.listIterator();
        int count = 0;
        while(it.hasNext()){
            Photo p = it.next();
            String c = allColors.get(count)[0];
            if(c.equals("BLACK")){
                c = allColors.get(count)[1];
            }
            if(c.equals(colorToSearch)){
                p.setColor(c);
            }else{
                it.remove();
            }
            count++;
        }
        Intent i = new Intent(ctx, ItemsList.class);
        Bundle extras = new Bundle();
        extras.putParcelableArrayList("photos", photoList);
        i.putExtras(extras);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(i);
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

    private static String[] getColorBuckets(Bitmap b) {

        int c1 = b.getPixel(30, 30);
        int c2 = extractColors(b, c1);

        float[] hsv1 = new float[3];
        float[] hsv2 = new float[3];

        Color.colorToHSV(c1,hsv1);
        Color.colorToHSV(c2, hsv2);

        boolean c1_white = false;
        boolean c2_white = false;

        if(hsv1[1] < 0.1) {
            c1_white = true;
        }else if(hsv2[1] < 0.1) {
            c2_white = true;
        }

        hsv1[1] = (float) 0.5;
        hsv1[2] = (float) 0.5;
        hsv2[1] = (float) 0.5;
        hsv2[2] = (float) 0.5;

        String[] colors = null;
        int black = -16777200;

        if(c1 <= black || c1_white ){
            colors[0] = "BLACK";
            String s2 = getBucket(hsv2[0]);
            colors[1] = s2;
        } else if(c2 <= -black || c2_white){
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

    private static int extractColors (Bitmap b, int colorOne) {
        for (int y = 20; y < b.getHeight() - 20; y++) {
            for (int x = 20; x < b.getWidth() - 20; x++) {
                int tempColor = b.getPixel(x, y);
                if (tempColor != colorOne) {
                    int colorTwo = tempColor;
                    return colorTwo;
                }
            }
        }
        return 0;
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