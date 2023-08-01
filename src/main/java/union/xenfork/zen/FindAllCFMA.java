package union.xenfork.zen;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import union.xenfork.zen.saved.Arg;
import union.xenfork.zen.saved.Class;
import union.xenfork.zen.saved.Field;
import union.xenfork.zen.saved.Method;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.Annotation;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author baka4n
 * @since Find All class field method args
 */
public class FindAllCFMA {

    private final ArrayList<File> files = new ArrayList<>();
    public static final String LINE = "\\r\\n|\\n";
    public static final String importHead = "import [^|\n\r\t]+";//引用
    public static final String classHead = "zenClass [^|\n\r\t]+ \\{";//头
    public static final String methodFind = "function [^|\n\r\t]+;";//方法
    public static final String fieldFind = "(val|var) [^\n\r\t]+;";//字段
    public static final String annotation = "#[^|\n\r\t]+";//注解

    public static final String searchArgAndType = " as ";//分类arg和返回值

    public static final String extends_class = "#extends [^\n\r\t]+";
    public final Map<String, Class> classMap = new HashMap<>();
    /**
     * @apiNote find dir
     * @param dir is Directory
     */
    public FindAllCFMA(File dir) {
        if (!dir.exists())
            throw new RuntimeException("don't have%s".formatted(dir));
        findDzs(dir);
        for (File file : files) {
            String s = FileUtil.readUtf8String(file);
            String[] lines = s.split(LINE);//分行读取
            Class clazz = null;
            String className = "";
            boolean isHidden = false;
            for (String value : lines) {
                if (clazz == null)
                    clazz = new Class();
                String line = value.trim();
                import_search(clazz, line);//search line about import
                if (line.matches(extends_class)) {
                    String exts = line.replace("#extends", "").trim();
                    for (String ext : exts.split(" ")) {
                        clazz.addExtendsClass(ext);
                    }
                }
                className = headSearch(className, line);//search head to get className
                isHidden = annootation(isHidden, line);
                isHidden = method(clazz, isHidden, line);
                field(clazz, line);
                end(clazz, className, line);

            }
            File file1 = new File(file.getAbsolutePath().replace(".dzs", ".json").replace("scripts", "outputs"));
            String jsonPrettyStr = JSONUtil.toJsonPrettyStr(clazz);
            FileUtil.writeString(jsonPrettyStr, file1, StandardCharsets.UTF_8);
        }

    }

    private void end(Class clazz, String className, String line) {
        if (line.equals("}")) {
            classMap.put(className, clazz);
        }
    }

    private static void field(Class clazz, String line) {
        if (line.matches(fieldFind)) {
            String nameType = line.substring(4).replace(";", "");
            String[] split = nameType.split(searchArgAndType);
            Field field = new Field();
            field.name = split[0].trim();
            field.returnType = split[1].trim();
            if (clazz.imports != null) {
                for (String anImport : clazz.imports) {
                    String[] split_ = anImport.split("\\.");
                    if (split_[split_.length - 1].equals(field.returnType)) {
                        field.inTheClass = anImport;
                        break;
                    }
                }
            }
            clazz.addFields(field);
        }
    }

    private static boolean method(Class clazz, boolean isHidden, String line) {
        if (line.matches(methodFind)) {
            if (!isHidden) {
                Method method = new Method();
                String nameArgs = line.substring(line.indexOf("function") + 8, line.lastIndexOf("as"));
                method.name = nameArgs.substring(0, nameArgs.indexOf("(")).trim();
                String argsName = nameArgs.substring(nameArgs.indexOf("(") + 1).replace(")", "");
                if (!argsName.trim().isEmpty()) {
                    for (String arg_ : argsName.split(",")) {
                        Arg arg = new Arg();
                        String[] nameT = arg_.split(searchArgAndType);
                        arg.name = nameT[0].trim();
                        arg.type = nameT[1].trim();
                        if (clazz.imports != null) {
                            for (String anImport : clazz.imports) {
                                String[] split = anImport.split("\\.");
                                if (split[split.length - 1].equals(arg.type)) {
                                    arg.inTheClass = anImport;
                                    break;
                                }
                            }
                        }
                        method.addArgs(arg);
                    }
                }
                clazz.addMethod(method);
            } else {
                isHidden = false;
            }

        }
        return isHidden;
    }

    private static boolean annootation(boolean isHidden, String line) {
        if (line.matches(annotation) && line.startsWith("#hidden")) isHidden = true;
        return isHidden;
    }

    private static String headSearch(String className, String line) {
        if (line.matches(classHead))
            className = line.substring(line.indexOf("zenClass") + 8, line.indexOf("{"));
        return className;
    }

    private static void import_search(Class clazz, String line) {
        if (line.matches(importHead))
            clazz.addImports(line.split(" ")[1].replace(";", ""));
    }

    private void findDzs(File path) {
        if (path.isDirectory()) {
            File[] files1 = path.listFiles();
            if (files1 != null) {
                for (File file : files1) {
                    findDzs(file);
                }
            }
        } else if (path.getAbsolutePath().endsWith(".dzs")) {
            files.add(path);
        }
    }

    /**
     * @apiNote test
     * @param args args
     */
    public static void main(String[] args) {
        FindAllCFMA findAllCFMA = new FindAllCFMA(new File(System.getProperty("user.dir"), MessageFormat.format("scripts{0}generated", File.separator)));
    }
}
