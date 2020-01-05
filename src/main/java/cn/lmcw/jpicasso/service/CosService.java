package cn.lmcw.jpicasso.service;

import cn.lmcw.jpicasso.conf.WebConf;
import cn.lmcw.jpicasso.service.base.BaseService;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;

import java.io.File;

public class CosService extends BaseService {
    @Override
    public void compress(File inFile, File outFile) throws Exception {

    }

    private static COSClient cosClient;

    private String secretId;
    private String secretKey;
    private String apRegion;
    private String bucketName;

    public CosService(WebConf webConf) {
        super(webConf);
        if (cosClient == null) {
            // 1 初始化用户身份信息（secretId, secretKey）。
            secretId = webConf.cosSecretId;
            secretKey = webConf.cosSecretKey;
            apRegion = webConf.cosRegion;
            bucketName = webConf.cosBucketName;
            COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
            // 2 设置 bucket 的区域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
            // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
            Region region = new Region(apRegion);
            ClientConfig clientConfig = new ClientConfig(region);
            cosClient = new COSClient(cred, clientConfig);
        }
    }

    @Override
    public void syncYun(File file, String path) throws Exception {
        try {
            // 指定要上传到 COS 上对象键
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, path, file);
            cosClient.putObject(putObjectRequest);
            System.out.println("---> Uploaded 腾讯云 bucketName：" + bucketName + " " + path);
            super.syncYun(file,path);
        } catch (CosClientException clientException) {
            clientException.printStackTrace();
        }
        super.syncYun(file, path);
    }

    @Override
    public void deleteYunFile(String file) {
        cosClient.deleteObject(bucketName, file);
        System.out.println("---> 删除腾讯云Cos文件 " + file);
    }
}
