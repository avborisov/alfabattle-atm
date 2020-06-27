package ru.alfabattle.borisov.atms.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import ru.alfabattle.borisov.atms.model.alfabank.ATMDetails;
import ru.alfabattle.borisov.atms.model.alfabank.BankATMDetails;
import ru.alfabattle.borisov.atms.model.alfabank.JSONResponseBankATMDetails;
import ru.alfabattle.borisov.atms.model.alfabank.JSONResponseBankATMStatus;
import ru.alfabattle.borisov.atms.model.gateway.AtmResponse;
import ru.alfabattle.borisov.atms.model.gateway.ErrorResponse;
import ru.alfabattle.borisov.atms.services.ArmConverter;
import ru.alfabattle.borisov.atms.services.CoordinateUtils;

import java.awt.*;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/atms")
@Slf4j
public class AtmController {

    // Endpoints:
    public static final String ATM_SERVICE_ATMS_STATUS = "/atm-service/atms/status";
    public static final String ATM_SERVICE_ATMS = "/atm-service/atms";

    private final RestTemplate restTemplate;
    private final Environment env;

    @Autowired
    public AtmController(RestTemplate restTemplate, Environment env) {
        this.restTemplate = restTemplate;
        this.env = env;
    }

    @GetMapping(value = ATM_SERVICE_ATMS)
    public ResponseEntity<JSONResponseBankATMDetails> getAtms() {
        log.info("GET atms data invoked");
        try {
            URI url = new URI(env.getProperty("endpoint.alfa-api") + ATM_SERVICE_ATMS);
            log.info("endpoint name: {}", url.toString());
            HttpEntity<String> entity = getAtmsApiHeaders();
            return restTemplate.exchange(url, HttpMethod.GET, entity, JSONResponseBankATMDetails.class);
        } catch (Exception ex) {
            log.error("Something wrong with request to endpoint", ex);
        }
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping(value = ATM_SERVICE_ATMS_STATUS)
    public ResponseEntity<JSONResponseBankATMStatus> getAtmsStatus() {
        log.info("GET atms status invoked");
        try {
            URI url = new URI(env.getProperty("endpoint.alfa-api") + ATM_SERVICE_ATMS_STATUS);
            log.info("endpoint name: {}", url.toString());
            HttpEntity<String> entity = getAtmsApiHeaders();
            return restTemplate.exchange(url, HttpMethod.GET, entity, JSONResponseBankATMStatus.class);
        } catch (Exception ex) {
            log.error("Something wrong with request to endpoint", ex);
        }
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/{deviceId}")
    public ResponseEntity<AtmResponse> getAtmResponse(@PathVariable String deviceId) {
        log.info("GET atm status invoked, deviceId: {}", deviceId);

        if (!isNumeric(deviceId)) {
            return new ResponseEntity(new ErrorResponse("atm not found"), HttpStatus.NOT_FOUND);
        }

        try {
            ResponseEntity<JSONResponseBankATMDetails> response = getAtms();
            BankATMDetails data = response.getBody().getData();
            if (data == null) {
                return new ResponseEntity(new ErrorResponse("alfa api return no data"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            log.info("Alfa API return {} atms", data.getAtms().size());
            ATMDetails atmDetails = data.getAtms().stream()
                    .filter(atm -> atm.getDeviceId().equals(Integer.valueOf(deviceId)))
                    .findFirst().orElse(null);
            if (atmDetails == null) {
                return new ResponseEntity(new ErrorResponse("atm not found"), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity(ArmConverter.convert(atmDetails), HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Something wrong with request to endpoint", ex);
        }

        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/nearest")
    public ResponseEntity<AtmResponse> getNearestAtm(
            @RequestParam final String latitude,
            @RequestParam final String longitude,
            @RequestParam final Boolean payments) {
        log.info("GET NearestAtm invoked, latitude: {}, longitude: {}, payments: {}", latitude, longitude, payments);

        if (latitude == null || longitude == null) {
            return new ResponseEntity(new ErrorResponse("atm not found"), HttpStatus.NOT_FOUND);
        }

        try {
            ResponseEntity<JSONResponseBankATMDetails> response = getAtms();
            BankATMDetails data = response.getBody().getData();
            if (data == null) {
                return new ResponseEntity(new ErrorResponse("alfa api return no data"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            log.info("Alfa API return {} atms", data.getAtms().size());

            Map<Integer, Point> atmLocations = data.getAtms().stream()
                    .filter(atm -> {
                        if (payments == null || !payments.booleanValue()) {
                            return true;
                        }
                        if (atm.getServices().getPayments().contains("Y")) {
                            return true;
                        }
                        return false;
                    })
                    .collect(Collectors.toMap(ATMDetails::getDeviceId, this::getPoint));

            ATMDetails atm = data.getAtms().stream().filter(atmDetail -> {
                Point point = new Point();
                point.setLocation(Double.parseDouble(latitude), Double.parseDouble(longitude));
                return atmDetail.getDeviceId().equals(CoordinateUtils.getClosestDeviceId(point, atmLocations));
            }).findFirst().orElse(null);

            return new ResponseEntity(ArmConverter.convert(atm), HttpStatus.OK);

        } catch (Exception ex) {
            log.error("Something wrong with request to endpoint", ex);
        }

        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Point getPoint(ATMDetails atm) {
        Point point = new Point();
        point.setLocation(Double.parseDouble(atm.getCoordinates().getLatitude()), Double.parseDouble(atm.getCoordinates().getLongitude()));
        return point;
    }

    private HttpEntity<String> getAtmsApiHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("x-ibm-client-id", env.getProperty("server.ssl.client-id"));
        return new HttpEntity<>("body", headers);
    }

    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}
