package ru.alfabattle.borisov.atms.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
public class ATMStatus {

  private AvailableNow availableNow;
  private Integer deviceId;
  private OffsetDateTime recordUpdated;

}

