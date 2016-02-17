package com.cicdaas.nasasoundapiautomation.test;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.cicdaas.nasasoundapiautomation.client.NASASoundAPIRESTClient;
import com.cicdaas.nasasoundapiautomation.dto.NASAGETSoundTrackResponse;
import com.cicdaas.nasasoundapiautomation.dto.NASAGETSoundTrackResponseHolder;
import com.cicdaas.nasasoundapiautomation.dto.SoundTrack;

public class NASASoundAPITest extends Assert {

    protected ApplicationContext applicationContext;
    protected NASASoundAPIRESTClient client;

    protected String defaultAPIClientErrorMsg = "Unable to get sounds from NASA Sound API!";

    @BeforeClass
    protected void setup() {
        applicationContext = new ClassPathXmlApplicationContext( "applicationContext.xml" );
        client = applicationContext.getBean("nasaSoundAPIRESTClient", NASASoundAPIRESTClient.class);
    }

    @Test(groups = {"nasa-sound-api-regression", "nasa-sound-api-sanity"})
    public void testNASASoundAPIGETCallWithValidKey() {
        try {
            NASAGETSoundTrackResponseHolder responseHolder = client.getSoundTrack();
            NASAGETSoundTrackResponse response = responseHolder.getResponse();
            assertEquals(response.getCount(), 10, "Sounds count didn't match!");
            verifySoundTrackResponse(responseHolder);
        } catch (Exception e) {
            fail(defaultAPIClientErrorMsg, e);
        }
    }

    // This test is fails intermittently!!! Verified manually too.. The rate remaining is not consistent.
    @Test(groups = {"nasa-sound-api-regression"})
    public void testNASASoundAPIGETCallRateLimitRemainingHeader() {
        try {
            NASAGETSoundTrackResponseHolder responseHolder = client.getSoundTrack();
            NASAGETSoundTrackResponse response = responseHolder.getResponse();
            assertEquals(response.getCount(), 10, "Sounds count didn't match!");
            int initialRateLimitRemaining = getRateLimitRemaining(responseHolder);
            sleep(250);
            responseHolder = client.getSoundTrack();
            int recentRateLimitRemaining = getRateLimitRemaining(responseHolder);
            assertEquals(recentRateLimitRemaining, initialRateLimitRemaining - 1, "Rate limit remaining header value didn't "
                    + "match!");
        } catch (Exception e) {
            fail(defaultAPIClientErrorMsg, e);
        }
    }

    @Test(groups = {"nasa-sound-api-regression"})
    public void testNASASoundAPIGETCallWithLimit5() {
        try {
            int limit = 5;
            NASAGETSoundTrackResponseHolder responseHolder = client.getSoundTrack(limit);
            NASAGETSoundTrackResponse response = responseHolder.getResponse();
            assertEquals(response.getCount(), limit, "Sounds count didn't match!");
            verifySoundTrackResponse(responseHolder);
        } catch (Exception e) {
            fail(defaultAPIClientErrorMsg, e);
        }
    }

    @Test(groups = {"nasa-sound-api-regression"})
    public void testNASASoundAPIGETCallWithLimitGreaterThanDefault25() {
        try {
            int limit = 25;
            NASAGETSoundTrackResponseHolder responseHolder = client.getSoundTrack(limit);
            NASAGETSoundTrackResponse response = responseHolder.getResponse();
            assertEquals(response.getCount(), limit, "Sounds count didn't match!");
            verifySoundTrackResponse(responseHolder);
        } catch (Exception e) {
            fail(defaultAPIClientErrorMsg, e);
        }
    }

    @Test(groups = {"nasa-sound-api-regression"})
    public void testNASASoundAPIGETCallWithSearchKeywordTsunami() {
        try {
            String keyword = "Tsunami";
            int limit = 10;
            NASAGETSoundTrackResponseHolder responseHolder = client.getSoundTrack(keyword);
            NASAGETSoundTrackResponse response = responseHolder.getResponse();
            assertEquals(response.getCount(), limit, "Sounds count didn't match!");
            verifySoundTrackResponse(responseHolder);
        } catch (Exception e) {
            fail(defaultAPIClientErrorMsg, e);
        }
    }

    @Test(groups = {"nasa-sound-api-regression"})
    public void testNASASoundAPIGETCallWithLimit5AndSearchKeywordTsunami() {
        try {
            String keyword = "Tsunami";
            int limit = 5;
            NASAGETSoundTrackResponseHolder responseHolder = client.getSoundTrack(keyword, limit);
            NASAGETSoundTrackResponse response = responseHolder.getResponse();
            assertEquals(response.getCount(), limit, "Sounds count didn't match!");
            verifySoundTrackResponse(responseHolder);
        } catch (Exception e) {
            fail(defaultAPIClientErrorMsg, e);
        }
    }

    @Test(groups = {"nasa-sound-api-regression", "nasa-sound-api-sanity"})
    public void testNegNASASoundAPIGETCallwithoutKey() {
        try {
            client.getSoundTrackWithoutAPIKey();
            fail("Sound API returned valid response w/o API Key!");
        } catch (HttpClientErrorException hcee) {
            assertEquals(HttpStatus.FORBIDDEN, hcee.getStatusCode(), "HTTP Status code didn't match!");
        } catch (Exception e) {
            fail(defaultAPIClientErrorMsg, e);
        }
    }

