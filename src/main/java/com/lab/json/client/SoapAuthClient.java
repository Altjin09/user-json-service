package com.lab.json.client;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

@Component
public class SoapAuthClient {

    private static final String SOAP_URL = "http://localhost:8081/ws";

    public ValidateResult validateToken(String token) {
        try {
            String soapRequest =
                    "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:lab=\"http://lab.com/soap\">" +
                    "<soapenv:Header/>" +
                    "<soapenv:Body>" +
                    "<lab:validateTokenRequest>" +
                    "<lab:token>" + token + "</lab:token>" +
                    "</lab:validateTokenRequest>" +
                    "</soapenv:Body>" +
                    "</soapenv:Envelope>";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_XML);

            HttpEntity<String> requestEntity = new HttpEntity<>(soapRequest, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity =
                    restTemplate.postForEntity(SOAP_URL, requestEntity, String.class);

            String response = responseEntity.getBody();

            System.out.println("SOAP validate response:");
            System.out.println(response);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);

            Document doc = factory.newDocumentBuilder()
                    .parse(new InputSource(new StringReader(response)));

            String validText = doc.getElementsByTagNameNS("*", "valid").item(0).getTextContent();
            String userIdText = doc.getElementsByTagNameNS("*", "userId").item(0).getTextContent();
            String usernameText = doc.getElementsByTagNameNS("*", "username").item(0).getTextContent();

            ValidateResult result = new ValidateResult();
            result.setValid(Boolean.parseBoolean(validText));
            result.setUserId(Long.parseLong(userIdText));
            result.setUsername(usernameText);

            return result;

        } catch (Exception e) {
            e.printStackTrace();

            ValidateResult result = new ValidateResult();
            result.setValid(false);
            result.setUserId(0L);
            result.setUsername("");
            return result;
        }
    }
}