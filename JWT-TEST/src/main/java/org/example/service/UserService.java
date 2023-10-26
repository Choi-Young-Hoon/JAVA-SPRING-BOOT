package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.User;
import org.example.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public User findById(Long userId) {
        return this.userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public User findByEmail(String email) {
        return this.userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        return JwtTokenService.getCurrentUsername()
                .flatMap(userRepository::findByEmail)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }
}
