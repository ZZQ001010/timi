package com.github.mybatis.entity;

import cn.org.atool.fluent.mybatis.metadata.DbType;
import com.github.mybatis.javafile.AbstractFile;
import com.github.mybatis.util.GeneratorHelper;
import com.github.mybatis.filer.AbstractFiler;
import com.github.mybatis.filer.RefsFile;
import com.github.mybatis.filer.refs.AllRefFiler;
import com.github.mybatis.filer.refs.EntityRelationFiler;
import com.github.mybatis.filer.refs.FieldRefFiler;
import com.github.mybatis.filer.refs.FormRefFiler;
import com.github.mybatis.filer.refs.MapperRefFiler;
import com.github.mybatis.filer.refs.QueryRefFiler;
import com.github.mybatis.filer.segment.BaseDaoFiler;
import com.github.mybatis.filer.segment.DefaultsFiler;
import com.github.mybatis.filer.segment.EntityHelperFiler;
import com.github.mybatis.filer.segment.FormSetterFiler;
import com.github.mybatis.filer.segment.MapperFiler;
import com.github.mybatis.filer.segment.MappingFiler;
import com.github.mybatis.filer.segment.QueryFiler;
import com.github.mybatis.filer.segment.SqlProviderFiler;
import com.github.mybatis.filer.segment.UpdaterFiler;
import com.github.mybatis.filer.segment.WrapperHelperFiler;
import lombok.Getter;

import javax.annotation.processing.Filer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import static cn.org.atool.fluent.mybatis.base.IRefs.Fix_Package;
import static cn.org.atool.fluent.mybatis.mapper.StrConstant.NEWLINE;
import static com.github.mybatis.util.GeneratorHelper.sameStartPackage;

/**
 * 所有Entity的FluentEntity信息列表
 *
 * @author darui.wu
 */
public class FluentList {
    /*
     * FluentEntity收集器
     */
    /**
     * 项目所有编译Entity类列表
     */
    @Getter
    private static final List<FluentEntity> fluents = new ArrayList<>();

    private static final Map<String, FluentEntity> map = new HashMap<>();

    /**
     * 所有entity对象的共同基础package
     */
    @Getter
    private static String samePackage = null;

    public static void addFluent(FluentEntity fluent) {
        map.put(fluent.getClassName(), fluent);
        fluents.add(fluent);
        samePackage = sameStartPackage(samePackage, fluent.getBasePack());
    }

    public static String refsPackage() {
//        return getSamePackage() + ".refs";
        return Fix_Package;
    }

    public static FluentEntity getFluentEntity(String entityName) {
        return map.get(entityName);
    }

    /**
     * 生成java文件
     *
     * @param filer  Filer
     * @param logger Consumer
     */
    public static void generate(Filer filer, Consumer<String> logger) {
        fluents.sort(Comparator.comparing(FluentEntity::getNoSuffix));
        boolean first = true;
        for (FluentEntity fluent : FluentList.getFluents()) {
            try {
                List<AbstractFiler> javaFiles = generateJavaFile(fluent);
                for (AbstractFiler javaFile : javaFiles) {
                    javaFile.javaFile().writeTo(filer);
                }
            } catch (Exception e) {
                logger.accept("FluentEntityInfo:" + fluent + NEWLINE + GeneratorHelper.toString(e));
                throw new RuntimeException(e);
            }
            if (first) {
                dbType = fluent.getDbType();
            } else if (dbType != null && !Objects.equals(dbType, fluent.getDbType())) {
                // 如果有多个数据源, 设置为未知态
                dbType = null;
            }
            first = false;
        }
        if (fluents.isEmpty()) {
            return;
        }
        for (AbstractFile file : refFiles()) {
            try {
                file.writeTo(filer);
            } catch (Exception e) {
                logger.accept("Generate Refs error:\n" + GeneratorHelper.toString(e));
                throw new RuntimeException(e);
            }
        }
    }

    private static List<AbstractFile> refFiles() {
        return Arrays.asList(
            new AllRefFiler(),
            new FieldRefFiler(),
            new QueryRefFiler(),
            new FormRefFiler(),
            new RefsFile(),
            new EntityRelationFiler(),
            new MapperRefFiler()
        );
    }

    /**
     * 生成java文件
     *
     * @param fluent FluentEntity
     */
    private static List<AbstractFiler> generateJavaFile(FluentEntity fluent) {
        return Arrays.asList(
            new MapperFiler(fluent),
            new MappingFiler(fluent),
            new EntityHelperFiler(fluent),
            new SqlProviderFiler(fluent),
            new WrapperHelperFiler(fluent),
            new QueryFiler(fluent),
            new UpdaterFiler(fluent),
            new BaseDaoFiler(fluent),
            new DefaultsFiler(fluent),
            new FormSetterFiler(fluent)
        );
    }

    private static DbType dbType;

    public static String getDbType() {
        return dbType == null ? null : dbType.name();
    }
}