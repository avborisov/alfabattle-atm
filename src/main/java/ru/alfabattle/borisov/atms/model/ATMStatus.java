package ru.alfabattle.borisov.atms.model;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class ATMStatus {

  private AvailableNow availableNow;
  private Integer deviceId;
  private ZonedDateTime recordUpdated;

}

