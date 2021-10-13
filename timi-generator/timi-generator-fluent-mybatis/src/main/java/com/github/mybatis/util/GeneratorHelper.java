package com.github.mybatis.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 代码生成辅助工具类
 *
 * @author wudarui
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class GeneratorHelper {
    public static void convertPath(String path) {
        convertPath(new File(path));
    }

    /**
     * 转换目录
     *
     * @param path file path
     */
    public static void convertPath(File path) {
        if (!path.isDirectory() || !path.exists()) {
            info("File not exist: " + path.getAbsolutePath());
            return;
        }
        File[] files = path.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isFile()) {
                convertFile(file);
                info("Convert file [" + file.getName() + "] success");
            } else if (file.isDirectory()) {
                convertPath(file);
            }
        }
    }

    /**
     * 转换文件
     *
     * @param file file
     * @return ignore
     */
    public static String convertFile(File file) {
        String[] lines = readLinesFromFile(file);

        StringBuilder buff = new StringBuilder();
        for (String line : lines) {
            buff.append(convertLine(line));
            buff.append("\n");
        }
        String value = buff.toString();
        writeStringToFile(file, value);
        return value;
    }

    private static final String reg = "(.*\\.)(\\w+_[\\w\\d_]+)(\\.(values|formatAutoIncrease|autoIncrease)\\(.*\\)+;?(.*))";

    private static final Pattern pattern = Pattern.compile(reg);

    public static String convertLine(String line) {
        Matcher m = pattern.matcher(line);
        if (!m.matches()) {
            return line;
        }
        return m.group(1) + underlineToCapital(m.group(2)) + m.group(3);
    }

    public static String underlineToCapital(String input) {
        if (input == null) {
            return null;
        }
        StringBuilder buff = new StringBuilder();
        boolean isUnderline = false;
        for (char ch : input.toCharArray()) {
            if (ch == '_') {
                isUnderline = true;
                continue;
            }
            buff.append(isUnderline ? String.valueOf(ch).toUpperCase() : ch);
            isUnderline = false;
        }
        return buff.toString();
    }

    public static void info(String info) {
        System.out.println(info);
    }

    public static boolean isBlank(String in) {
        if (in == null) {
            return true;
        } else {
            return in.trim().isEmpty();
        }
    }


    public static String[] readLinesFromFile(File file) {
        try {
            try (InputStream stream = new FileInputStream(file);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                String line;
                List<String> list = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    list.add(line);
                }
                return list.toArray(new String[0]);
            }
        } catch (IOException e) {
            throw new RuntimeException("Fail to read file" + e.getMessage(), e);
        }
    }

    @SuppressWarnings("all")
    public static void writeStringToFile(File file, String content) {
        File path = file.getParentFile();
        if (!path.exists()) {
            path.mkdirs();
        }
        try (FileWriter writer = new FileWriter(file, false)) {
            writer.write(content);
        } catch (IOException e) {
            throw new RuntimeException("Fail to write file: " + e.getMessage(), e);
        }
    }

    /**
     * 返回base1和base2的共同package路径
     *
     * @param base1 dir 1
     * @param base2 dir 2
     * @return shared partition
     */
    public static String sameStartPackage(String base1, String base2) {
        if (base1 == null || base2 == null) {
            return base1 == null ? base2 : base1;
        }
        String base = base1;
        while (!base2.startsWith(base)) {
            int last = base.lastIndexOf('.');
            base = base.substring(0, last);
        }
        return base;
    }

    /**
     * 将异常日志转换为字符串
     *
     * @param e exception
     * @return string
     */
    public static String toString(Throwable e) {
        try (StringWriter writer = new StringWriter(); PrintWriter print = new PrintWriter(writer)) {
            e.printStackTrace(print);
            return writer.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
}