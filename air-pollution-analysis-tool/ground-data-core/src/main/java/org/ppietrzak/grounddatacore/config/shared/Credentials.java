package org.ppietrzak.grounddatacore.config.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Credentials {

    private String username;
    private char[] password;
}
