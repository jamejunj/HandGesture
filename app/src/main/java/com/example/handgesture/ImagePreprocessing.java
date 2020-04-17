package com.example.handgesture;


import ij.ImagePlus;
import ij.io.Opener;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.gui.ShapeRoi;

import imagingbook.lib.math.Complex;
import imagingbook.pub.fd.FourierDescriptor;
import imagingbook.pub.fd.FourierDescriptorUniform;
import imagingbook.pub.regions.Contour;
import imagingbook.pub.regions.RegionContourLabeling;
import imagingbook.pub.threshold.global.OtsuThresholder;
//import test_classes.myBinaryMorphologyFilter;

import java.awt.Color;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.List;



public class ImagePreprocessing {


    //Setup
    static int FourierDescriptorPairs = 15;
    static int ShapeReconstructionPoints = 100;

    static boolean DrawOriginalContour = true;
    static boolean DrawOriginalSamplePoints = true;

    static boolean DrawNormalizedShapes = true;

    static boolean showContours = true;
    static Color ContourColor = Color.blue;
    static double ContourStrokeWidth = 0.5;
    static Color ReconstructionColor = Color.green.darker();
    static double ReconstructionStrokeWidth = 0.5; //0.
    static Color CanonicalShapeAColor = Color.blue.brighter();
    static Color CanonicalShapeBColor = new Color(128, 66, 36); // brown75;

    public static void main(String[] args) {

        //---- INPUT IMAGE ----
        File file = new File("test_img_02.jpg");
        ImagePlus imp = new Opener().openImage(file.getAbsolutePath());
        ImageProcessor ip = imp.getProcessor();
        //Show image
        Show_Original_Image(ip);

        //Thresholding Image
        ByteProcessor bp = Image_Tresholding(ip);
        //Show image
        Show_Binary_Image(bp);

        //---- Binary Morphological Filtering (Noise Removal) ----
        ByteProcessor bp2 = Image_Morphological(bp);
        //Show image
        Show_Image_Noise_Removal(bp2);

        //----- Contour Extraction -----
        List<Contour> outerContours = Contour_Extraction(bp);
        FourierDescriptor fd = Get_FourierDescriptor(outerContours);
        FourierDescriptor[] fdAB = Get_Invariant_Descriptors(fd);
        Complex[] R = Get_Reconstruction(fd);
        //Show image
        Show_Image_Contour(fd, ip);
        Show_Image_Reconstruction(R, ip);
        Show_NormalizedShape(fdAB, ip);
    }

    static ByteProcessor Image_Tresholding(ImageProcessor ip){
        //----- Thresholding Global Otsu ------
        ByteProcessor bp = ip.convertToByteProcessor();
        OtsuThresholder thr = new OtsuThresholder();
        int q = thr.getThreshold(bp);
        if (q >= 0) {
            bp.threshold(q);
        }
        return bp;
    }

    static ByteProcessor Image_Morphological(ByteProcessor bp){
        myBinaryMorphologyFilter m = new myBinaryMorphologyFilter();
        m.dilate(bp);
        bp = m.ip_out;
        return bp;
    }

    static List<Contour> Contour_Extraction(ByteProcessor bp){
        // segment the image and select the longest outer region contour:
        RegionContourLabeling labeling = new RegionContourLabeling(bp);
        List<Contour> outerContours = labeling.getOuterContours(true);
        return outerContours;
    }

    static FourierDescriptor Get_FourierDescriptor(List<Contour> outerContours){
        FourierDescriptor fd = null;
        if (outerContours.isEmpty()){
            return fd;
        }
        Contour contr = outerContours.get(0);	// select the longest contour
        Point2D[] V = contr.getPointArray();

        // create the Fourier descriptor for 'V' with Mp coefficient pairs:
        int Mp = FourierDescriptorPairs;
        fd = new FourierDescriptorUniform(V, Mp);

        return fd;
    }

    static Complex[] Get_Reconstruction(FourierDescriptor fd){
        // reconstruct the corresponding shape with 100 contour points:
        Complex[] R = fd.getReconstruction(100);
        return R;
    }

