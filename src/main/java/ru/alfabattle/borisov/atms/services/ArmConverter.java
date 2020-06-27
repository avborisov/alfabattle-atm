package ru.alfabattle.borisov.atms.services;

import ru.alfabattle.borisov.atms.model.alfabank.ATMDetails;
import ru.alfabattle.borisov.atms.model.gateway.AtmResponse;

public class ArmConverter {

    public static AtmResponse convert(ATMDetails details) {
        if (details == null) {
            return null;
        }
        return AtmResponse.builder()
                .city(details.getAddress().getCity())
                .deviceId(details.getDeviceId())
                .latitude(details.getCoordinates().getLatitude())
                .longitude(details.getCoordinates().getLongitude())
                .location(details.getAddress().getLocation())
                .payments(details.getServices().getPayments().contains("Y")).build();

    }

}
