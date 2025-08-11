package vn.hoidanit.jobhunter.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.LoginDTO;
import vn.hoidanit.jobhunter.domain.dto.ResLoginDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.service.util.SecurityUtil;
import vn.hoidanit.jobhunter.service.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.service.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    @Value("${hoidanit.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder,
                          SecurityUtil securityUtil,
                          UserService userService
    ) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
         this.securityUtil =  securityUtil;
         this.userService = userService;
    }

    @PostMapping("/auth/login")
    @ApiMessage("Login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(),
                loginDTO.getPassword()
        );

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        //create a token
        ResLoginDTO resLoginDTO = new ResLoginDTO();
        User currentUserDb = this.userService.handleFindUserByUsername(loginDTO.getUsername());

        if(currentUserDb != null){
            resLoginDTO.setUser(ResLoginDTO.UserLogin.builder()
                    .id(currentUserDb.getId())
                    .email(currentUserDb.getEmail())
                    .name(currentUserDb.getName())
                    .build()
            );
        }

        String accessToken = this.securityUtil.createAccessToken(authentication.getName(), resLoginDTO.getUser());


        resLoginDTO.setAccessToken(accessToken);


        //create refresh token
        String refreshToken = this.securityUtil.createRefreshToken(loginDTO.getUsername(), resLoginDTO);

        //update user
        this.userService.updateUserToken(refreshToken, loginDTO.getUsername());

        //set cookies
        ResponseCookie resCookies = ResponseCookie.from("refresh_token", refreshToken)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(resLoginDTO);
    }

    @GetMapping("/auth/account")
    @ApiMessage("Get account login")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccountLogin(){

        String email = SecurityUtil.getCurrentUserLogin().isPresent() ?
                SecurityUtil.getCurrentUserLogin().get() : "";
        User currentUserDb = this.userService.handleFindUserByUsername(email);

        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();

        if (currentUserDb != null){
            userLogin.setId(currentUserDb.getId());
            userLogin.setEmail(currentUserDb.getEmail());
            userLogin.setName(currentUserDb.getName());
            userGetAccount.setUser(userLogin);
        }

        return ResponseEntity.ok().body(userGetAccount);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Get user by refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "abc") String refreshToken
    ) throws IdInvalidException {

        if ("abc".equals(refreshToken)){
            throw new IdInvalidException("refresh token is invalid");
        }

        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refreshToken);
        String email = decodedToken.getSubject();

        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refreshToken, email);
        if(currentUser == null){
            throw new IdInvalidException("Refresh token invalid");
        }



        ResLoginDTO resLoginDTO = new ResLoginDTO();
        User currentUserDb = this.userService.handleFindUserByUsername(email);

        if(currentUserDb != null){
            resLoginDTO.setUser(ResLoginDTO.UserLogin.builder()
                    .id(currentUserDb.getId())
                    .email(currentUserDb.getEmail())
                    .name(currentUserDb.getName())
                    .build()
            );
        }

        String accessToken = this.securityUtil.createAccessToken(email, resLoginDTO.getUser());


        resLoginDTO.setAccessToken(accessToken);


        //create refresh token
        String newRefreshToken = this.securityUtil.createRefreshToken(email, resLoginDTO);

        //update user
        this.userService.updateUserToken(newRefreshToken, email);

        //set cookies
        ResponseCookie resCookies = ResponseCookie.from("refresh_token", refreshToken)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(resLoginDTO);
    }

    @PostMapping("/auth/logout")
    @ApiMessage("Log out")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ?
                SecurityUtil.getCurrentUserLogin().get() : "";

        if ("".equals(email)){
            throw new IdInvalidException("Access token is invalid");
        }

        this.userService.updateUserToken(null, email);

        ResponseCookie resCookies = ResponseCookie.from("refresh_token", null)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(null);
    }

}
