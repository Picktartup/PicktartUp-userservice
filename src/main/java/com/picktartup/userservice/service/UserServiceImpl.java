package com.picktartup.userservice.service;

import com.picktartup.userservice.common.CommonResult;
import com.picktartup.userservice.config.jwt.JwtTokenProvider;
import com.picktartup.userservice.dto.request.UserLoginRequest;
import com.picktartup.userservice.dto.request.UserRequestDto;
import com.picktartup.userservice.dto.response.JWTAuthResponse;
import com.picktartup.userservice.entity.Role;
import com.picktartup.userservice.entity.UserEntity;
import com.picktartup.userservice.exception.BusinessLogicException;
import com.picktartup.userservice.exception.ExceptionList;
import com.picktartup.userservice.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final ResponseService responseService;
    private final BCryptPasswordEncoder pwdEncoder;

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final MyUserDetailsService myUserDetailsService;
    private final RedisServiceImpl redisServiceImpl;


    @Override
    public CommonResult register(UserRequestDto userRequestDto) {

        /* 중복 아이디 체크 */
        if(userRepository.existsByEmail(userRequestDto.getEmail())){
            return responseService.getFailResult(ExceptionList.EMAIL_ALREADY_EXISTS.getCode(), ExceptionList.EMAIL_ALREADY_EXISTS.getMessage());
        }

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = mapper.map(userRequestDto, UserEntity.class);
        userEntity.setEncryptedPwd(pwdEncoder.encode(userRequestDto.getPassword()));
        userEntity.setRole(Role.USER);
        userEntity.setCreatedAt(LocalDateTime.now());
        userRepository.save(userEntity);

        return responseService.getSuccessfulResultWithMessage("사용자 회원가입이 성공적으로 완료되었습니다!");
    }

    @Transactional(readOnly = true)
    @Override
    public JWTAuthResponse login(UserLoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        Long userId = myUserDetailsService.findUserIdByEmail(loginRequest.getEmail()); //1
        String name = myUserDetailsService.findNameByEmail(loginRequest.getEmail()); //fisa@gmail.com
        JWTAuthResponse token = jwtTokenProvider.generateToken(authentication, userId, name);
        return token;

    }

    @Override
    public void logout(HttpServletRequest request) {
        String accessToken = jwtTokenProvider.resolveToken(request);
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);

        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new BusinessLogicException(ExceptionList.TOKEN_INVALID);
        }
        //access token에서 인증정보 조회
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        String username = authentication.getName(); //사용자 이메일

        //아이디로 조회되는 refresh token 이 있으면 삭제
        if(redisServiceImpl.getValues(username)!=null){
            redisServiceImpl.deleteValues(username);
        }

        //Access Token 남은 유효시간 가지고 와서 blacklist에 추가
        Long expiration = jwtTokenProvider.getAccessTokenExpiration(accessToken);
        redisServiceImpl.setBlackList(accessToken, "access_token", expiration);

        // Refresh Token 남은 유효시간 가지고 와서 blacklist에 추가
        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            Long refreshTokenExpiration = jwtTokenProvider.getRefreshTokenExpiration(refreshToken);
            redisServiceImpl.setBlackList(refreshToken, "refresh_token", refreshTokenExpiration);
        }
    }
}
