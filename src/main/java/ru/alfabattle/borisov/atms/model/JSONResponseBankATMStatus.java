package ru.alfabattle.borisov.atms.model;

import lombok.Data;

@Data
public class JSONResponseBankATMStatus {

  private BankATMStatus data;
  private Error error;
  private Boolean success;
  private Integer total;

}

