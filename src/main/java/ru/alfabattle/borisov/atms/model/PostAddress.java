package ru.alfabattle.borisov.atms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PostAddress {

  private String city;
  private String location;
  private ModeEnum mode;

  public enum ModeEnum {
    @JsonProperty("FIAS")
    FIAS,
    @JsonProperty("KLADR")
    KLADR,
    @JsonProperty("OtherDictionary")
    OTHERDICTIONARY,
    @JsonProperty("HandMade")
    HANDMADE;
  }

}

