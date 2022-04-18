package com.IA683.pbas.Helpers;

public class VibrationTracker {
    private boolean isLongPress;
    private int tapCount;
    private long longPressTime;

    public VibrationTracker(boolean isLongPress, int tapCount, long longPressTime) {
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

    public long getLongPressTime() {
        return longPressTime;
    }

    public void setLongPressTime(long longPressTime) {
        this.longPressTime = longPressTime;
    }

    @Override
    public String toString() {
        return "VibrationTracker{" +
                "isLongPress=" + isLongPress +
                ", tapCount=" + tapCount +
                ", longPressTime=" + longPressTime +
                '}';
    }
}
