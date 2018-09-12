package com.mstx.framework.user.util;

import com.mstx.framwork.common.util.DateUtil;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GeneratorCode {

    private static Logger logger = LoggerFactory.getLogger(GeneratorCode.class);

    public static void main(String[] args) {
        try {
            /**
             List<String> warnings = new ArrayList<>();
             boolean overwrite = Boolean.TRUE;
             File file = new File("generatorConfig.xml");
             ConfigurationParser configurationParser = new ConfigurationParser(warnings);
             Configuration configuration = configurationParser.parseConfiguration(file);
             DefaultShellCallback defaultShellCallback = new DefaultShellCallback(overwrite);
             MyBatisGenerator myBatisGenerator = new MyBatisGenerator(configuration, defaultShellCallback, warnings);
             myBatisGenerator.generate(null);
             warnings.stream().forEach(logger::error);
             */
            String[] s = "【民生通讯】您的短信验证码为：&code&，验证码需要妥善保存请勿透漏给他人。".split("&");
            System.out.println(s[0] + "123456" + s[2]);

            Date date1 = new Date();
            Date date2 = DateUtil.addMinutes(new Date(), 2);
            System.out.println(date2.getTime() - date1.getTime());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
