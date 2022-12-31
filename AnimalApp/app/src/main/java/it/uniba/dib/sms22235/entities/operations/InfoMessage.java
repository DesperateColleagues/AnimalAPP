package it.uniba.dib.sms22235.entities.operations;


public class InfoMessage {
    private String leftText;
    private int rightImage;
    private String type;

    public int getRightImage() {
        return rightImage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public InfoMessage(String leftText, int rightImage, String type) {
        this.leftText = leftText;
        this.rightImage = rightImage;
        this.type = type;
    }

    public String getLeftText() {
        return leftText;
    }

    public void setLeftText(String leftText) {
        this.leftText = leftText;
    }
}
