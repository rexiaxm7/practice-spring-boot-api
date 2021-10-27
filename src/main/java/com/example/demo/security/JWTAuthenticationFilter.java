package com.example.demo.security;


import com.example.demo.bean.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static com.example.demo.security.SecurityConstants.*;


public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    private AuthenticationManager authenticationManager;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.authenticationManager = authenticationManager;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        // ログイン用のpathを変更する
        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(LOGIN_URL, "POST"));

        // ログイン用のID/PWのパラメータ名を変更する
        setUsernameParameter(LOGIN_ID);
        setPasswordParameter(PASSWORD);
    }

    // 認証の処理
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            // requestパラメータからユーザ情報を読み取る
            UserForm userForm = new ObjectMapper().readValue(req.getInputStream(), UserForm.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userForm.getEmail(),
                            userForm.getPassword(),
                            new ArrayList<>())
            );
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // 認証に成功した場合の処理
    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        byte[] secretKeyBytes = SECRET.getBytes();

        SimpleLoginUser user = (SimpleLoginUser)auth.getPrincipal();
        System.out.println(user);
        // loginIdからtokenを設定してヘッダにセットする
        String token = Jwts.builder()
                .setSubject(user.getUsername()) // usernameだけを設定する
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(new SecretKeySpec(secretKeyBytes, "HmacSHA512"))
                .compact();


        // ログインに成功したユーザ情報を取得する
        ObjectMapper mapper = new ObjectMapper();
        String userString = mapper.writeValueAsString(user);

        res.setContentType("Application/json; charset=UTF-8");
        res.getWriter().write(new ObjectMapper().writeValueAsString(userString));
        res.setStatus(HttpStatus.OK.value()); //200 OK.
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
        res.addHeader("Access-Control-Expose-Headers", HEADER_STRING);

        // ここでレスポンスを組み立てると個別のパラメータを返せるがFilterの責務の範囲内で実施しなければならない
        // auth.getPrincipal()で取得できるUserDetailsは自分で作ったEntityクラスにもできるのでカスタム属性は追加可能
    }

}