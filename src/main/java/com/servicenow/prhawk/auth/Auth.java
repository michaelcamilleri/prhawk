package com.servicenow.prhawk.auth;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

@Component
public class Auth {

    private final String token;

    @Autowired
    public Auth(@Value("${username}") String username,
                @Value("${password}") char[] password) {
        char[] userPass = ArrayUtils.addAll((username+":").toCharArray(), password);
        ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(CharBuffer.wrap(userPass));
        token = Base64.getEncoder().encodeToString(Arrays.copyOf(byteBuffer.array(), byteBuffer.limit()));
        Arrays.fill(password, ' ');
        Arrays.fill(userPass, ' ');
    }

    public String getToken() {
        return token;
    }
}
