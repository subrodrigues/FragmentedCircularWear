package legend.cr.circularwearwidget;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by filiperodrigues on 10/11/17.
 */

public class MetricModel implements Parcelable{
    private float maxValue;
    private float currentValue;

    public MetricModel(int maxValue, int currentValue) {
        this.maxValue = maxValue;
        this.currentValue = currentValue;
    }

    private MetricModel(Parcel in) {
        maxValue = in.readFloat();
        currentValue = in.readFloat();
    }

    public static final Creator<MetricModel> CREATOR = new Creator<MetricModel>() {
        @Override
        public MetricModel createFromParcel(Parcel in) {
            return new MetricModel(in);
        }

        @Override
        public MetricModel[] newArray(int size) {
            return new MetricModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(maxValue);
        parcel.writeFloat(currentValue);
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public float getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }

    public float getCurrentPercentageValue(){
        return currentValue/maxValue;
    }

}
