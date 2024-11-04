package com.picktartup.userservice.service;

import com.picktartup.userservice.entity.UserEntity;
import com.picktartup.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserEntity> findOne = userRepository.findByEmail(email);
        UserEntity userEntity = findOne.orElseThrow(() -> new UsernameNotFoundException("없는 회원입니다"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(userEntity.getEmail())
                .password(userEntity.getEncryptedPwd())
                .build();
    }


    public Long findUserIdByEmail(String email) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            return userOptional.get().getUserId();
        }

        throw new UsernameNotFoundException("해당하는 사용자가 없습니다: " + email);
    }

    public String findNameByEmail(String email) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            return userOptional.get().getUsername(); //사용자 이름
        }

        throw new UsernameNotFoundException("해당하는 사용자가 없습니다: " + email);

    }
}
