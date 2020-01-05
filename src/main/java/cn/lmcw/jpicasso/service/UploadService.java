package cn.lmcw.jpicasso.service;

import cn.lmcw.jpicasso.JpicassoApplication;
import cn.lmcw.jpicasso.conf.WebConf;
import cn.lmcw.jpicasso.entity.UploadImage;
import cn.lmcw.jpicasso.io.FileTypeJudge;
import io.objectbox.Box;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.InputStream;

@Service
public class UploadService {

    @Autowired
    WebConf webConf;
    @Autowired
    PngquantatorService pngquantatorService;

    public void writeUploadInfo(HttpServletRequest httpServletRequest, boolean isCompress, File newFile, String yunKey) {
        // 记录上传文件至数据库
        // 获取用户ip
        String userIp = null;
        if (webConf.safeProxyIp.equals("")) {
            // 任意反代ip均获取来源ip
            String xRealIp = httpServletRequest.getHeader("X-Real-IP");
            if (xRealIp == null || xRealIp.equals("")) {
                // 一般这种情况是没有反代，只考虑nginx反代
                userIp = httpServletRequest.getRemoteAddr();
            } else {
                userIp = xRealIp;
            }
        } else {
            String[] ips = webConf.safeProxyIp.split(",");
            String xRealIp = httpServletRequest.getHeader("X-Real-IP");
            String remoteAddr = httpServletRequest.getRemoteAddr();
            boolean isSaleServer = false;
            for (String ip : ips) {
                if (remoteAddr.equals(ip)) {
                    isSaleServer = true;
                    break;
                }
            }
            if (isSaleServer) {
                userIp = xRealIp;
            } else {
                // 如果不是白名单的反代服务器 那么记录该服务器的ip，方便后期检测
                userIp = remoteAddr;
            }
        }

        if (webConf.uploadInfoWrite == 1) {
            Box<UploadImage> box = JpicassoApplication.boxFor(UploadImage.class);
            box.put(new UploadImage(yunKey, newFile.length(), isCompress, userIp));
            System.out.println("--> 上传文件记录数据库记录");
        }
    }

    public boolean checkFileStream(InputStream inputStream) {
        boolean ret = false;
        if (inputStream == null) {
            return ret;
        }
        try {
            String type = FileTypeJudge.getFileType(inputStream);
            String[] fileHeaders = webConf.allowUploadFileHeaders.split(",");

            for (String fileHeader : fileHeaders) {
                if (fileHeader.equals(type)) {
                    ret = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ret;
        }
        return ret;
    }

    public File compress(File tempFile) throws Exception {
        File compressOutFile = new File(tempFile.getParent(), "compress_" + tempFile.getName());
        pngquantatorService.compress(tempFile, compressOutFile);
        return compressOutFile;
    }
}
