package ru.alfabattle.borisov.atms.model;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class ATMDetails {

  private PostAddress address;
  private String addressComments;
  private List<String> availablePaymentSystems ;
  private List<String> cashInCurrencies;
  private List<String> cashOutCurrencies;
  private Coordinates coordinates;
  private Integer deviceId;
  private String nfc;
  private String publicAccess;
  private OffsetDateTime recordUpdated;
  private ATMServices services;
  private SupportInfo supportInfo;
  private ATMAccess timeAccess;
  private Integer timeShift;

}

