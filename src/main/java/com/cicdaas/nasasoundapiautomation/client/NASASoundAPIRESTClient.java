package com.cicdaas.nasasoundapiautomation.client;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.cicdaas.nasasoundapiautomation.dto.NASAGETSoundTrackResponse;
import com.cicdaas.nasasoundapiautomation.dto.NASAGETSoundTrackResponseHolder;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.Logger;

public class NASASoundAPIRESTClient {

    private static final Logger LOG = (Logger) LoggerFactory.getLogger(NASASoundAPIRESTClient.class);

    @Value("#{nasaSoundAPIAutomationProperties['nasa.sound.api.protocol']}")
    protected String nasaSoundAPIProtocol;

    @Value("#{nasaSoundAPIAutomationProperties['nasa.sound.api.host']}")
    protected String nasaSoundAPIHost;

    @Value("#{nasaSoundAPIAutomationProperties['nasa.sound.api.port']}")
    protected String nasaSoundAPIPort;

    @Value("#{nasaSoundAPIAutomationProperties['nasa.sound.api.context.path']}")
    protected String nasaSoundAPIContextPath;

    @Value("#{nasaSoundAPIAutomationProperties['nasa.sound.api.key']}")
    protected String nasaAPIKey;

    protected String apiURL;

    @PostConstruct
    public void initialize() {
        apiURL = String.format("%s://%s:%s%s?api_key=%s", nasaSoundAPIProtocol, nasaSoundAPIHost, nasaSoundAPIPort, 
                nasaSoundAPIContextPath, nasaAPIKey);
    }

    public NASAGETSoundTrackResponseHolder getSoundTrack() throws Exception {
        return getSoundTrackResponse(apiURL);
    }

    public NASAGETSoundTrackResponseHolder getSoundTrack(String keyword) throws Exception {
        String updatedAPIURL = String.format("%s&q=%s", apiURL, keyword);
        return getSoundTrackResponse(updatedAPIURL);
    }

    public NASAGETSoundTrackResponseHolder getSoundTrack(int limit) throws Exception {
        String updatedAPIURL = String.format("%s&limit=%d", apiURL, limit);
        return getSoundTrackResponse(updatedAPIURL);
    }

    public NASAGETSoundTrackResponseHolder getSoundTrack(String keyword, int limit) throws Exception {
        String updatedAPIURL = String.format("%s&q=%s&limit=%d", apiURL, keyword, limit);
        return getSoundTrackResponse(updatedAPIURL);
    }

    public NASAGETSoundTrackResponseHolder getSoundTrackUsingHTTPProtocol() throws Exception  {
        String apiURL = String.format("http://%s%s?api_key=%s", nasaSoundAPIHost, nasaSoundAPIContextPath, 
                nasaAPIKey);
        return getSoundTrackResponse(apiURL);
    }

    public NASAGETSoundTrackResponseHolder getSoundTrackWithoutAPIKey() throws Exception  {
        String apiURL = String.format("%s://%s:%s%s", nasaSoundAPIProtocol, nasaSoundAPIHost, nasaSoundAPIPort, 
                nasaSoundAPIContextPath);
        return getSoundTrackResponse(apiURL);
    }

    public NASAGETSoundTrackResponseHolder getSoundTrackWithSpecificAPIKey(String key) throws Exception  {
        String apiURL = String.format("%s://%s:%s%s?api_key=%s", nasaSoundAPIProtocol, nasaSoundAPIHost, nasaSoundAPIPort, 
                nasaSoundAPIContextPath, key);
        return getSoundTrackResponse(apiURL);
    }

    private NASAGETSoundTrackResponseHolder getSoundTrackResponse(String url) throws Exception {
        LOG.debug("NASA Sound API URL: " + url);
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new StringHttpMessageConverter());
        restTemplate.setMessageConverters(messageConverters);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, new Object[]{});
        String resultString = response.getBody();
        HttpHeaders headers = response.getHeaders();
        ObjectMapper mapper = new ObjectMapper();
        NASAGETSoundTrackResponse soundTrackResponse = mapper.readValue(resultString, NASAGETSoundTrackResponse.class);
        NASAGETSoundTrackResponseHolder responseHolder = new NASAGETSoundTrackResponseHolder();
        responseHolder.setHeaders(headers);
        responseHolder.setResponse(soundTrackResponse);
        return responseHolder;
    }

}
