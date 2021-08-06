package com.github.timi.assembler;

import com.github.api.IAssembler;
import com.github.constants.JavaType;
import com.github.meta.Column;
import com.github.meta.Database;
import com.github.meta.Table;
import org.apache.ibatis.ibator.api.dom.java.FullyQualifiedJavaType;
import org.apache.maven.plugin.logging.Log;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.constants.JavaType.fqjtBigDecimal;
import static com.github.constants.JavaType.fqjtByteArr;
import static com.github.constants.JavaType.fqjtDate;
import static com.github.constants.JavaType.fqjtInteger;
import static com.github.constants.JavaType.fqjtLong;
import static com.github.timi.assembler.Constants.*;
import static com.github.constants.DBType.*;
/**
 * @author zzq
 * @date 2021/8/6
 *
 *   <Object Type="Datablau.LDM.EntityComposite" /> table
 *   <Object Type="Datablau.LDM.EntityAttribute" /> colum
 *   <Property Id="90000003" Type="System.String" Value="CREATE_TIME" /> colum name
 *   <Property Id="80000002" Type="System.String" Value="DATETIME" /> colum type
 *   <Property Id="80100005" Type="System.String" Value="创建时间" /> colum desc
 *   <Property Id="80100033" Type="System.Boolean" Value="True" />   非空
 *   <Property Id="80100035" Type="System.Boolean" Value="True" />   主键
 *
 *   TODO JAVA_VERSION 处理
 */
public final class DMDAssembler implements IAssembler {
    
    private Log logger;
    
    public DMDAssembler(Log logger)
    {
        this.logger = logger;
    }
    
    @Override
    public Database assemble(File sources) throws DocumentException {
        SAXReader sar = new SAXReader();
        Document doc = sar.read(sources);
        List<Element> tableElements = doc.selectNodes(TABLE_MARK);
        List<Table> tables = new ArrayList<>();
        tableElements.forEach(tableElement -> {
            Table table = assembleTable(tableElement);
            List<Element> columnElements = tableElement.selectNodes(COLUM_MARK);
            List<Column> columns = new ArrayList();
            assembleColumn(table ,columns, columnElements);
            table.setColumns(columns);
            tables.add(table);
        });
        Database database = new Database();
        database.setDatabaseType("MYSQL");
        database.setTables(tables);
        return database;
    }
    
    private Table assembleTable(Element element) {
        Table table = new Table();
        assembleTableName(table, element);
        assembleTableDesc(table, element);
        return table;
    }
    
    private void assembleTableName(Table table, Element element) {
        Element tbNameElement = (Element)element.selectSingleNode(TABLE_NAME);
        String tableName = tbNameElement.attribute(VALUE).getValue();
        table.setTextName(tableName);
    }
    
    private void assembleTableDesc(Table table, Element element) {
        Element tbDescElement = (Element)element.selectSingleNode(TABLE_DESC);
        if (tbDescElement != null) {
            String tableDesc = tbDescElement.attribute(VALUE).getValue();
            table.setDescription(tableDesc);
        }
    }
    
    private void assembleColumn(Table table, List<Column> columns, List<Element> columElements) {
        columElements.forEach(columnElement -> {
            Column column = new Column();
            assembleColumnName(column, columnElement);
            assembleColumnType(column, columnElement);
            assembleColumnDesc(column, columnElement);
            assemblePrimaryKey(column, columnElement);
            if (column.getPrimaryKey()) {
                table.setColumns(Arrays.asList(column));
            }
            columns.add(column);
        });
    }
    
    private void assembleColumnName(Column column, Element element) {
        Element columnNameElement = (Element)element.selectSingleNode(COLUM_NAME);
        String columnName = columnNameElement.attribute(VALUE).getValue();
        column.setTextName(columnName);
        column.setId(columnName);
    }
    
    private void assembleColumnType(Column column, Element element) {
        Element columnTypeElement = (Element)element.selectSingleNode(COLUM_TYPE);
        String columnType = columnTypeElement.attribute(VALUE).getValue();
        column.setJavaType(javaType(columnType));
    }
    
    private FullyQualifiedJavaType javaType(String typeStr) {
        if(CHAR.equals(typeStr) || typeStr.startsWith(CHARACTER)
                || typeStr.startsWith(VARCHAR) || typeStr.equals(CLOB) || typeStr.startsWith(TEXT)) {
            return FullyQualifiedJavaType.getStringInstance();
        } else if(typeStr.startsWith(DECIMAL) || typeStr.startsWith(NUMERIC)) {
            return fqjtBigDecimal;
        } else if (typeStr.equals(INTEGER) || typeStr.equals(INT) || typeStr.equals(INT_UNSIGNED) ) {
            return fqjtInteger;
        } else if (typeStr.equals(BIGINT)) {
            return fqjtLong;
        } else if(typeStr.equals(DATE) || typeStr.startsWith(TIME)
                || typeStr.equals(TIMESTAMP) || typeStr.startsWith(DATETIME)) {
            return fqjtDate;
        } else if (typeStr.equals(BLOB)) {
            return fqjtByteArr;
        } else {
            throw new IllegalArgumentException("未知类型: " + typeStr);
        }
    }
    
    private void assembleColumnDesc(Column column, Element element) {
        Element columnDescElement = (Element)element.selectSingleNode(COLUM_DESC);
        String columnDesc = columnDescElement.attribute(VALUE).getValue();
        column.setDescription(columnDesc);
    }
    
    private void assemblePrimaryKey(Column column, Element element) {
        Element columnKeyElement = (Element)element.selectSingleNode(COLUM_PRIMARY_KEY);
        if (columnKeyElement != null ) {
            String columnKey = columnKeyElement.attribute(VALUE).getValue();
            if (columnKey.equals("True")) {
                column.setPrimaryKey(true);
            }
        }
    }
    
}
