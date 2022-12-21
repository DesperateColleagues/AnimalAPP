package it.uniba.dib.sms22235.entities.operations;

import android.graphics.drawable.Drawable;

public class InfoMessage {
    private int leftText;
    private int bottomText;
    private int rightImage;

    public int getRightImage() {
        return rightImage;
    }

    public void setRightImage(int rightImage) {
        this.rightImage = rightImage;
    }

    public InfoMessage(int leftText, int bottomText, int rightImage) {
        this.bottomText = bottomText;
        this.leftText = leftText;
        this.rightImage = rightImage;
    }

    public int getLeftText() {
        return leftText;
    }

    public void setLeftText(int leftText) {
        this.leftText = leftText;
    }

    public int getBottomText() {
        return bottomText;
    }

    public void setBottomText(int bottomText) {
        this.bottomText = bottomText;
    }
}
