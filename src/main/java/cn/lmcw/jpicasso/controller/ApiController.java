package cn.lmcw.jpicasso.controller;


import cn.lmcw.jpicasso.JpicassoApplication;
import cn.lmcw.jpicasso.conf.WebConf;
import cn.lmcw.jpicasso.entity.UploadImage;
import io.objectbox.Box;
import io.objectbox.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/api")
public class ApiController extends CoreController {

    @Autowired
    WebConf webConf;

    @GetMapping("/api/_all/list")
    @ResponseBody
    public Object list(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "") String apikey) {
        if (apikey.equals(webConf.apiKey)) {
            if (page <= 0)
                page = 1;
            int limit = 50;

            int offset = (page - 1) * page;

            Box<UploadImage> box = JpicassoApplication.boxFor(UploadImage.class);
            QueryBuilder<UploadImage> queryBuilder = box.query();
            List<UploadImage> uploadImages = queryBuilder.build().find(offset, limit);
            return uploadImages;

        } else {

            return fail(403, "apikey failed");
        }
    }

    @GetMapping("/_delete/list")
    @ResponseBody
    public Object delete(@RequestParam(defaultValue = "-1") long id, @RequestParam(defaultValue = "") String apikey) {
        if (apikey.equals(webConf.apiKey)) {
            if (id == -1) {
                Box<UploadImage> imageBox = JpicassoApplication.boxFor(UploadImage.class);
                long count = imageBox.count();
                imageBox.removeAll();
                return "delete all ! count：" + count;

            } else {
                return "delete id：" + id + "  " + JpicassoApplication.boxFor(UploadImage.class).remove(id);
            }

        } else {

            return fail(403, "apikey failed");
        }
    }
}
