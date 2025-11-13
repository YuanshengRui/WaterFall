//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package tech.waterfall.register.model;

public enum Status {
    A("A"),
    D("D");

    private final String value;

    private Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
