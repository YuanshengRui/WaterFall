package tech.waterfall.register.model;

import org.apache.commons.lang3.StringUtils;

public interface EnumBase {
    default String value() {
        return null;
    }

    String name();

    default String getName() {
        return StringUtils.isNotEmpty(this.value()) ? this.value() : this.name();
    }
}
