package com.fiafeng.common.utils;

import com.fiafeng.common.utils.spring.StrFormatter;

import java.util.Iterator;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Fiafeng
 * @create 2023/12/05
 * @description
 */
public class StringUtils {

    /**
     * 空字符串
     */
    private static final String NULL_STR = "";

    /**
     * 下划线
     */
    private static final char SEPARATOR = '_';

    public static final String EMPTY = "";

    private static final int STRING_BUILDER_SIZE = 256;



    /**
     * 输入下划线的字符串，返回驼峰风格字符串
     * @param underline 下划线格式的字符串
     * @return 驼峰风格字符串
     */
    public static String underlineToCamel(String underline){
        if (underline == null ||underline.isEmpty())
            return underline;

        Matcher matcher = Pattern.compile("_(\\w)").matcher(underline);
        StringBuffer camel = new StringBuffer();

        while (matcher.find()){
            matcher.appendReplacement(camel,matcher.group(1).toUpperCase());
        }


        return camel.toString().isEmpty() ? underline : camel.toString();
    }


    /**
     * 将输入字符串的驼峰命名转换成为下划线命名
     * @param camel 输入的驼峰风格字符串
     * @return 下划线风格方法的字符串
     */
    public static String camelToUnderline(String camel){
        if (camel == null ||camel.isEmpty())
            return camel;

        Matcher matcher = Pattern.compile("([A-W])").matcher(camel.substring(1));
        StringBuffer underline = new StringBuffer(camel.substring(0,1));

        while (matcher.find()){
            matcher.appendReplacement(underline, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(underline);
        String lowerCase = underline.toString().toLowerCase();
        return lowerCase.isEmpty()? camel: lowerCase;
    }

    /**
     * 将输入字符串的驼峰命名转换成为短横线命名法
     * @param camel 输入的驼峰风格字符串
     * @return 下划线风格方法的字符串
     */
    public static String camelToKebab(String camel){
        if (camel == null ||camel.isEmpty())
            return camel;

        Matcher matcher = Pattern.compile("([A-W])").matcher(camel.substring(1));
        StringBuffer underline = new StringBuffer(camel.substring(0,1));

        while (matcher.find()){
            matcher.appendReplacement(underline, "-" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(underline);
        String lowerCase = underline.toString().toLowerCase();
        return lowerCase.isEmpty()? camel: lowerCase;
    }

    /**
     * * 判断一个字符串是否为空串
     *
     * @param str String
     * @return true：为空 false：非空
     */
    public static boolean strIsEmpty(String str) {
        return ObjectUtils.isNull(str) || NULL_STR.equals(str.trim());
    }

    /**
     * * 判断一个字符串是否为非空串
     *
     * @param str String
     * @return true：非空串 false：空串
     */
    public static boolean strNotEmpty(String str) {
        return !strIsEmpty(str);
    }


    /**
     * 是否包含字符串
     *
     * @param str  验证字符串
     * @param strings 字符串组
     * @return 包含返回true
     */
    public static boolean inStringIgnoreCase(String str, String... strings) {
        if (str != null && strings != null) {
            for (String s : strings) {
                if (str.equalsIgnoreCase(strTrim(s))) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 去空格
     */
    public static String strTrim(String str) {
        return (str == null ? "" : str.trim());
    }


    /**
     * 格式化文本, {} 表示占位符<br>
     * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
     * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
     * 例：<br>
     * 通常使用：format("this is {} for {}", "a", "b") -> this is a for b<br>
     * 转义{}： format("this is \\{} for {}", "a", "b") -> this is \{} for a<br>
     * 转义\： format("this is \\\\{} for {}", "a", "b") -> this is \a for b<br>
     *
     * @param template 文本模板，被替换的部分用 {} 表示
     * @param params   参数值
     * @return 格式化后的文本
     */
    public static String strFormat(String template, Object... params) {
        if (ObjectUtils.isEmpty(params) || strIsEmpty(template)) {
            return template;
        }
        return StrFormatter.format(template, params);
    }

    public static String join(final Object[] array, final String delimiter, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        if (endIndex - startIndex <= 0) {
            return EMPTY;
        }
        final StringJoiner joiner = new StringJoiner(toStringOrEmpty(delimiter));
        for (int i = startIndex; i < endIndex; i++) {
            joiner.add(toStringOrEmpty(array[i]));
        }
        return joiner.toString();
    }

    private static String toStringOrEmpty(final Object obj) {
        return Objects.toString(obj, EMPTY);
    }


    public static String join(final Object[] array, final String delimiter) {
        if (array == null) {
            return null;
        }
        return join(array, delimiter, 0, array.length);
    }

    public static String join(final Iterable<?> iterable, final String separator) {
        if (iterable == null) {
            return null;
        }
        return join(iterable.iterator(), separator);
    }


    public static String join(final Iterator<?> iterator, final String separator) {

        // handle null, zero and one elements before building a buffer
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return EMPTY;
        }
        final Object first = iterator.next();
        if (!iterator.hasNext()) {
            return Objects.toString(first, "");
        }

        // two or more elements
        final StringBuilder buf = new StringBuilder(STRING_BUILDER_SIZE); // Java default is 16, probably too small
        if (first != null) {
            buf.append(first);
        }

        while (iterator.hasNext()) {
            if (separator != null) {
                buf.append(separator);
            }
            final Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }
        return buf.toString();
    }
}
