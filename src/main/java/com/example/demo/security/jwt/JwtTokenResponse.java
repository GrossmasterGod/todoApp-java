package com.example.demo.security.jwt;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class JwtTokenResponse implements Serializable {

    private static final long serialVersionUID = 8317676219297719109L;

    private String token;
}
