package Entities;

import org.opencv.core.Size;

public class StructuringElement {

    public static Size getStructuringElement(int imageResolution){
        if (imageResolution < 500000){
            return new Size(5.0,5.0);
        }
        else if (imageResolution < 1000000){
            return new Size(7.0,7.0);
        }
        else if (imageResolution < 1500000){
            return new Size(11.0,11.0);
        }
        else if (imageResolution < 2000000){
            return new Size(13.0,13.0);
        }
        else {
            return new Size(15.0,15.0);
        }
    }
}
