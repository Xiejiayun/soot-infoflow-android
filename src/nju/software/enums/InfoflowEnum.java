package nju.software.enums;

/**
 * Created by Xie on 2016/2/29.
 */
 //信息流Enum，列举出具体的信息流方法
public enum InfoflowEnum {
    //源点到沉淀点，默认为此
    SOURCETOSINK("sourcetosink"),
    //源点到出口点
    SOURCETOEXIT("sourcetoexit"),
    //入口点到沉淀点
    ENTRYTOSINK("entrytosink"),
    //入口点到出口点
    ENTRYTOEXIT("entrytoexit"),
    //入口点到源点，用于构造Android权限方法
    ENTRYTOSOURCE("entrytosource");

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
