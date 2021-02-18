package com.gari.graph.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
public class PointsData implements Serializable {

    @JsonProperty
    private List<TemporaryPoint> simPoints;

    @JsonProperty
    private List<TemporaryPoint> realPoints;

}
