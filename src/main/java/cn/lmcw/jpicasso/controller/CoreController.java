package cn.lmcw.jpicasso.controller;

import cn.lmcw.jpicasso.JpicassoApplication;
import cn.lmcw.jpicasso.conf.WebConf;
import cn.lmcw.jpicasso.entity.MyObjectBox;
import io.objectbox.BoxStore;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class CoreController {
    @Autowired
    protected HttpServletRequest request;
    @Autowired
    WebConf webConf;
 
    public Object fail(int code, String msg) {
        Map map = new HashMap(2);
        map.put("code", code);
        map.put("msg", msg);
        return map;
    }
}
