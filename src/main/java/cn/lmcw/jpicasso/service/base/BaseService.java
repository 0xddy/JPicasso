package cn.lmcw.jpicasso.service.base;

import cn.lmcw.jpicasso.JpicassoApplication;
import cn.lmcw.jpicasso.conf.WebConf;
import cn.lmcw.jpicasso.entity.UploadImage;
import cn.lmcw.jpicasso.entity.UploadImage_;
import cn.lmcw.jpicasso.service.AliOSSService;
import cn.lmcw.jpicasso.service.BosService;
import cn.lmcw.jpicasso.service.CosService;
import cn.lmcw.jpicasso.service.QiniuService;
import com.baidu.aip.contentcensor.AipContentCensor;
import com.baidu.aip.util.Util;
import io.objectbox.Box;
import io.objectbox.query.QueryBuilder;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BaseService implements IService {

    private static volatile ExecutorService executorService = Executors.newFixedThreadPool(10);
    private static volatile AipContentCensor client;

    protected WebConf webConf;

    public BaseService() {
    }

    public BaseService(WebConf webConf) {
        this.webConf = webConf;
        if (webConf.yunCheck == 1 && client == null) {
            client = new AipContentCensor(webConf.aipAppId,
                    webConf.aipApiKey, webConf.aipSecretKey);
            // 可选：设置网络连接参数
            client.setConnectionTimeoutInMillis(2000);
            client.setSocketTimeoutInMillis(60000);
            System.out.println("-> 初始化Baidu Aip");
        }
    }

    @Override
    public void compress(File inFile, File outFile) throws Exception {

    }

    @Override
    public void syncYun(File file, String path) throws Exception {
        if (webConf.uploadInfoWrite == 1) {
            String hash = cn.lmcw.jpicasso.utils.FileUtils.getMd5(path);
            Box<UploadImage> imageBox = JpicassoApplication.boxFor(UploadImage.class);
            QueryBuilder<UploadImage> query = imageBox.query();
            query.equal(UploadImage_.hash, hash);
            UploadImage uploadImage = query.build().findFirst();

            if (uploadImage != null) {
                if (this instanceof AliOSSService) {
                    uploadImage.setAttribute("OSS");
                } else if (this instanceof CosService) {
                    uploadImage.setAttribute("COS");
                } else if (this instanceof QiniuService) {
                    uploadImage.setAttribute("Qiniu");
                } else if (this instanceof BosService) {
                    uploadImage.setAttribute("Bos");
                }
                imageBox.put(uploadImage);
            }
        }

    }

    /**
     * @param localPath {保存目录}/{前缀}/2019/123.png
     * @param yunKey    {前缀}/2019/123.png
     */
    @Override
    public void check(String localPath, String yunKey) {

        if (webConf.yunCheck == 1) {
            File file = new File(localPath);
            String fileName = file.getName();
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            byte[] imgData = null;
            try {
                imgData = Util.readFileByBytes(localPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] finalImgData = imgData;
            executorService.submit(() -> {
                if (finalImgData == null) {
                    System.out.println("byte[] is null");
                    return;
                }
                JSONObject jsonObject = null;
                jsonObject = client.imageCensorUserDefined(finalImgData, null);
                if (jsonObject != null && !jsonObject.isNull("conclusion")) {
                    //System.out.println(jsonObject.toString());
                    System.out.println("----> 安全检测中...");
                    int conclusionType = (int) jsonObject.get("conclusionType");
                    if (conclusionType == 2) {
                        //删除该文件
                        System.out.println("--> 检测到【" + ((JSONObject)jsonObject.getJSONArray("data").get(0)).get("msg") + "】文件 " + localPath);

                        try {
                            boolean deleted = false;
                            if (webConf.checkedDelete == 1) {
                                try {
                                    FileUtils.forceDelete(file);
                                } catch (IOException e) {
                                    //e.printStackTrace();
                                }
                                System.out.println("---> 删除本地文件 " + localPath);
                                if (webConf.fileYun > 0) {
                                    this.deleteYunFile(yunKey);
                                }
                                deleted = true;
                            }

                            if (webConf.uploadInfoWrite == 1) {

                                String hash = cn.lmcw.jpicasso.utils.FileUtils.getMd5(yunKey);
                                Box<UploadImage> imageBox = JpicassoApplication.boxFor(UploadImage.class);
                                QueryBuilder<UploadImage> query = imageBox.query();
                                query.equal(UploadImage_.hash, hash);
                                UploadImage uploadImage = query.build().findFirst();

                                if (uploadImage != null) {
                                    uploadImage.setAttribute(String.valueOf(conclusionType));
                                    uploadImage.setStatus(deleted ? "0" : "1"); //已删除
                                    imageBox.put(uploadImage);
                                }

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("----> 安全检测完成...未发现违法内容");
                    }

                }
            });

        }

    }

    @Override
    public void deleteYunFile(String file) {

    }
}
