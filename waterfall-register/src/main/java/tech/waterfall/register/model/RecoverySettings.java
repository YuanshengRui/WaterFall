package tech.waterfall.register.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecoverySettings {
    long staleMinutes;
    int maxRetries;

    public RecoverySettings() {
    }

    public RecoverySettings(long staleMinutes, int maxRetries) {
        this.staleMinutes = staleMinutes;
        this.maxRetries = maxRetries;
    }
}
