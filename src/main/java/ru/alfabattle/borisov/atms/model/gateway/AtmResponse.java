package ru.alfabattle.borisov.atms.model.gateway;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AtmResponse {

  private String city;
  private Integer deviceId;
  private String latitude;
  private String location;
  private String longitude;
  private Boolean payments;
}

