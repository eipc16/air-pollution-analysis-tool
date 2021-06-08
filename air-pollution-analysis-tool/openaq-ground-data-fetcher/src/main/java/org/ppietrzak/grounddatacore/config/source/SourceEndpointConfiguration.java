package org.ppietrzak.grounddatacore.config.source;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SourceEndpointConfiguration {
    private String data;
    private String heartBeat;
    private String reindex;
    private String reindexStatus;
}