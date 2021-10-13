package com.github.mybatis.javafile;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 代码生成基类
 *
 * @author darui.wu
 */
@SuppressWarnings({"unused", "rawtypes"})
public abstract class AbstractFile {
    protected String packageName;

    protected String klassName;

    protected String comment;

    protected ClassName className() {
        return ClassName.get(packageName, klassName);
    }

    /**
     * 生成java文件
     *
     * @param srcDir 代码src路径
     */
    public final void javaFile(String srcDir, boolean forceWrite) {
        this.javaFile(new File(srcDir), forceWrite);
    }

    /**
     * 生成java文件
     *
     * @param srcDir     代码src路径
     * @param forceWrite 重写
     */
    public final void javaFile(File srcDir, boolean forceWrite) {
        if (!forceWrite && new File(srcDir + this.filePath()).exists()) {
            System.out.println(".......... File " + this.klassName + ".java already exist, skip according to configuration");
            return;
        }
        TypeSpec.Builder builder = this.getBuilder();
        this.build(builder);
        try {
            JavaFile.Builder javaBuilder = JavaFile.builder(packageName, builder.build());
            this.staticImport(javaBuilder);
            javaBuilder.build().writeTo(srcDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate java file: " + e.getMessage(), e);
        }
    }

    private String filePath() {
        return "/" + this.packageName.replace('.', '/') + "/" + this.klassName + ".java";
    }

    protected void staticImport(JavaFile.Builder builder) {
    }

    protected abstract void build(TypeSpec.Builder builder);

    protected TypeName parameterizedType(ClassName raw, TypeName... paras) {
        return ParameterizedTypeName.get(raw, paras);
    }

    protected TypeName parameterizedType(Class raw, Class... paras) {
        return ParameterizedTypeName.get(raw, paras);
    }

    /**
     * 是否接口类
     *
     * @return ignore
     */
    protected abstract boolean isInterface();

    private TypeSpec.Builder getBuilder() {
        if (this.isInterface()) {
            return TypeSpec.interfaceBuilder(klassName).addModifiers(Modifier.PUBLIC);
        } else {
            return TypeSpec.classBuilder(klassName).addModifiers(Modifier.PUBLIC);
        }
    }

    /**
     * 代码块, 或者注释块
     *
     * @param lines 代码行
     * @return ignore
     */
    protected CodeBlock codeBlock(String... lines) {
        return CodeBlock.join(Stream.of(lines).map(CodeBlock::of).collect(Collectors.toList()), "\n");
    }

    public void writeTo(Filer filer) {
        TypeSpec.Builder builder = this.getBuilder();
        CodeBlock comment = this.codeBlock("",
            this.klassName + (this.comment == null ? "" : ": " + this.comment),
            "",
            "@author powered by " + this.generatorName()
        );
        builder.addJavadoc("$L", comment);
        this.build(builder);
        try {
            JavaFile.Builder javaBuilder = JavaFile.builder(packageName, builder.build());
            this.staticImport(javaBuilder);
            javaBuilder.build().writeTo(filer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 返回生成器名称
     *
     * @return ignore
     */
    protected String generatorName() {
        return "Test4J";
    }
}