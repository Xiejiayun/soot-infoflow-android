package nju.software.enums;

/**
 * Created by Xie on 2016/2/29.
 */
 //信息流Enum，列举出具体的信息流方法
public enum InfoflowEnum {
    //源点到沉淀点，默认为此
    SOURCETOSINK("SOURCETOSINK"),
    //源点到出口点
    SOURCETOEXIT("SOURCETOEXIT"),
    //入口点到沉淀点
    ENTRYTOSINK("ENTRYTOSINK"),
    //入口点到出口点
    ENTRYTOEXIT("ENTRYTOEXIT"),
    //入口点到源点，用于构造Android权限方法
    ENTRYTOSOURCE("ENTRYTOSOURCE");

    private String type;

    InfoflowEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
