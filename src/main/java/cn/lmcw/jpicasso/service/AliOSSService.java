package cn.lmcw.jpicasso.service;

import cn.lmcw.jpicasso.conf.WebConf;
import cn.lmcw.jpicasso.service.base.BaseService;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;

import java.io.File;

public class AliOSSService extends BaseService {
    @Override
    public void compress(File inFile, File outFile) throws Exception {

    }

    private String accessKeyId;
    private String accessKeySecret;
    private String endpoint;
    private String bucketName;

    private static OSS ossClient;

    public AliOSSService(WebConf webConf) {
        super(webConf);
        accessKeyId = webConf.aliAccessKeyId;
        accessKeySecret = webConf.aliAccessKeySecret;
        bucketName = webConf.ossBucketName;
        endpoint = webConf.ossEdpoint;
        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    /**
     * @param file        源文件
     * @param ossFilePath 20190101/123.png 不能是 /  \开头
     * @throws Exception
     */
    @Override
    public void syncYun(File file, String ossFilePath) throws Exception {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
                ossFilePath, file);
        ossClient.putObject(putObjectRequest);
        System.out.println("---> Uploaded 阿里云 bucketName：" + bucketName + " " + ossFilePath);
        super.syncYun(file,ossFilePath);
    }

    public void deleteYunFile(String path) {
        //  20190201/123.png
        ossClient.deleteObject(bucketName, path);
        System.out.println("---> 删除阿里云OSS文件 " + path);
    }

}
