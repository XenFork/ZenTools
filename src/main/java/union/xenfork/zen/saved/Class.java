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

    @Alias("extends")
    public ArrayList<String> extends_class;

    public void addImports(String import_) {
        if (imports == null)imports = new ArrayList<>();
        imports.add(import_);
    }

    public ArrayList<String> getImports() {
        return imports;
    }

    public void addMethod(Method method) {
        if (this.methods == null) {
            this.methods = new ArrayList<>();
        }
        methods.add(method);
    }

    public ArrayList<Method> getMethods() {
        return methods;
    }

    public void addFields(Field field) {
        if (this.fields == null) fields = new ArrayList<>();
        fields.add(field);
    }

    public ArrayList<Field> getFields() {
        return fields;
    }
}
