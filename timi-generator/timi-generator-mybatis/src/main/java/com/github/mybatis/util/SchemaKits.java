package com.github.mybatis.util;

import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.utility.LoggingConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Collection;
import java.util.Objects;
import java.util.logging.Level;

import static com.github.mybatis.util.GeneratorHelper.isBlank;

/**
 * 利用schemacrawler工具获取表元数据信息
 *
 * @author wudarui
 */
public class SchemaKits {
    private static final String SCHEMA_NOT_FOUND = "Unable to parse schemaName from URL, please specify it explicitly: @Tables(schema=\"yourSchema\")";

    public static String getSchemaName(String schemaName, DataSource dataSource) {
        if (!isBlank(schemaName)) {
            return schemaName;
        }
        try {
            String schema = dataSource.getConnection().getSchema();
            if (isBlank(schema)) {
                schema = dataSource.getConnection().getCatalog();
            }
            if (!isBlank(schema)) {
                return schema;
            }
        } catch (Exception e) {
            throw new RuntimeException(SCHEMA_NOT_FOUND, e);
        }
        throw new RuntimeException(SCHEMA_NOT_FOUND);
    }

    public static String getSchemaName(String schemaName, String url) {
        if (!isBlank(schemaName)) {
            return schemaName;
        } else if (isBlank(url)) {
            throw new RuntimeException(SCHEMA_NOT_FOUND);
        }
        String _url = url;
        int index = url.indexOf('?');
        if (index == -1) {
            index = url.indexOf(';');
        }
        if (index > 0) {
            _url = url.substring(0, index);
        }
        int first = _url.indexOf('/');
        int last = _url.lastIndexOf('/');
        if (last - first > 3 && (last + 1) < _url.length()) {//间隔4个字符以上, 避免 jdbc:// 子串
            return _url.substring(last + 1);
        }
        throw new RuntimeException(SCHEMA_NOT_FOUND);
    }

    public static Collection<Table> getTables(Connection connection, String schemaName) {
        new LoggingConfig(Level.OFF);
        try {
            final LimitOptionsBuilder limitOptionsBuilder = LimitOptionsBuilder.builder()
                //.includeSchemas(new IncludeAll());
                .includeSchemas(new RegularExpressionInclusionRule(".?" + schemaName + ".?"))
                .tableTypes("TABLE");
            final LoadOptionsBuilder loadOptionsBuilder = LoadOptionsBuilder.builder()
                // Set what details are required in the schema - this affects the
                // time taken to crawl the schema
                .withSchemaInfoLevel(SchemaInfoLevelBuilder.standard());

            final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
                .withLimitOptions(limitOptionsBuilder.toOptions())
                .withLoadOptions(loadOptionsBuilder.toOptions());

            final Catalog catalog = SchemaCrawlerUtility.getCatalog(connection, options);
            for (Schema schema : catalog.getSchemas()) {
                String fullName = schema.getFullName();
                if (isSchema(fullName, schemaName)) {
                    return catalog.getTables(schema);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("getTables from schema[" + schemaName + "] error:" + e.getMessage(), e);
        }
        throw new RuntimeException("schema[" + schemaName + "] not found.");
    }

    static boolean isSchema(String fullName, String schemaName) {
        if (Objects.equals(fullName, schemaName)) {
            return true;
        } else {
            int len = fullName.length();
            char first = fullName.charAt(0);
            char last = fullName.charAt(len - 1);
            return !isLetterOrDigit(first) && !isLetterOrDigit(last) &&
                Objects.equals(fullName.substring(1, len - 1), schemaName);
        }
    }

    /**
     * 字符是 字母/数字/下划线
     */
    static boolean isLetterOrDigit(char ch) {
        return Character.isLetterOrDigit(ch) || ch == '_';
    }
}