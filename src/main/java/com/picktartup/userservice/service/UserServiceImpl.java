package com.picktartup.userservice.service;

import com.picktartup.userservice.common.CommonResult;
import com.picktartup.userservice.common.SingleResult;
import com.picktartup.userservice.config.jwt.JwtTokenProvider;
import com.picktartup.userservice.dto.UserDto;
import com.picktartup.userservice.dto.WalletDto;
import com.picktartup.userservice.entity.Role;
import com.picktartup.userservice.entity.UserEntity;
import com.picktartup.userservice.exception.BusinessLogicException;
import com.picktartup.userservice.exception.ExceptionList;
import com.picktartup.userservice.repository.UserRepository;
import com.picktartup.userservice.webclient.WalletServiceClient;
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
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final WalletServiceClient walletServiceClient;
    private final ResponseService responseService;
    private final BCryptPasswordEncoder pwdEncoder;

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final MyUserDetailsService myUserDetailsService;
    private final RedisServiceImpl redisServiceImpl;

    @Override
    public SingleResult<Long> register(UserDto.SignUpRequest signUpRequest) {

        /* 중복 이메일 체크 */
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BusinessLogicException(ExceptionList.EMAIL_ALREADY_EXISTS);
        }

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserEntity userEntity = modelMapper.map(signUpRequest, UserEntity.class);
        userEntity.setEncryptedPwd(pwdEncoder.encode(signUpRequest.getPassword()));
        userEntity.setRole(Role.USER);
        userEntity.setCreatedAt(LocalDateTime.now());
        UserEntity savedUser = userRepository.save(userEntity);

        return responseService.getSingleResultwithMessage(savedUser.getUserId(),
                "사용자 회원가입이 성공적으로 완료되었습니다!");
    }


    @Transactional(readOnly = true)
    @Override
    public UserDto.AuthResponse login(UserDto.SignInRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        Long userId = myUserDetailsService.findUserIdByEmail(loginRequest.getEmail()); //1
        String name = myUserDetailsService.findNameByEmail(loginRequest.getEmail()); //fisa@gmail.com
        UserDto.AuthResponse token = jwtTokenProvider.generateToken(authentication, userId, name);
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

    @Override
    public UserDto.UserInfoResponse getUserById(Long userId) {
        ModelMapper modelMapper = new ModelMapper();
        // 1. User 정보 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionList.USER_NOT_FOUND));

        // 2. Wallet 정보 조회
        WalletDto.Details wallet = walletServiceClient.getWalletByUserId(userId);

        // 3. Response 생성
        UserDto.UserInfoResponse userResponse = modelMapper.map(user, UserDto.UserInfoResponse.class);

        // 4. Wallet 정보 설정
        if (wallet != null && wallet.getAddress() != null) {
            userResponse.setWalletAddress(wallet.getAddress());
            userResponse.setWalletBalance(wallet.getBalance());
            userResponse.setWalletStatus(wallet.getStatus());
        }

        return userResponse;
    }

    @Override
    public UserDto.UserValidationResponse validateUser(Long userId) {
        // 1. User 정보 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionList.USER_NOT_FOUND));

        // 2. UserValidationResponse 생성 및 반환
        return UserDto.UserValidationResponse.builder()
                .id(user.getUserId())
                .email(user.getEmail())
                .username(user.getUsername())
                .status(user.getIsActivated() ? "ACTIVE" : "INACTIVE")  // Boolean 값을 상태 문자열로 변환
                .build();
    }

    @Override
    public UserDto.AuthResponse reissueAccessToken(HttpServletRequest request) {
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);
        this.verifiedRefreshToken(refreshToken);

        String email = jwtTokenProvider.getEmail(refreshToken);
        String redisRefreshToken = redisServiceImpl.getValues(email);

        if (redisServiceImpl.checkExistsValue(redisRefreshToken) && refreshToken.equals(redisRefreshToken)) {
            Optional<UserEntity> findUser = userRepository.findByEmail(email);
            UserEntity userEntity = UserEntity.of(findUser);
            UserDto.AuthResponse tokenDto = jwtTokenProvider.generateToken(jwtTokenProvider.getAuthentication(refreshToken), userEntity.getUserId(), userEntity.getUsername());
            return tokenDto;
        } else throw new BusinessLogicException(ExceptionList.TOKEN_IS_NOT_SAME);

    }

    private void verifiedRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            throw new BusinessLogicException(ExceptionList.HEADER_REFRESH_TOKEN_NOT_EXISTS);
        }
    }


}
