package org.ppietrzak.grounddatacore.config.openaq;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OpenAQEndpoints {
    private String locations;
    private String measurements;
    private String parameters;
}
