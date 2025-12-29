package com.hospital.registration.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum DoctorTitle {
    RESIDENT,       // 住院医师
    ATTENDING,      // 主治医师
    ASSOCIATE_CHIEF, // 副主任医师
    DEPUTY_CHIEF_PHYSICIAN, // 副主任医师 (Added to match frontend input)
    CHIEF;           // 主任医师

    @JsonCreator
    public static DoctorTitle fromString(String value) {
        if (value == null) {
            return null;
        }
        for (DoctorTitle title : DoctorTitle.values()) {
            if (title.name().equalsIgnoreCase(value)) {
                return title;
            }
        }
        return null; // Return null if no match is found
    }
}
