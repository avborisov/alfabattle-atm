package ru.alfabattle.borisov.atms.model;

import lombok.Data;

import java.util.List;

@Data
public class BankATMStatus {

  private List<ATMStatus> atms = null;
  private String bankLicense;

}

