package com.mstx.framework.task.util;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GeneratorCode {

    private static Logger logger = LoggerFactory.getLogger(GeneratorCode.class);

    public static void main(String[] args) {
        try {
            List<String> warnings = new ArrayList<>();
            boolean overwrite = Boolean.TRUE;
            File file = new File("generatorConfig.xml");
            ConfigurationParser configurationParser = new ConfigurationParser(warnings);
            Configuration configuration = configurationParser.parseConfiguration(file);
            DefaultShellCallback defaultShellCallback = new DefaultShellCallback(overwrite);
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(configuration, defaultShellCallback, warnings);
            myBatisGenerator.generate(null);
            warnings.stream().forEach(logger::error);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
