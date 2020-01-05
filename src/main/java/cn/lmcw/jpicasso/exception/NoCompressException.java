package cn.lmcw.jpicasso.exception;

public class NoCompressException extends Exception {

    private String msg;

    public NoCompressException(String msg) {
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return msg;
    }
}
