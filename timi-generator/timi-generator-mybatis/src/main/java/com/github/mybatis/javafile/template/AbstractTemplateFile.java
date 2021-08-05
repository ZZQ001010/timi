package com.github.mybatis.javafile.template;

import com.github.mybatis.database.model.TableSetter;
import com.github.mybatis.javafile.AbstractFile;

public abstract class AbstractTemplateFile extends AbstractFile {
    protected TableSetter table;

    public AbstractTemplateFile(TableSetter table) {
        this.table = table;
    }
}