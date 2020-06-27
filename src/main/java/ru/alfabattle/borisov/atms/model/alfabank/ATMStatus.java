package ru.alfabattle.borisov.atms.model.alfabank;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class ATMStatus {

  private AvailableNow availableNow;
  private Integer deviceId;
  private ZonedDateTime recordUpdated;

}

