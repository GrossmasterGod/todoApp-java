package com.example.demo.clock;

import com.example.demo.security.jwt.ClockImpl;
import io.jsonwebtoken.Clock;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class ClockTest {

    private final Clock clock = new ClockImpl();

    @Test
    public void clockInstance() {
        assertEquals(clock.getClass(), ClockImpl.class);
    }

    @Test
    public void clockDateNow() {
        assertEquals(new Date(), clock.now());
    }

}
