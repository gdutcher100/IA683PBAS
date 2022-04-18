package com.IA683.pbas;

public class VibrationTracker {
    private boolean isLongPress;
    private int tapCount;
    private double longPressTime;

    public VibrationTracker(boolean isLongPress, int tapCount, double longPressTime) {
        this.isLongPress = isLongPress;
        this.tapCount = tapCount;
        this.longPressTime = longPressTime;
    }

    public boolean isLongPress() {
        return isLongPress;
    }

    public void setLongPress(boolean longPress) {
        isLongPress = longPress;
    }

    public int getTapCount() {
        return tapCount;
    }

    public void setTapCount(int tapCount) {
        this.tapCount = tapCount;
    }

    public double getLongPressTime() {
        return longPressTime;
    }

    public void setLongPressTime(double longPressTime) {
        this.longPressTime = longPressTime;
    }
}
