package union.xenfork.zen.saved;

import cn.hutool.core.annotation.Alias;

import java.util.ArrayList;

public class Class {
    @Alias("imports")
    public ArrayList<String> imports;
    @Alias("name")
    public String name;//zen class name
    @Alias("methods")
    public ArrayList<Method> methods;
    @Alias("fields")
    public ArrayList<Field> fields;

    public void addImports(String import_) {
        if (imports == null)imports = new ArrayList<>();
        imports.add(import_);
    }

    public ArrayList<String> getImports() {
        return imports;
    }
}
