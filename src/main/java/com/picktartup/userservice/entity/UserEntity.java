package com.picktartup.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Entity
@Table(name ="users")
public class UserEntity {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq_generator")
    @SequenceGenerator(name = "users_seq_generator", sequenceName = "users_seq", allocationSize = 1)
    private Long userId;


    @Column(name = "name", nullable = false, length = 50)
    private String username;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "encrypted_pwd", nullable = false, length = 100)
    private String encryptedPwd;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "is_activated", nullable = false)
    private Boolean isActivated = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Wallet 서비스 ID 참조 : 간접참조
    @Column(name = "wallet_id")
    private Long walletId;
}