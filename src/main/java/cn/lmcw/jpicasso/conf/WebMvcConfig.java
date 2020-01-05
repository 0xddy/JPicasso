package cn.lmcw.jpicasso.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    WebConf webConf;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new ResHandlerInterceptor())
//                .addPathPatterns("/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        File uploadDir = new File(webConf.uploadDir);
        if (uploadDir.exists()) {
            registry.addResourceHandler("/**")
                    .addResourceLocations("file:" + uploadDir.getAbsolutePath() + File.separator);

            System.out.println("--> 设置本地存储文件夹映射：" + uploadDir.getAbsolutePath());
        }
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}
