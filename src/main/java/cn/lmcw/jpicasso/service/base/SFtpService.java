package cn.lmcw.jpicasso.service.base;

import cn.lmcw.jpicasso.conf.WebConf;
import cn.lmcw.jpicasso.utils.SFtp;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.io.FileInputStream;

public class SFtpService extends BaseService {


    String username;
    int port;
    String host;
    String password;

    public SFtpService(WebConf webConf) {
        super(webConf);
        username = webConf.yunSftpUserName;
        port = webConf.yunSftpPort;
        host = webConf.yunSftpHost;
        password = webConf.yunSftpPwd;
    }

    @Override
    public void syncYun(File file, String path) throws Exception {

        SFtp sFtp = SFtp.getInstance().init(host, username, password);
        sFtp.connect(sftpChannel -> {
            if (sftpChannel == null)
                return;

            String dst = path;
            File dstFile = new File(dst);
            String parentDir = dst.substring(0, dst.lastIndexOf(dstFile.getName()));
            boolean ismkdirs = false;
            try {
                sftpChannel.cd(parentDir);
            } catch (SftpException e) {
                ismkdirs = true;
            }

            if (ismkdirs) {
                String[] dirs = parentDir.split("/");
                String tempPath = webConf.yunSftpPath;
                for (String dir : dirs) {
                    if (null == dir || "".equals(dir))
                        continue;
                    tempPath += "/" + dir;
                    try {
                        boolean isCreate = false;
                        try {
                            sftpChannel.cd(tempPath);
                        } catch (SftpException e) {
                            isCreate = true;
                        }
                        if (isCreate)
                            sftpChannel.mkdir(tempPath);
                    } catch (SftpException e) {
                        //e.printStackTrace();
                        System.out.println("创建目录失败 " + tempPath);
                    }
                }
            }

            try {
                String parentPath = "";
                if (!webConf.yunSftpPath.endsWith("/")) {
                    parentPath = webConf.yunSftpPath + "/";
                }
                sftpChannel.put(new FileInputStream(file), parentPath + dst);
                System.out.println("---> Uploaded SFTP host：" + host + " " + path);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        super.syncYun(file, path);
    }

    @Override
    public void deleteYunFile(String file) {
        SFtp sFtp = SFtp.getInstance().init(host, username, password);
        sFtp.connect(sftpChannel -> {
            if (sftpChannel == null)
                return;
            try {
                String parentPath = "";
                if (!webConf.yunSftpPath.endsWith("/")) {
                    parentPath = webConf.yunSftpPath + "/";
                }
                sftpChannel.rm(parentPath + file);
                System.out.println("---> 删除Sftp文件 " + file);
            } catch (SftpException e) {
                System.out.println("---> 删除Sftp文件失败 " + file);
            }

        });

        super.deleteYunFile(file);
    }
}
