package cn.lmcw.jpicasso.controller;

import cn.lmcw.jpicasso.exception.NoCompressException;
import cn.lmcw.jpicasso.service.ServiceFactory;
import cn.lmcw.jpicasso.service.UploadService;
import cn.lmcw.jpicasso.service.base.BaseService;
import cn.lmcw.jpicasso.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class UploadController extends CoreController {

    @Autowired
    UploadService uploadService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @PostMapping(value = "/api/upload.phpNo1", produces = {"application/json;charset = UTF-8"})
    public Object upload(HttpServletRequest httpServletRequest, @RequestParam("file") MultipartFile fileUpload) {

        if (fileUpload.getSize() > webConf.maxUploadFileSize.toBytes()) {
            return fail(400, "文件大小超过限制");
        }

        boolean checkFormat = false;
        InputStream fileInputStream = null;
        try {
            checkFormat = uploadService.
                    checkFileStream(fileInputStream = fileUpload.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (fileInputStream == null) {
            return fail(402, "文件不存在");
        }
        if (!checkFormat) {
            return fail(400, "格式不正确");
        }

        //获取文件名
        String fileName = fileUpload.getOriginalFilename();
        int dotPosition;
        if ((dotPosition = fileName.lastIndexOf(".")) == -1) {
            return fail(401, "格式不正确");
        }

        Map<String, Object> data = new HashMap<>();
        String fileMd5 = null;
        if (webConf.checkFileMd5 == 1) {
            fileMd5 = FileUtils.getMD5(fileInputStream);
            System.out.println("--> file md5 " + fileMd5);
            String imgCacheUrl = redisTemplate.opsForValue().get(fileMd5);
            if (imgCacheUrl != null && !imgCacheUrl.equals("")) {
                // 有缓存走缓存读取
                data.put("code", 200);
                data.put("data", imgCacheUrl);
                data.put("fast", 1);
                return data;
            }
        }

        //获取文件后缀名
        String suffixName = fileName.substring(dotPosition);
        //重新生成文件名
        String newFileName = UUID.randomUUID() + suffixName;
        File tempDir = new File(webConf.uploadTempDir);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        File tempFile = new File(tempDir.getAbsolutePath(), newFileName);
        try {
            fileUpload.transferTo(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 压缩
        boolean isCompress;
        try {
            // 返回压缩后的图片，如果压缩成功删除临时原图
            File tempCompressFile = uploadService.compress(tempFile);
            tempFile.delete();
            tempFile = tempCompressFile;
            isCompress = true;

        } catch (Exception e) {
            if (e instanceof NoCompressException) {
                System.out.println("-> " + e.getMessage());
            } else {
                e.printStackTrace();
            }
            isCompress = false;
        }

        int code = 500;
        String img = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            String dateDir = simpleDateFormat.format(new Date());
            File uploadDir = new File(webConf.uploadDir
                    + File.separator + webConf.uploadUrlPrefix + File.separator + dateDir);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            File newFile = new File(uploadDir.getAbsolutePath(), newFileName);
            FileCopyUtils.copy(Files.newInputStream(tempFile.toPath()), Files.newOutputStream(newFile.toPath()));
            code = 200;
            String imgUrl = (webConf.uploadUrlPrefix.equals("") ? "" : "/" + webConf.uploadUrlPrefix)
                    + "/" + dateDir + "/" + newFileName;
            img = imgUrl;
            data.put("size", newFile.length());
            if (fileMd5 != null && webConf.checkFileMd5 == 1) {
                redisTemplate.opsForValue().set(fileMd5, img);
            }

            String yunKey = imgUrl.startsWith("/") ?
                    imgUrl.replaceFirst("/", "") : imgUrl;
            // 上传本地成功，写入记录
            uploadService.writeUploadInfo(httpServletRequest, isCompress, newFile, yunKey);

            // 选择同步至云存储
            BaseService iService = ServiceFactory.getService(webConf);

            try {
                iService.syncYun(newFile, yunKey);
                iService.check(newFile.getAbsolutePath(), yunKey);
                //是否本地保存
                if (webConf.fileYun > 0 && webConf.uploadLocal == 0) {
                    newFile.delete();
                    System.out.println("---> 删除本地文件");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //删除临时文件
            tempFile.delete();
        }
        data.put("code", code);
        data.put("data", img);
        data.put("isCompress", isCompress);

        return data;
    }


}
