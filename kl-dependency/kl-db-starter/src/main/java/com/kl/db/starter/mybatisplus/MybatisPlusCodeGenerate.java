package com.kl.db.starter.mybatisplus;


import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.FileType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.io.File;
import java.util.*;

/**
 * mybatis-plus 自动生成代码
 */
public class MybatisPlusCodeGenerate {

    private static String projectPath = System.getProperty("user.dir");
    private static String basePwd = projectPath + "/src/main/java";
    private static String mybatisDir = ".data";
    public static void main(String[] args) {
        String jdbcUrl = args[0];
        String username = args[1];
        String password = args[2];
        String author = args[3];
        String basePackage = args[4];

        String[] tables = Arrays.copyOfRange(args, 5, args.length);
        // 代码生成器

        Map<String, String[]> map = handleTable(tables);
        map.forEach((module, values) -> {
            AutoGenerator mpg = createAutoGenerator(author, jdbcUrl, username, password);
            System.out.println("生成模块开始："+module);
            generatorCode(mpg, projectPath, basePwd, basePackage, module, values);
            System.out.println("生成模块完成："+module);
        });

    }

    /**
     * 构造不同模块的数据源
     * @param author
     * @param jdbcUrl
     * @param username
     * @param password
     * @return
     */
    public static AutoGenerator createAutoGenerator(String author, String jdbcUrl, String username, String password){
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();

        gc.setOutputDir(basePwd);
        gc.setAuthor(author);
        gc.setOpen(false);
        gc.setEntityName("%sEntity");
        gc.setMapperName("%sMapper");
//        gc.setXmlName("%sMapper");
        // gc.setSwagger2(true); 实体属性 Swagger2 注解
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(jdbcUrl + "?useUnicode=true&useSSL=false&characterEncoding=utf8");
        // dsc.setSchemaName("public");
//        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setDriverName("com.mysql.jdbc.Driver");
        dsc.setUsername(username);
        dsc.setPassword(password);
        mpg.setDataSource(dsc);
        return mpg;

    }

    /**
     * 按模块转化table
     * @param tables
     * @return
     */
    public static Map<String, String[]> handleTable(String[] tables){
        Map<String, List<String>> map = new HashMap<>();
        Map<String, String[]> mapResult = new HashMap<>();
        Arrays.stream(tables).forEach(table ->{
            table = table.startsWith("/")? table.substring(1):table;
            String[] modelTable = table.split("/");
            if (modelTable.length == 1){
                List<String> list = map.getOrDefault("", new ArrayList<>());
                list.add(modelTable[0]);
                map.put("", list);
            } else {
                List<String> list = map.getOrDefault(modelTable[0], new ArrayList<>());
                list.add(modelTable[1]);
                map.put(modelTable[0], list);
            }

        });
        map.forEach((key, value) ->{
            String[] va = new String[value.size()];
            value.toArray(va);
            mapResult.put(key,va);
        });
        return mapResult;
    }

    /**
     * 检查文件目录，不存在自动递归创建
     *
     * @param filePath 文件路径
     */
    static void checkDir(String filePath) {
        File file = new File(filePath);
        boolean exist = file.exists();
        if (!exist) {
            file.mkdirs();
        }
    }
    /**
     * 执行生成代码
     * @param mpg 数据源
     * @param projectPath 项目路径
     * @param basePwd 生成目录位置
     * @param moduleName 模块名称
     * @param tables 表名
     */
    public static void generatorCode(AutoGenerator mpg, String projectPath, String basePwd,
                                     String basePackage, String moduleName, String[] tables){
        mkdirs(basePackage);
        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setModuleName(moduleName);
        pc.setParent(basePackage + mybatisDir);
        pc.setController("");
        pc.setService("");
        pc.setServiceImpl("");
        pc.setXml("");
        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };

        // 如果模板引擎是 freemarker
        String templatePath = "/templates/mapper.xml.ftl";
        // 如果模板引擎是 velocity
        // String templatePath = "/templates/mapper.xml.vm";

        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {

            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                String mapperXml = projectPath + "/src/main/resources/mapper/" + pc.getModuleName()
                        + "/";
                checkDir(mapperXml);
                return mapperXml + tableInfo.getMapperName() + StringPool.DOT_XML;
            }
        });
        cfg.setFileCreate(new IFileCreate() {
            @Override
            public boolean isCreate(ConfigBuilder configBuilder, FileType fileType, String filePath) {
                // 判断自定义文件夹是否需要创建
                String servicePackage = basePwd + "/" + basePackage.replace(".", "/");
                checkDir(servicePackage + "/do");
                checkDir(servicePackage + "/po");
                checkDir(servicePackage + "/impl");
                checkDir(servicePackage + "/consumer");
                if (fileType == FileType.ENTITY) {
                    // entity 永远覆盖生成
                    return true;
                }
                // 不生成service、controller
                if (fileType == FileType.SERVICE || fileType == FileType.SERVICE_IMPL || fileType == FileType.CONTROLLER) {
                    return false;
                }
                // 判断文件是否存在
                return !new File(filePath).exists();
            }
        });
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        // 配置自定义输出模板
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        // templateConfig.setEntity("templates/entity2.java");
        // templateConfig.setService();
        // templateConfig.setController();

        templateConfig.setXml(null);
        mpg.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        //strategy.setSuperEntityClass("你自己的父类实体,没有就不用设置!");
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        // 公共父类
        //strategy.setSuperControllerClass("你自己的父类控制器,没有就不用设置!");
        // 写于父类中的公共字段
        //strategy.setSuperEntityColumns("id");
        //strategy.setInclude(scanner("t_course,t_live_chat").split(","));
        //strategy.setInclude("t_course","t_live_chat");
        strategy.setInclude(tables);
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setTablePrefix(pc.getModuleName() + "_");
        mpg.setStrategy(strategy);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }

    /**
     * 创建包
     *
     * @param basePackage 基础包
     */
    private static void mkdirs(String basePackage) {
        String servicePackage = basePwd + "/" + basePackage.replace(".", "/");
        // 基础业务
        checkDir(servicePackage + "/biz");
        // dao层输出参数
        checkDir(servicePackage + "/po");
        // dubbo 实现
        checkDir(servicePackage + "/impl");
        // 消费包
        checkDir(servicePackage + "/consumer/kafka");
        checkDir(servicePackage + "/consumer/rocketmq");
    }
}
