package cn.lmcw.jpicasso.service;

import cn.lmcw.jpicasso.conf.WebConf;
import cn.lmcw.jpicasso.service.base.BaseService;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

import java.io.File;

public class QiniuService extends BaseService {
    @Override
    public void compress(File inFile, File outFile) throws Exception {

    }

    private String qiniuAccessKey;
    private String qiniuSecretKey;
    private String qiniuBucketName;

    private UploadManager uploadManager;
    private Auth auth;
    private BucketManager bucketManager;

    private Configuration cfg;

    public QiniuService(WebConf webConf) {
        super(webConf);
        if (uploadManager == null || bucketManager == null) {
            qiniuAccessKey = webConf.qiniuSecretId;
            qiniuSecretKey = webConf.qiniuSecretKey;
            qiniuBucketName = webConf.qiniuBucketName;
            //构造一个带指定 Region 对象的配置类
            cfg = new Configuration(Region.autoRegion());
            //...生成上传凭证，然后准备上传
            auth = Auth.create(qiniuAccessKey, qiniuSecretKey);

            //...其他参数参考类注释
            uploadManager = new UploadManager(cfg);
            bucketManager = new BucketManager(auth, cfg);

        }


    }

    @Override
    public void syncYun(File file, String path) throws Exception {

        String upToken = auth.uploadToken(qiniuBucketName);
        try {
            uploadManager.put(file, path, upToken);
            System.out.println("---> Uploaded 七牛云 bucketName：" + qiniuBucketName + " " + path);

        } catch (QiniuException ex) {
            Response r = ex.response;
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }
        super.syncYun(file,path);
    }

    @Override
    public void deleteYunFile(String file) {
        try {
            bucketManager.delete(qiniuBucketName, file);
            System.out.println("---> 删除七牛云文件 " + file);
        } catch (Exception e) {
            System.out.println("----> 删除七牛文件异常 " + file);
            System.out.println("----> 七牛异常信息 " + e.getMessage());
        }

    }
}
