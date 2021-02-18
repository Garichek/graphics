package com.gari.graph.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gari.graph.config.JacksonDateDeserializer;
import com.gari.graph.config.JacksonDateSerializer;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
public class TemporaryPoint implements Serializable {

    /**
     * Uss position identifier
     */
    private String enroutePositionsId = null;

    /**
     * Point number in a list
     */
    @JsonProperty("n")
    protected Integer number;

    /**
     * Geographical coordinates
     */
    @JsonProperty("lat")
    protected double latitude;

    @JsonProperty("lng")
    protected double longitude;

    /**
     * Point label
     */
    @JsonProperty("l")
    protected String label;

    /**
     * id of an object where the way point belongs(base, storage, recharge base etc.)
     */
    @JsonProperty("o")
    private Long objectId;

    @JsonProperty
    private double pitch;

    @JsonProperty
    private double roll;

    @JsonProperty
    private double yaw;

    @JsonProperty
    private double course;

    /**
     * Trace field for course to destination info
     */
    @JsonIgnore
    private double courseToDestination;

    @JsonProperty("h")
    private double altitude; // altitude above sea level, m

    /**
     * Altitude above Start Point level, m
     * The height obtained by telemetry from the drone -
     * is the height above the surface 'AGL' at the starting point of the drone
     */
    @JsonProperty("hasp")
    private double altitudeASP; // altitude above Start Point level, m

    @JsonProperty("hagl")
    private double altitudeAGL; // altitude above ground level, m

    /**
     * Trace parameter for understanding how many meters remained to reach destination height
     */
    @JsonIgnore
    private double heightToDestination;

    @JsonProperty("rh")
    private double reliefHeight;

    /**
     * Relief Height value (as MSL level in meters) in a First Point of Telemetry
     * (or Second if some error with the first),
     * obtained from Elevation Files
     */
    @JsonProperty("frh")
    private double fileReliefHeight;

    /**
     * Speed m/s
     */
    @JsonProperty("s")
    private double speed;

    @JsonProperty("vs")
    private double verticalSpeed;

    @JsonIgnore
    private double destinationSpeed;

    /**
     * Indicates waypoint belonging
     */
    @JsonProperty
    private int knot;

    @JsonProperty("d")
    private double distance;

    @JsonProperty("ft")
    private double flightTime;

    @JsonProperty("ms")
    private boolean motorsStatus;

    @JsonProperty("fs")
    private boolean flying;

    /**
     * Trace field for distance to destination info
     */
    @JsonIgnore
    private Double distanceToDestination;

    /**
     * Trace field for understanding do the drone reached destination
     */
    @JsonIgnore
    private boolean reachedDestination = false;

    /**
     * Battery charge in mAh
     */
    @JsonIgnore
    private double charge;

    /**
     * Battery charge in percents
     */
    @JsonProperty("cp")
    private double chargePercents;

    @JsonProperty("cr")
    private Boolean crash = null;

    /**
     * Trace field for distance to change speed
     */
    @JsonIgnore
    private Double distanceToChangeSpeed;

    @JsonProperty("c")
    private boolean isConnected = true;

    @JsonProperty("gsc")
    private int gpsSatelliteCount;

    @JsonProperty
    private boolean cellAvailability = true;

    @JsonProperty
    private boolean manual = false;

//    @JsonProperty("dt")
//    private TemporaryPointDetails details;

    @JsonProperty
    private String commandName;

//    @JsonIgnore
//    private PointPhaseEnum phase;


    // Time set by telemetry source (drone side) as 'Long timestamp' and converted to OffsetDateTime
    @JsonProperty("time")
    @JsonSerialize(using = JacksonDateSerializer.class)
    @JsonDeserialize(using = JacksonDateDeserializer.class)
    private OffsetDateTime time;

    @JsonProperty
    @JsonSerialize(using = JacksonDateSerializer.class)
    @JsonDeserialize(using = JacksonDateDeserializer.class)
    private OffsetDateTime timeReceived;

//    @JsonProperty
//    private TelemetrySource source = TelemetrySource.NOT_DETECT;

    private long latency;

    public TemporaryPoint() {
    }


}
