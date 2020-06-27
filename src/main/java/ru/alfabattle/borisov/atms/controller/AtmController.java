package ru.alfabattle.borisov.atms.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import ru.alfabattle.borisov.atms.model.JSONResponseBankATMDetails;
import ru.alfabattle.borisov.atms.model.JSONResponseBankATMStatus;

import java.net.URI;
import java.util.Collections;

@RestController
@RequestMapping(value = "/alfa")
@Slf4j
public class AtmController {

    // Endpoints:
    public static final String ATM_SERVICE_ATMS_STATUS = "/atm-service/atms/status";
    public static final String ATM_SERVICE_ATMS = "/atm-service/atms";

    private final RestTemplate restTemplate;
    private final Environment env;
    private final WebSocketStompClient stompClient;

    @Autowired
    public AtmController(RestTemplate restTemplate, Environment env, WebSocketStompClient webSocketStompClient) {
        this.restTemplate = restTemplate;
        this.env = env;
        this.stompClient = webSocketStompClient;
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

    private HttpEntity<String> getAtmsApiHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("x-ibm-client-id", env.getProperty("server.ssl.client-id"));
        return new HttpEntity<>("body", headers);
    }

}
