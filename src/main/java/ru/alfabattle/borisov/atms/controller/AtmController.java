package ru.alfabattle.borisov.atms.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ru.alfabattle.borisov.atms.model.JSONResponseBankATMDetails;

import java.net.URI;
import java.util.Collections;

@RestController
@RequestMapping(value = "/alfa")
@Slf4j
public class AtmController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    Environment env;

    @RequestMapping(value = "/atm-service/atms", method = RequestMethod.GET)
    public String getData() {
        log.info("GET atms data invoked");
        try {
            String endpoint = env.getProperty("endpoint.alfa-api");
            log.info("endpoint name: {}", endpoint);

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.set("x-ibm-client-id", env.getProperty("server.ssl.client-id"));
            HttpEntity<String> entity = new HttpEntity<>("body", headers);

            final ResponseEntity<JSONResponseBankATMDetails> atmDetails = restTemplate.exchange(
                    new URI(endpoint) + "/atm-service/atms", HttpMethod.GET, entity, JSONResponseBankATMDetails.class);

            return atmDetails.getBody().toString();
        } catch (Exception ex) {
            log.error("Something wrong with request to endpoint", ex);
        }
        return "Exception occurred...";
    }


}
