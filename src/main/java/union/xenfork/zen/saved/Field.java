package union.xenfork.zen.saved;

import cn.hutool.core.annotation.Alias;

public class Field {
    @Alias("name")
    public String name;
    @Alias("type")
    public String returnType;
    @Alias("import")
    public String inTheClass;//检索所在位置自动添加import
}
