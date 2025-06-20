package com.oauth2.app.oauth2_authorization_server.application.port.out;

import java.time.Duration;
import java.util.Map;

public interface IVerificationTokenService {
    String encryptData(String keyName, Map<String, Object> data, Duration validity);
    Map<String, Object> decryptAllData(String token);
}
