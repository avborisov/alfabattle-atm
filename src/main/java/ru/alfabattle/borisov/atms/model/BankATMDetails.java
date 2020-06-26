package ru.alfabattle.borisov.atms.model;

import lombok.Data;

import java.util.List;

@Data
public class BankATMDetails {

  private List<ATMDetails> atms = null;
  private String bankLicense;

}

