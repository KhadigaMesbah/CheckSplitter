package com.tesseractdemo.tesseractdemo;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static org.opencv.android.NativeCameraView.TAG;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.MORPH_ELLIPSE;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.dilate;
import static org.opencv.imgproc.Imgproc.erode;
import static org.opencv.imgproc.Imgproc.getStructuringElement;

public class ProcessImage {

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.w(TAG, "Unable to load OpenCV");
        } else {
            Log.d(TAG,"OpenCV loaded");
        }
    }

    public Bitmap process(Bitmap bitOrigin) {

        Mat origin = new Mat();
        Mat origin2 = new Mat();
        Utils.bitmapToMat(bitOrigin, origin);
        Utils.bitmapToMat(bitOrigin, origin2);

        //Convert the image to GRAY
        //Mat originGray = new Mat();

        cvtColor(origin, origin, COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(origin, origin, new Size(5, 5), 5);
        Imgproc.adaptiveThreshold(origin, origin, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, 15);

        //To Calculate Deskew angle!!!
        //BGR2GRAY, GaussianBlur, AdaptiveThreshold
        //Image here must be out of adaptive threshold as black text on white background, and the inverse is taken care of inside deskew fn.
        Mat element1 = getStructuringElement(MORPH_ELLIPSE, new Size(21, 21), new Point(1, 1));
        erode(origin, origin, element1);
        dilate(origin, origin, element1);

        double angle = deskew_angle(origin);
        Log.d ("ANGLE", Double.toString(angle));

        origin2 = deskew(origin2, angle);
        cvtColor(origin2, origin2, COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(origin2, origin2, new Size(3, 3), 5);
        Imgproc.adaptiveThreshold(origin2, origin2, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, 15);

//        Size s = origin2.size();
//        double new_height = 2 * s.height;
//        double new_width = 2 * s.width;
//        int interpolation = Imgproc.INTER_CUBIC;
//        Imgproc.resize(origin2, origin2, new Size(new_height,new_width), 0, 0, interpolation );

        Mat element2 = getStructuringElement(MORPH_ELLIPSE, new Size(5, 5), new Point(1, 1));
        erode(origin, origin, element2);
        dilate(origin, origin, element2);


        return toBitmap(origin2);
    }

    private double deskew_angle( Mat source){
        //Mat source = Imgcodecs.imread(input.getName(),0);
        Size size = source.size();
        Core.bitwise_not(source, source);
        Mat lines = new Mat();
        Imgproc.HoughLinesP(source, lines, 1, Math.PI / 180, 100, size.width / 2.f, 20);
        double angle = 0.;
        for(int i = 0; i < lines.height(); i++){
            for(int j = 0; j<lines.width();j++){
                angle += Math.atan2(lines.get(i, j)[3] - lines.get(i, j)[1], lines.get(i, j)[2] - lines.get(i, j)[0]);
            }
        }
        angle /= lines.size().area();
        angle = angle * 180 / Math.PI;

        return angle;
    }

    private Mat deskew(Mat src, double angle) {
        Point center = new Point(src.width()/2, src.height()/2);
        Mat rotImage = Imgproc.getRotationMatrix2D(center, angle, 1.0);
        //1.0 means 100 % scale
        Size size = new Size(src.width(), src.height());
        Imgproc.warpAffine(src, src, rotImage, size, Imgproc.INTER_LINEAR + Imgproc.CV_WARP_FILL_OUTLIERS);
        return src;
    }

    public static Bitmap toBitmap(Mat mat) {
        Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }
}

