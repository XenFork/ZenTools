package union.xenfork.zen;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import union.xenfork.zen.saved.Class;
import union.xenfork.zen.saved.Method;

import java.io.File;
import java.nio.charset.StandardCharsets;
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
    public static final String importHead = "import [^|\n\r\t]+";
    public static final String classHead = "zenClass [^|\n\r\t]+ \\{";
    public static final String methodFind = "function [^|\n\r\t]+;";
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
//            System.out.println(file);
            String s = FileUtil.readUtf8String(file);
            String[] lines = s.split(LINE);
            Class clazz = null;
            String className = "";
            for (int i = 0; i < lines.length; i++) {
                if (clazz == null) {
                    clazz = new Class();
                }
                String line = lines[i].replace("    ", "");
                if (line.matches(importHead)) {
                    clazz.addImports(line.split(" ")[1]);
                }
                if (line.matches(classHead)) {
                    className = line.substring(line.indexOf("zenClass") + 8, line.indexOf("{"));
                }
                if (line.matches(methodFind)) {
//                    System.out.println(line);
                    Method method = new Method();
                    String NameArgs = line.substring(line.indexOf("function") + 8, line.lastIndexOf("as"));
                    System.out.println(NameArgs);
                    method.returnType = line.substring(line.lastIndexOf("as") + 2);
                    if (clazz.imports != null) {
                        for (String anImport : clazz.imports) {
                            String[] split = anImport.split("\\.");
                            if (split[split.length - 1].equals(method.returnType)) {
                                method.inTheClass = anImport;
                                break;
                            }
                        }
                    } else {
                        method.inTheClass = method.returnType;
                    }
                }
                if (line.equals("}")) {
                    classMap.put(className, clazz);
                }
            }
            File file1 = new File(file.getAbsolutePath().replace(".dzs", ".json").replace("scripts", "outputs"));
            String jsonPrettyStr = JSONUtil.toJsonPrettyStr(clazz);
            FileUtil.writeString(jsonPrettyStr, file1, StandardCharsets.UTF_8);
        }
    }

    private void findDzs(File path) {
        if (path.isDirectory()) {
            File[] files1 = path.listFiles();
            if (files1 != null) {
                for (File file : files1) {
                    findDzs(file);
                }
            }
        } else {
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
