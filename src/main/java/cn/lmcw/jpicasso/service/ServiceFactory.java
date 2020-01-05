package cn.lmcw.jpicasso.service;

import cn.lmcw.jpicasso.conf.WebConf;
import cn.lmcw.jpicasso.service.base.BaseService;
import cn.lmcw.jpicasso.service.base.SFtpService;

public class ServiceFactory {

    public static BaseService getService(WebConf webConf) {
        if (webConf.fileYun == 1) {
            return new AliOSSService(webConf);
        } else if (webConf.fileYun == 2) {
            return new CosService(webConf);
        }else if(webConf.fileYun == 3){
            return new QiniuService(webConf);
        }else if(webConf.fileYun == 4){
            return new BosService(webConf);
        }else if(webConf.fileYun==5){
            return new SFtpService(webConf);
        }
        return new BaseService(webConf);
    }


}
