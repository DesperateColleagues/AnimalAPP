package it.uniba.dib.sms22235.entities.operations;


public class InfoMessage {
    private int leftText;
    private int rightImage;

    public int getRightImage() {
        return rightImage;
    }

    public InfoMessage(int leftText, int rightImage) {
        this.leftText = leftText;
        this.rightImage = rightImage;
    }

    public int getLeftText() {
        return leftText;
    }

    public void setLeftText(int leftText) {
        this.leftText = leftText;
    }
}