    @Test(groups = {"nasa-sound-api-regression", "real-svc-only", "nasa-sound-api-sanity"})
    public void testNegNASASoundAPIGETCallwithInvalidKey() {
        try {
            String key = "123";
            client.getSoundTrackWithSpecificAPIKey(key);
            fail("Sound API returned valid response for invalid API Key!");
        } catch (HttpClientErrorException hcee) {
            assertEquals(HttpStatus.FORBIDDEN, hcee.getStatusCode(), "HTTP Status code didn't match!");
        } catch (Exception e) {
            fail(defaultAPIClientErrorMsg, e);
        }
    }

    @Test(groups = {"nasa-sound-api-regression"})
    public void testNegNASASoundAPIGETCallWithInvalidLimit0() {
        try {
            int limit = 0;
            NASAGETSoundTrackResponseHolder responseHolder = client.getSoundTrack(limit);
            NASAGETSoundTrackResponse response = responseHolder.getResponse();
            assertEquals(response.getCount(), limit, "Sounds count didn't match!");
        } catch (Exception e) {
            fail(defaultAPIClientErrorMsg, e);
        }
    }

    @Test(groups = {"nasa-sound-api-regression"})
    public void testNegNASASoundAPIGETCallWithInvalidLimitMinus1() {
        try {
            int defaultLimit = 10;
            int limit = -1;
            NASAGETSoundTrackResponseHolder responseHolder = client.getSoundTrack(limit);
            NASAGETSoundTrackResponse response = responseHolder.getResponse();
            assertEquals(response.getCount(), defaultLimit, "Sounds count didn't match! Limit '-1' didn't return default limit!");
        } catch (Exception e) {
            fail(defaultAPIClientErrorMsg, e);
        }
    }

    @Test(groups = {"nasa-sound-api-regression"})
    public void testNegNASASoundAPIGETCallWithInvalidQueryKeyword() {
        try {
            String keyword = "!(!)";
            int limit = 10;
            NASAGETSoundTrackResponseHolder responseHolder = client.getSoundTrack(keyword);
            NASAGETSoundTrackResponse response = responseHolder.getResponse();
            assertEquals(response.getCount(), limit, "Sounds count didn't match when searching for invalid keyword!");
        } catch (Exception e) {
            fail(defaultAPIClientErrorMsg, e);
        }
    }

    @Test(groups = {"nasa-sound-api-regression", "real-svc-only", "nasa-sound-api-sanity"})
    public void testNegNASASoundAPIGETCallWithInvalidHTTPProtocol() {
        try {
            client.getSoundTrackUsingHTTPProtocol();
            fail("Sound API returned valid response for non-secure protocol access (HTTP) - Bad Request succeeded!");
        } catch (HttpClientErrorException hcee) {
            assertEquals(HttpStatus.BAD_REQUEST, hcee.getStatusCode(), "HTTP Status code didn't match!");
        } catch (Exception e) {
            fail(defaultAPIClientErrorMsg, e);
        }
    }

    private void verifySoundTrackResponse(NASAGETSoundTrackResponseHolder responseHolder) {
        SoftAssert softAssert = new SoftAssert();
        // headers
        String rateLimitHeaderName = "x-ratelimit-limit";
        List<String> rateLimit = responseHolder.getHeaders().get(rateLimitHeaderName);
        softAssert.assertEquals(rateLimit.size(), 1, "Header: " + rateLimitHeaderName + " has more than one value!");
        softAssert.assertEquals(rateLimit.get(0), "1000", "Header: " + rateLimitHeaderName + " didn't match!");

        // response body
        List<SoundTrack> soundTracks = responseHolder.getResponse().getResults();
        int index = 0;
        for (SoundTrack soundTrack : soundTracks) {
            softAssert.assertNotNull(soundTrack.getDownloadUrl(), "Download URL found null @ SoundTrack Index: " + index);
            softAssert.assertNotNull(soundTrack.getDuration(), "Duration found null @ SoundTrack Index: " + index);
            softAssert.assertNotNull(soundTrack.getLastModified(), "Last Modified found null @ SoundTrack Index: " + index);
            softAssert.assertNotNull(soundTrack.getLicense(), "License found null @ SoundTrack Index: " + index);
            softAssert.assertNotNull(soundTrack.getStreamURL(), "Stream URL found null @ SoundTrack Index: " + index);
            softAssert.assertNotNull(soundTrack.getTagList(), "Tag List found null @ SoundTrack Index: " + index);
            softAssert.assertNotNull(soundTrack.getTitle(), "Title found null @ SoundTrack Index: " + index);
            index++;
        }
        softAssert.assertAll();
    }

    private int getRateLimitRemaining(NASAGETSoundTrackResponseHolder responseHolder) {
        try {
            String rateLimitRemainingHeaderName = "x-ratelimit-remaining";
            List<String> rateLimitRemaining = responseHolder.getHeaders().get(rateLimitRemainingHeaderName);
            return Integer.parseInt(rateLimitRemaining.get(0));
        } catch (Exception e) {
            throw new RuntimeException("Unable to extrac/parse rate-limit remaining value!", e);
        }
    }

    private void sleep(int delay) {
        try {
            Thread.sleep(delay);
        } catch (Exception e) {
            // ignore
        }
    }

}
