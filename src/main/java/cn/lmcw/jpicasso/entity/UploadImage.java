package cn.lmcw.jpicasso.entity;

import cn.lmcw.jpicasso.utils.FileUtils;
import com.qcloud.cos.utils.Md5Utils;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class UploadImage {
    @Id
    public long id;

    private String path;

    private long fileSize;

    private boolean isCompress;

    private long creatTime = System.currentTimeMillis();

    private String uploaderIp;

    private String hash;

    private String status = "1";

    private String attribute;

    public UploadImage() {
    }

    public UploadImage(String path, long fileSize, boolean isCompress) {
        this(path, fileSize, isCompress, null);
    }

    public UploadImage(String path, long fileSize, boolean isCompress, String uploaderIp) {
        this.path = path;
        this.fileSize = fileSize;
        this.isCompress = isCompress;
        this.uploaderIp = uploaderIp;
        hash = FileUtils.getMd5(path);
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isCompress() {
        return isCompress;
    }

    public void setCompress(boolean compress) {
        isCompress = compress;
    }

    public long getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(long creatTime) {
        this.creatTime = creatTime;
    }

    public String getUploaderIp() {
        return uploaderIp;
    }

    public void setUploaderIp(String uploaderIp) {
        this.uploaderIp = uploaderIp;
    }

    @Override
    public String toString() {
        return "UploadImage{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", fileSize=" + fileSize +
                ", isCompress=" + isCompress +
                ", creatTime=" + creatTime +
                ", uploaderIp='" + uploaderIp + '\'' +
                '}';
    }
}
