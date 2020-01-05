package cn.lmcw.jpicasso.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

@Component
@PropertySource(value = "classpath:application.properties", encoding = "UTF-8")
public class WebConf {

    @Value("${website.title}")
    public String websiteTitle;
    @Value("${website.keywords}")
    public String websiteKeywords;
    @Value("${website.upload.picurl}")
    public String websiteUploadPicUrl;
    @Value("${website.upload.file.headers}")
    public String allowUploadFileHeaders;
    @Value("${website.upload.tempdir}")
    public String uploadTempDir;
    @Value("${website.upload.dir}")
    public String uploadDir;
    @Value("${website.upload.url.prefix}")
    public String uploadUrlPrefix;
    @Value("${compress.pngquant.path}")
    public String compressPngquantBinPath;
    @Value("${compress.magick.path}")
    public String compressMagickBinPath;
    @Value("${compress.magick.quality}")
    public String compressMagickQuality;
    @Value("${compress.magick.limit.min}")
    public int magickLimitMin;

    @Value("${compress.pngquant.speed}")
    public int compressPngquantSpeed;
    @Value("${compress.pngquant.quality}")
    public String compressPngquantQuality;
    @Value("${file.yun}")
    public int fileYun;
    @Value("${file.yun.ali.accessKeyId}")
    public String aliAccessKeyId;
    @Value("${file.yun.ali.accessKeySecret}")
    public String aliAccessKeySecret;
    @Value("${file.yun.alioss.bucketName}")
    public String ossBucketName;
    @Value("${file.yun.alioss.endpoint}")
    public String ossEdpoint;

    @Value("${file.yun.cos.secretId}")
    public String cosSecretId;
    @Value("${file.yun.cos.secretKey}")
    public String cosSecretKey;
    @Value("${file.yun.cos.region}")
    public String cosRegion;
    @Value("${file.yun.cos.bucketName}")
    public String cosBucketName;

    @Value("${file.yun.qiniu.secretId}")
    public String qiniuSecretId;
    @Value("${file.yun.qiniu.secretKey}")
    public String qiniuSecretKey;
    @Value("${file.yun.qiniu.bucketName}")
    public String qiniuBucketName;

    @Value("${file.yun.sftp.host}")
    public String yunSftpHost;
    @Value("${file.yun.sftp.username}")
    public String yunSftpUserName;
    @Value("${file.yun.sftp.port}")
    public int yunSftpPort;
    @Value("${file.yun.sftp.password}")
    public String yunSftpPwd;
    @Value("${file.yun.sftp.path}")
    public String yunSftpPath;

    @Value("${file.yun.check}")
    public int yunCheck;

    @Value("${file.check.md5}")
    public int checkFileMd5;

    @Value("${compress.mode}")
    public int compressMode;

    @Value("${baidu.aip.appid}")
    public String aipAppId;
    @Value("${baidu.aip.apiKey}")
    public String aipApiKey;
    @Value("${baidu.aip.secretKey}")
    public String aipSecretKey;

    @Value("${server.proxy.safeip}")
    public String safeProxyIp;

    @Value("${upload.info.write}")
    public int uploadInfoWrite;
    @Value("${checked.delete}")
    public int checkedDelete;

    @Value("${file.yun.baidu.accessKeyId}")
    public String baiduAccessKeyId;
    @Value("${file.yun.baidu.secretAccessKey}")
    public String baiduSecretAccessKey;
    @Value("${file.yun.baidu.bucketName}")
    public String baiduBucketName;
    @Value("${file.yun.baidu.type}")
    public int bosType;

    @Value("${server.apikey}")
    public String apiKey;

    @Value("${website.header}")
    public String headerHtml;

    @Value("${upload.local}")
    public int uploadLocal;

    @Value("${upload.file.maxsize}")
    public DataSize maxUploadFileSize;

}
