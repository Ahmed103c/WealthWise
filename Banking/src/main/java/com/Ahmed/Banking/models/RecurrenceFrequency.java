package com.Ahmed.Banking.models;

import lombok.Getter;

@Getter
public enum RecurrenceFrequency {
    NONE("No recurrence"),
    DAILY("Every day"),
    WEEKLY("Every week"),
    MONTHLY("Every month"),
    YEARLY("Every year");

    private final String description;

    RecurrenceFrequency(String description) {
        this.description = description;
    }

}
