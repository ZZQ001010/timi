//package com.github.mybatis.javafile;
//
//import com.github.mybatis.database.IGlobalConfig;
//import com.github.mybatis.database.IGlobalConfigSet;
//import com.github.mybatis.database.ITableConfig;
//import com.github.mybatis.database.ITableConfigSet;
//import com.github.mybatis.database.config.impl.GlobalConfig;
//import com.github.mybatis.database.config.impl.TableConfigSet;
//import com.github.mybatis.database.model.TableSetter;
//import com.github.mybatis.javafile.summary.ATMFile;
//import com.github.mybatis.javafile.template.DaoImplementFile;
//import com.github.mybatis.javafile.template.DaoInterfaceFile;
//import com.github.mybatis.javafile.template.DataMapFile;
//import com.github.mybatis.javafile.template.EntityFile;
//import com.github.mybatis.javafile.template.TableMixFile;
//import com.github.mybatis.util.GeneratorHelper;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.experimental.Accessors;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.function.Consumer;
//
//import static com.github.mybatis.util.GeneratorHelper.info;
//import static com.github.mybatis.util.GeneratorHelper.isBlank;
//
///**
// * @author darui.wu
// */
//@Getter
//@Accessors(chain = true)
//public class TemplateGenerator implements IGlobalConfig, ITableConfig {
//    /**
//     * 生成test辅助文件
//     * 全局开放设置, 方便多个设置时, 最后生成ATM汇总文件
//     */
//    public static boolean withTest = false;
//    /**
//     * 生成entity文件
//     */
//    protected boolean withEntity = false;
//
//    protected TableConfigSet tableConfigs;
//    /**
//     * 全局配置
//     */
//    @Setter
//    protected GlobalConfig globalConfig;
//
//    public static IGlobalConfig build(boolean withEntity, boolean withTest) {
//        if (!withEntity && !withTest) {
//            throw new RuntimeException("At last one of Entity or Test generation must be true");
//        }
//        com.github.mybatis.javafile.TemplateGenerator generator = new com.github.mybatis.javafile.TemplateGenerator();
//        com.github.mybatis.javafile.TemplateGenerator.withTest = withTest;
//        generator.withEntity = withEntity;
//        return generator;
//    }
//
//    @Override
//    public ITableConfig globalConfig(Consumer<IGlobalConfigSet> consumer) {
//        this.globalConfig = new GlobalConfig();
//        consumer.accept(this.globalConfig);
//        return this;
//    }
//
//    @Override
//    public ITableConfig tables(Consumer<ITableConfigSet> consumer) {
//        TableConfigSet tableConfig = new TableConfigSet(this.globalConfig);
//        consumer.accept(tableConfig);
//        tables(tableConfig);
//        return this;
//    }
//
//    @Override
//    public ITableConfig relations(Consumer<TableConfigSet> consumer) {
//        consumer.accept(tableConfigs);
//        return this;
//    }
//
//    public ITableConfig tables(ITableConfigSet... tableConfigs) {
//        if (this.tableConfigs == null) {
//            this.tableConfigs = new TableConfigSet(this.globalConfig);
//        }
//        for (ITableConfigSet tableConfig : tableConfigs) {
//            this.tableConfigs.add((TableConfigSet) tableConfig);
//        }
//        return this;
//    }
//
//    private static final List<TableSetter> tables = new ArrayList<>();
//
//    /**
//     * 执行代码生成
//     */
//    @Override
//    public void execute() {
//        if (globalConfig == null) {
//            throw new RuntimeException("The global config not set.");
//        }
//        info("=== Database metadata initializing...");
////        this.tableConfigs.initTables();
//        this.tableConfigs.initRelations();
//        info("=== Preparing for file generation...");
//        this.tableConfigs.getTables().values().forEach(table -> {
//            this.generateFiles(table);
//            tables.add(table);
//        });
//        info("=== File generation finish !!!");
//        setBasePackage(this.globalConfig.getBasePackage());
//        setGlobalTestDir(this.globalConfig.getTestOutputDir());
//    }
//
//    private void generateFiles(TableSetter table) {
//        GlobalConfig gc = table.getGlobalConfig();
//
//        info("====== Handling table: " + table.getTableName());
//        if (withTest) {
//            info("====== Writing test file to " + table.getGlobalConfig().getTestOutputDir());
//            new DataMapFile(table).javaFile(gc.getTestOutputDir(), true);
//            new TableMixFile(table).javaFile(gc.getTestOutputDir(), true);
//        }
//        if (withEntity) {
//            info("====== Writing entity file to " + table.getGlobalConfig().getOutputDir());
//            new EntityFile(table).javaFile(gc.getOutputDir(), true);
//            info("====== Writing dao&impl file to " + table.getGlobalConfig().getDaoOutputDir());
//            new DaoInterfaceFile(table).javaFile(gc.getDaoOutputDir(), false);
//            new DaoImplementFile(table).javaFile(gc.getDaoOutputDir(), false);
//        }
//    }
//
//    private static String basePackage = null;
//
//    private static String globalTestDir = null;
//
//    public static void setBasePackage(String _package) {
//        basePackage = GeneratorHelper.sameStartPackage(basePackage, _package);
//    }
//
//    public static void setGlobalTestDir(String dir) {
//        if (!isBlank(dir)) {
//            globalTestDir = dir;
//        }
//    }
//
//    /**
//     * 生成汇总文件
//     */
//    public static void generateSummary() {
//        new ATMFile(basePackage, tables).javaFile(globalTestDir, true);
//    }
//}