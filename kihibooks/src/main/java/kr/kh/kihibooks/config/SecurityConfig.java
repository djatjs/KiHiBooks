package kr.kh.kihibooks.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import kr.kh.kihibooks.model.vo.UserRole;
import kr.kh.kihibooks.service.MemberDetailService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf ->csrf.disable())
        
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers("account/mykihi")
                .authenticated()
                .requestMatchers("/admin/**")
                .hasRole(UserRole.ADMIN.name())
                .anyRequest()
                .permitAll()  // 그 외 요청은 인증 필요
            )
            .formLogin((form) -> form
                .loginPage("/login")  // 커스텀 로그인 페이지 설정
                .permitAll()           // 로그인 페이지는 접근 허용
                .loginProcessingUrl("/login")
                .failureUrl("/login")
                .defaultSuccessUrl("/")
            )
            .logout((logout) -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .permitAll())
            ;
        return http.build();
    }

    @Bean
	public PasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
	}

    // @Bean
    // public UserDetailsService userDetailsService() {
    //     return new MemberDetailService();
    // }
    
}
