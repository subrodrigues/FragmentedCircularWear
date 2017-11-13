package legend.cr.circularwearwidget;

/**
 * Created by filiperodrigues on 10/11/17.
 */

public class MetricModel {
    private float maxValue;
    private float currentValue;

    public MetricModel(int maxValue, int currentValue) {
        this.maxValue = maxValue;
        this.currentValue = currentValue;
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
