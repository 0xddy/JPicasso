package cn.lmcw.jpicasso.service;

import cn.lmcw.jpicasso.conf.WebConf;
import cn.lmcw.jpicasso.exception.NoCompressException;
import cn.lmcw.jpicasso.service.base.BaseService;
import cn.lmcw.jpicasso.utils.ShellUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class PngquantatorService extends BaseService {

    @Autowired
    WebConf webConf;

    @Override
    public void compress(File inFile, File outFile) throws Exception {

        if (webConf.compressMode == 1) {

            if (inFile.getName().endsWith(".png")) {
                File pngquantBin = new File(webConf.compressPngquantBinPath);
                if (!pngquantBin.exists() || pngquantBin.isDirectory()) {
                    throw new NoCompressException("Pngquant执行程序路径不正确，不启用压缩");
                } else {
                    String cmd = webConf.compressPngquantBinPath + " --force --verbose --ordered --speed=" +
                            webConf.compressPngquantSpeed + " --quality=" + webConf.compressPngquantQuality + " "
                            + inFile.getAbsolutePath() + " --output " + outFile.getAbsolutePath();
                    //System.out.println(cmd);
                    ShellUtils.runShell(cmd);
                    if (!outFile.exists()) {
                        throw new NoCompressException("未达到压缩阈值");
                    }
                }
            } else if (inFile.getName().endsWith(".jpg")) {
                File magickBin = new File(webConf.compressMagickBinPath);
                if (!magickBin.exists() || magickBin.isDirectory()) {
                    throw new NoCompressException("Magick执行程序路径不正确，不启用压缩");
                } else if (inFile.length() < webConf.magickLimitMin) {
                    //文件过小没必要压缩
                    throw new NoCompressException("当前图片大小过小，不启用压缩");
                } else {
                    String cmd = webConf.compressMagickBinPath + " -quality " + webConf.compressMagickQuality + " " + inFile.getAbsolutePath() + " " + outFile.getAbsolutePath();
                    //System.out.println(cmd);
                    ShellUtils.runShell(cmd);
                    if (!outFile.exists()) {
                        throw new NoCompressException("JPG压缩失败");
                    }
                }

            }

        } else {
            throw new NoCompressException("未开启压缩配置");
        }
    }

    @Override
    public void syncYun(File file, String path) throws Exception {

    }

}
