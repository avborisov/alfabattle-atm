package ru.alfabattle.borisov.atms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ATMAccess {

    private String schedule;
    private ModeEnum mode;

    public enum ModeEnum {
        @JsonProperty("alltime")
        ALLTIME,
        @JsonProperty("schedule")
        SCHEDULE,
        @JsonProperty("noinfo")
        NOINFO;

    }

}

