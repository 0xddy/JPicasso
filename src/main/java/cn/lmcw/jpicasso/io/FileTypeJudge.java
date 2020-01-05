package cn.lmcw.jpicasso.io;

import java.io.IOException;
import java.io.InputStream;

public class FileTypeJudge {
    /**
     * Constructor
     */
    private FileTypeJudge() {
    }

    /**
     * 将文件头转换成16进制字符串
     * @return 16进制字符串
     */
    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 得到文件头
     * @return 文件头
     * @throws IOException
     */
    private static String getFileContent(InputStream is) throws IOException {
        byte[] b = new byte[28];
        InputStream inputStream = null;
        try {
            is.read(b, 0, 28);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        }
        return bytesToHexString(b);
    }

    /**
     * 获取文件类型类
     * @return 文件类型
     */
    public static FileType getType(InputStream is) throws IOException {
        String fileHead = getFileContent(is);
        if (fileHead == null || fileHead.length() == 0) {
            return null;
        }
        fileHead = fileHead.toUpperCase();
        FileType[] fileTypes = FileType.values();
        for (FileType type : fileTypes) {
            if (fileHead.startsWith(type.getValue())) {
                return type;
            }
        }
        return null;
    }

    /**
     * 获取文件类型
     * @param is
     * @return
     * @throws Exception
     */
    public static String getFileType(InputStream is) throws Exception{
        FileType fileType = getType(is);
        if(fileType!=null){
            return fileType.getValue();
        }
        return null;
    }
}
