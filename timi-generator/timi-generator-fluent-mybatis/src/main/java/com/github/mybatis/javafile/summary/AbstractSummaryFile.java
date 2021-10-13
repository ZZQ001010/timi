package com.github.mybatis.javafile.summary;

import com.github.mybatis.database.model.TableSetter;
import com.github.mybatis.javafile.AbstractFile;

import java.util.List;

public abstract class AbstractSummaryFile extends AbstractFile {
    protected List<TableSetter> tables;

    public AbstractSummaryFile(String basePackage, List<TableSetter> tables) {
        this.tables = tables;
        this.packageName = basePackage;
    }
}