    static FourierDescriptor[] Get_Invariant_Descriptors(FourierDescriptor fd){
        // create a pair of invariant descriptors (G^A, G^B):
        FourierDescriptor[] fdAB = fd.makeInvariant();
        FourierDescriptor fdA = fdAB[0];	// = G^A
        FourierDescriptor fdB = fdAB[1];	// = G^B
        return fdAB;
    }

    static void Show_Original_Image(ImageProcessor ip){
        (new ImagePlus("Original Image", ip)).show();
    }

    static void Show_Binary_Image(ByteProcessor bp){
        (new ImagePlus("Binary Image", bp)).show();
    }

    static void Show_Image_Noise_Removal(ByteProcessor bp){
        (new ImagePlus("Noise Removal", bp)).show();
    }

    static void Show_Image_Contour(FourierDescriptor fd, ImageProcessor ip){
        Overlay oly = new Overlay();
        Roi roi = makeClosedPathShape(fd.getSamples(), 0.5, 0.5);
        roi.setStrokeColor(ContourColor);
        roi.setStrokeWidth(ContourStrokeWidth);
        oly.add(roi);

        ByteProcessor bp = ip.convertToByteProcessor();
        ImagePlus im = new ImagePlus("Contour Extraction", bp);
        if (bp.isInvertedLut()) {
            bp.invert();
            bp.invertLut();
        }
        brighten(bp, 220);
        im.setOverlay(oly);
        im.show();
    }

    static void Show_Image_Reconstruction(Complex[] R, ImageProcessor ip){
        Overlay oly = new Overlay();
        ShapeRoi roi = makeClosedPathShape(R, 0.5, 0.5);
        roi.setStrokeColor(ReconstructionColor);
        roi.setStrokeWidth(ReconstructionStrokeWidth);
        oly.add(roi);

        ByteProcessor bp = ip.convertToByteProcessor();
        ImagePlus im = new ImagePlus("Image Reconstruction by FD", bp);
        if (bp.isInvertedLut()) {
            bp.invert();
            bp.invertLut();
        }
        brighten(bp, 220);
        im.setOverlay(oly);
        im.show();
    }

    static void Show_NormalizedShape(FourierDescriptor[] fdAB, ImageProcessor ip){
        FourierDescriptor fdA = fdAB[0];	// = G^A
        FourierDescriptor fdB = fdAB[1];	// = G^B

        Overlay oly = new Overlay();
        ShapeRoi roiA = makeClosedPathShape(fdA.getReconstruction(ShapeReconstructionPoints), 0.5, 0.5);
        roiA.setStrokeColor(CanonicalShapeAColor);
        roiA.setStrokeWidth(ReconstructionStrokeWidth);
        oly.add(roiA);
        ShapeRoi roiB = makeClosedPathShape(fdB.getReconstruction(ShapeReconstructionPoints), 0.5, 0.5);
        roiB.setStrokeColor(CanonicalShapeBColor);
        roiB.setStrokeWidth(ReconstructionStrokeWidth);
        oly.add(roiB);

        ByteProcessor bp = ip.convertToByteProcessor();
        ImagePlus im = new ImagePlus("Image Reconstruction by Normalized Shape", bp);
        if (bp.isInvertedLut()) {
            bp.invert();
            bp.invertLut();
        }
        brighten(bp, 220);
        im.setOverlay(oly);
        im.show();
    }

    static ShapeRoi makeClosedPathShape(Complex[] points, double dx, double dy) {
        Path2D path = new Path2D.Float();
        for (int i = 0; i < points.length; i++) {
            Complex pt = points[i];
            double xt = pt.re + dx;
            double yt = pt.im + dy;
            if (i == 0) {
                path.moveTo(xt, yt);
            } else {
                path.lineTo(xt, yt);
            }
        }
        path.closePath();
        return new ShapeRoi(path);
    }

    static void brighten(ByteProcessor ip, int minGray) {
        if (minGray > 254) {
            minGray = 254;
        }
        float scale = (255 - minGray) / 255f;
        int[] table = new int[256];
        for (int i = 0; i < 256; i++) {
            table[i] = (int) Math.round(minGray + scale * i);
        }
        ip.applyTable(table);
    }


}
