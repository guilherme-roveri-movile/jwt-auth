package com.gbr.gateways.jwt;

import com.gbr.domains.User;
import com.gbr.gateways.exceptions.InvalidTokenException;
import com.gbr.gateways.spring.UserPrincipal;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;

public class JwtTokenProviderTests {

    private User validUser;

    @Before
    public void setup() {
        validUser = new User();
        validUser.setAuthorities(Arrays.asList("ROLE_ADMIN", "ROLE_HOLDING"));
        validUser.setUid("uuid1");
        validUser.setUsername("+5519993031289");
    }

    @Test
    public void test_generate_and_convert_jwt_token() {
        final JwtTokenProvider tokenProvider = new JwtTokenProvider("testSecret");
        final String generatedToken = tokenProvider.generateToken(UserPrincipal.fromUser(validUser));
        final User generatedUser = tokenProvider.getUserFromToken(generatedToken);
        Assert.assertEquals(validUser.getUid(), generatedUser.getUid());
        Assert.assertEquals(validUser.getUsername(), generatedUser.getUsername());
    }

    @Test(expected = InvalidTokenException.class)
    public void test_fail_convertion_with_wrong_secret() {
        JwtTokenProvider tokenProvider = new JwtTokenProvider("dGVzdFNlY3JldA==");
        final String generatedToken = tokenProvider.generateToken(UserPrincipal.fromUser(validUser));
        tokenProvider = new JwtTokenProvider("testAnotherSecret");
        tokenProvider.getUserFromToken(generatedToken);
    }

    @Test
    public void test_get_expiration() {
        JwtTokenProvider tokenProvider = new JwtTokenProvider("dGVzdFNlY3JldA==");
        final String generatedToken = tokenProvider.generateToken(UserPrincipal.fromUser(validUser));
        final Date exp = tokenProvider.getExp(generatedToken);
        Assert.assertTrue(exp.toInstant().atZone(ZoneOffset.UTC).toLocalDate().isEqual(LocalDate.now().plusYears(1)));
    }

}
