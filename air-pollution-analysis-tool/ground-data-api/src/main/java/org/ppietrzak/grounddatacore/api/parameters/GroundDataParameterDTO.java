package org.ppietrzak.grounddatacore.api.parameters;

import lombok.NonNull;

import java.util.Objects;

public class GroundDataParameterDTO {

    private String name;

    private GroundDataParameterDTO() {
        // empty
    }

    private GroundDataParameterDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static GroundDataParameterDTO create(@NonNull String name) {
        Objects.requireNonNull(name, "Name should be present.");
        return new GroundDataParameterDTO(name);
    }
}
