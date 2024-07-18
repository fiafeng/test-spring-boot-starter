package com.fiafeng.dynamicClass.controller;


import com.alibaba.fastjson2.JSONObject;
import com.fiafeng.common.pojo.Dto.AjaxResult;
import com.fiafeng.common.utils.StringUtils;
import com.fiafeng.dynamicClass.utils.LocalVariableInfoVisitor;
import com.fiafeng.dynamicClass.utils.ProcyonUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/dynamicClass/class")
public class ClassWriterController {

    String path = "C:\\Users\\issuser\\Desktop\\临时文件";

    @PostMapping("/v1")
    public AjaxResult ajaxResultV1(String className, @RequestBody JSONObject jsonObject) {
        String string = jsonObject.getString("className");
        if (string != null) {
            className = string;
        }

        if (StringUtils.strIsEmpty(className)) {
            return AjaxResult.error("参数不允许为空");
        }

        ProcyonUtils.procyon("D:\\environment\\maven\\apache-maven-3.5.4\\repository\\com\\xdap\\runtime\\3.2.20.13\\runtime-3.2.20.13.jar",path);


        return AjaxResult.success();
    }

    @PostMapping("/v2")
    public AjaxResult ajaxResultV2(String className, @RequestBody JSONObject jsonObject) throws URISyntaxException, IOException, ClassNotFoundException {
        String string = jsonObject.getString("className");
        if (string != null) {
            className = string;
        }

        if (StringUtils.strIsEmpty(className)) {
            return AjaxResult.error("参数不允许为空");
        }

//        copyClass(className, "C:\\Users\\issuser\\Desktop\\临时文件");


        return AjaxResult.success();
    }

    @PostMapping("/v3")
    public AjaxResult ajaxResultV3(String className, @RequestBody JSONObject jsonObject) throws Exception {
        String string = jsonObject.getString("className");
        if (string != null) {
            className = string;
        }

        if (StringUtils.strIsEmpty(className)) {
            return AjaxResult.error("参数不允许为空");
        }
        LocalVariableInfoVisitor.ss(className);
        return AjaxResult.success();
    }




    public void copyClass(String className, String targetPath) throws ClassNotFoundException, URISyntaxException, IOException {
        Class<?> aClass = Class.forName(className);
        copyClass(aClass, targetPath);
    }

    public void copyClass(Class<?> aclass, String targetPath) throws URISyntaxException, IOException {
        // 获取class文件的URL
        URL classUrl = aclass.getResource(aclass.getSimpleName() + ".class");
        // 将URL转换为路径
        Path classPath = null;
        if (classUrl != null) {
            classPath = Paths.get(classUrl.toURI());
            // 获取目标路径
            Path target = Paths.get(targetPath + "\\" + aclass.getSimpleName() + ".class");
            // 复制文件
            Files.copy(classPath, target);
        }


    }
}
