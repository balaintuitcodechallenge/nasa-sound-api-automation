
# Introduction

This repo contains automated tests implemented in JAVA Programming language to verify NASA Sound API.

- Test Framework: TestNG
- REST Client: Spring REST Template
- Data Format: JSON
- API Web Service: REST
- Build System: Maven


# Requirements

NASA Developer API Key

New Developer Registration: [https://api.nasa.gov/index.html#apply-for-an-api-key]

# Getting Started

Install your preferred REST Client for your favorite browser.

Basic Request URL: [https://api.nasa.gov/planetary/sounds?api_key=<<YOUR_NASA_API_KEY>>&q=tsunami&limit=1]

Response:
````
{"count": 1, "results": [{"description": "The Voyager 1 spacecraft has experienced three \"tsunami waves\" in interstellar space. Listen to how these waves cause surrounding ionized matter to ring. More details on this sound can be found here: http://www.nasa.gov/jpl/nasa-voyager-tsunami-wave-still-flies-through-interstellar-space/", "license": "cc-by-nc-sa", "title": "Voyager 1: Three \"Tsunami Waves\" in Interstellar Space", "download_url": "https://api.soundcloud.com/tracks/181835738/download", "duration": 18365, "last_modified": "2014/12/16 22:34:23 +0000", "stream_url": "https://api.soundcloud.com/tracks/181835738/stream", "tag_list": "Space", "id": 181835738}]}
````

**Note:** To access soundcloud download & stream URL's, we need *client_id*/ *authentication* from SoundCloud. For sake of simplicity, these two URL's are ignored during validation. 

# Running/Executing Automated Tests

- Clone this repo
- Update 'nasa_sound_api_automation.properties' file under directory 'src/test/resources' with your NASA API Key
- mvn clean test

# Test Cases

Common Verification - HTTP Response Headers

- HTTP Status Code: 200 (OK)
- Content-Type: application/json
- X-Cache: MISS
- x-ratelimit-limit: 1000
- x-ratelimit-remaining: Number less than 1000


## A) Test: NASASoundAPIGETCallWithValidKey

### Description: 

A very simple API call with valid Key. A happy path case & sanity test. 

### Steps: 

Open Web Browser - REST Client

Generate Request:

- Choose HTTP Operation: GET
- Enter URL: https://api.nasa.gov/planetary/sounds
- Enter Query Parameter: name=api_key , value=<<YOUR_NASA_API_KEY>>
- Send the request

Validate Response:

a) Common HTTP Response Headers
b) Response body: 

- Valid JSON 
- Field: count = 10  
- Field: results = array of soundcloud objects (count of items) 
- Each soundcloud object should contain mandatory fields (id, description, duration, download url, stream url etc) 

## B) Test: NASASoundAPIGETCallWithLimit5

### Description:

This test pass 'limit' parameter to control the number of soundcloud object to be returned in the response.

### Steps:

Generate Request:

To the above test (A), pass additional query param named 'limit'

c) Enter Query Parameter: name=limit , value=5

Validate Response:

b) Response body:

- Field: count = 5
- Field: results = array of 5 soundcloud objects

## C) Test: NASASoundAPIGETCallWithSearchKeywordTsunami

### Description:

This test pass 'q' parameter with value 'Tsunami' to retrieve soundcloud objects related to 'Tsunami'

### Steps & Validation: Follow test (A)

## D) Test: NASASoundAPIGETCallWithLimit5AndSearchKeywordTsunami

### Description:

This test combines (B) & (C)

## E) Test: NegNASASoundAPIGETCallwithoutKey

### Description:

This negative test sends request without query param 'api_key'

### Validate Response:

HTTP Headers

- HTTP Status Code: 403 Forbidden
- Content-Type: text/html

HTTP Body

- Assert for text exist: API_KEY_MISSING 
- Assert for text exist: No api_key was supplied 

## F) Test: NegNASASoundAPIGETCallwithInvalidKey

### Description:

This negative test sends invalid API key 

### Steps:

Generate request:

c. Enter Query Parameter: name=api_key , value=123


Validate Response:

HTTP Headers

- HTTP Status Code: 403 Forbidden
- Content-Type: text/html

HTTP Body

- Assert for text exist: API_KEY_INVALID
- Assert for text exist: An invalid api_key was supplied 

## G) Test: NegNASASoundAPIGETCallWithInvalidLimit0

### Description:

This negative test sends invalid 'limit' value '0'

Steps:

Generate Request:

c) Enter Query Parameter: name=limit , value=0

Validate Response:

b) Response body:

- Field: count = 0
- Field: results = empty array


## H) Test: NegNASASoundAPIGETCallWithInvalidLimitMinus1

### Description:

This negative test sends invalid 'limit' value '-1'

### Steps:

Generate Request:

c) Enter Query Parameter: name=limit , value=-1

Validate Response:

b) Response body: The system is expected to ignore the invalid 'limit' value.

- Field: count = 10
- Field: results = array with count of soundcloud objects

## I) Test: NegNASASoundAPIGETCallWithInvalidQueryKeyword

### Description:

This negative test sends invalid characters as query keyword

### Steps:

Generate Request:

c) Enter Query Parameter: name=limit , value=!(!)

Validate Response:

b) Response body: The system is expected to ignore the invalid 'q' value and return default results.

- Field: count = 10
- Field: results = array with count of soundcloud objects

## Test: NegNASASoundAPIGETCallWithInvalidHTTPProtocol

### Description:

This negative test sends request via normal HTTP protocol (instead of secure HTTP protocol)

### Steps:

Generate Request:

b. Enter URL: http://api.nasa.gov/planetary/sounds

Validate Response:

b) Response body: 

HTTP Headers

- HTTP Status Code: 400 Bad Request
- Content-Type: text/html

HTTP Body

- Assert for text exist: HTTPS_REQUIRED
- Assert for text exist: Requests must be made over HTTPS

## J) NASASoundAPIGETCallWithLimitGreaterThanDefault25

### Description:

This test sends valid 'limit' value '25'. For default query, count limits to specified value.

### Steps:

Generate Request:

c) Enter Query Parameter: name=limit , value=25

Validate Response:

b) Response body:

- Field: count = 25
- Field: results = array of 25 soundcloud objects
