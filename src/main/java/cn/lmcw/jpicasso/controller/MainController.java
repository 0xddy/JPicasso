package cn.lmcw.jpicasso.controller;

import cn.lmcw.jpicasso.JpicassoApplication;
import cn.lmcw.jpicasso.entity.UploadImage;
import com.qiniu.util.Md5;
import io.objectbox.query.QueryBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class MainController extends CoreController {


    @GetMapping("/")
    public Object index() {
        request.setAttribute("webTitle", webConf.websiteTitle);
        request.setAttribute("websiteKeywords", webConf.websiteKeywords);
        request.setAttribute("websiteUploadPicUrl", webConf.websiteUploadPicUrl);
        request.setAttribute("websiteUploadPicUrlPrefix", webConf.uploadUrlPrefix);
        request.setAttribute("websiteHeader",webConf.headerHtml);
        return "index";
    }

    @GetMapping("/user/login")
    @ResponseBody
    public String login(String user, String pwd) {

        return "wadawidaodoaijw";
    }

}
