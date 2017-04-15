package Models;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arxa on 30/3/2017.
 */
public class Region
{
    private Mat region;
    private List<SubRegion> subregions; //cropped gray text regions

    public Region() {
        region = new Mat();
        subregions = new ArrayList<>();
    }

    public Region(Mat region) {
        this.region = new Mat();
        region.copyTo(this.region);
        subregions = new ArrayList<>();
    }

    public Mat getRegion() {
        return region;
    }

    public void setRegion(Mat region) {
        region = region;
    }

    public List<SubRegion> getSubregions() {
        return subregions;
    }

    public void setSubregions(List<SubRegion> subregions) {
        subregions = subregions;
    }
}
