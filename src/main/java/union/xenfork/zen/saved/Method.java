package union.xenfork.zen.saved;

import cn.hutool.core.annotation.Alias;

import java.util.ArrayList;

public class Method {
    @Alias("name")
    public String name;
    @Alias("type")
    public String returnType;
    @Alias("import")
    public String inTheClass;//检索所在位置自动添加import
    @Alias("args")
    public ArrayList<Arg> args;

    public void addArgs(Arg arg) {
        if (this.args == null) args = new ArrayList<>();
        args.add(arg);
    }
}
