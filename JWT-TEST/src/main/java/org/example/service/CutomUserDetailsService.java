package org.example.service;

//////////////////////////////////////////////
// UserDetailsSerivce 가 2개 이상 이면
// authenticationManagerBuilder.getObject().authenticate() 에서 null 이 반환된다.
//////////////////////////////////////////////


import lombok.RequiredArgsConstructor;
import org.example.domain.User;
import org.example.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Component("userDetailsService")
@RequiredArgsConstructor
public class CutomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByEmail(username)
                .map(user -> this.createSpringSecurityUser(username, user))
                .orElseThrow(() -> new UsernameNotFoundException(username + " 데이터베이스에서 찾을 수 없음"));
    }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(String email, User user) {
        /* 권한 정보가 있을경우 가져온다.
        List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toList());
        */
        return new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(),
                Collections.emptyList());
                //grantedAuthorities);
    }
}
