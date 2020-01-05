package cn.lmcw.jpicasso.service;

import cn.lmcw.jpicasso.conf.WebConf;
import cn.lmcw.jpicasso.service.base.BaseService;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;
import com.baidubce.services.bos.model.PutObjectRequest;

import java.io.File;

public class BosService extends BaseService {

    private String accessKeyId;
    private String secretAccessKey;
    private String bucketName;

    private int bosType;

    public BosService(WebConf webConf) {
        super(webConf);
        accessKeyId = webConf.baiduAccessKeyId;
        secretAccessKey = webConf.baiduSecretAccessKey;
        bucketName = webConf.baiduBucketName;
        bosType = webConf.bosType;
    }

    @Override
    public void syncYun(File file, String path) throws Exception {

        // 初始化一个BosClient
        BosClientConfiguration config = new BosClientConfiguration();
        config.setCredentials(new DefaultBceCredentials(accessKeyId, secretAccessKey));
        BosClient client = new BosClient(config);

        PutObjectRequest request = new PutObjectRequest(bucketName, path, file);
        if (bosType == 1) {
            request.withStorageClass(BosClient.STORAGE_CLASS_STANDARD);
        } else if (bosType == 2) {
            request.withStorageClass(BosClient.STORAGE_CLASS_STANDARD_IA);
        } else if (bosType == 3) {
            request.withStorageClass(BosClient.STORAGE_CLASS_COLD);
        }

        client.putObject(request);
        System.out.println("---> Uploaded 百度云 bucketName：" + bucketName + " " + path);

        super.syncYun(file, path);
    }

    @Override
    public void deleteYunFile(String file) {

        BosClientConfiguration config = new BosClientConfiguration();
        config.setCredentials(new DefaultBceCredentials(accessKeyId, secretAccessKey));
        BosClient client = new BosClient(config);
        client.deleteObject(bucketName, file);
        System.out.println("---> 删除百度BOS文件 " + file);
        super.deleteYunFile(file);
    }
}
