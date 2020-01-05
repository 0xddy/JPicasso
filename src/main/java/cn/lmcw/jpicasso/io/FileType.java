package cn.lmcw.jpicasso.io;

public enum FileType {

    JPEG("FFD8FF"),
    PNG("89504E47"),
    GIF("47494638"),
    WEBP("52494646"),
    BMP("424D");

    private String value = "";

    FileType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
