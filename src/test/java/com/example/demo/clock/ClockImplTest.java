package com.example.demo.clock;

import io.jsonwebtoken.Clock;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;


public class ClockImplTest implements Clock {

    @Override
    public Date now() {
        return new Date();
    }
}
