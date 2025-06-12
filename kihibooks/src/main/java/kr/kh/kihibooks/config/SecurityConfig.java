package kr.kh.kihibooks.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import kr.kh.kihibooks.model.vo.UserRole;
import kr.kh.kihibooks.service.MemberDetailService;
import kr.kh.kihibooks.utils.CustomLoginFailureHandler;
import kr.kh.kihibooks.utils.CustomUser;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
	MemberDetailService memberDetailService;

    @Autowired
    private CustomLoginFailureHandler customLoginFailureHandler;
	
	@Value("${spring.remember.me.key}")
	String rememberMeKey;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf ->csrf.disable())
        
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers("edit/checkPw")
                .authenticated()
                .requestMatchers("/account/mykihi")
                .hasRole(UserRole.USER.name())
                .requestMatchers("/admin/**")
                .hasRole(UserRole.ADMIN.name())
                .anyRequest()
                .permitAll()  // 그 외 요청은 인증 필요
            )
            .formLogin((form) -> form
                .loginPage("/login")
                .failureHandler(customLoginFailureHandler)
                .permitAll()
                .loginProcessingUrl("/login")
                // .failureUrl("/login?error")
                .defaultSuccessUrl("/genre/romance")
            )
            //자동 로그인 처리
			.rememberMe(rm-> rm
				.userDetailsService(memberDetailService)//자동 로그인할 때 사용할 userDetailService를 추가
				.key(rememberMeKey)//키가 변경되면 기존 토큰이 무효처리
				.rememberMeCookieName("LC")//쿠키 이름
				.tokenValiditySeconds(60 * 60 * 24 * 7)//유지 기간 : 7일
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
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider() {
            @Override
            protected void additionalAuthenticationChecks(UserDetails userDetails,
                    UsernamePasswordAuthenticationToken authentication)
                    throws AuthenticationException {
                super.additionalAuthenticationChecks(userDetails, authentication);

                CustomUser customUser = (CustomUser) userDetails;
                if ("Y".equals(customUser.getUser().getUr_del())) {
                    throw new DisabledException("탈퇴한 사용자입니다.");
                }
            }
        };
        provider.setUserDetailsService(memberDetailService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
	public PasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
	}
    
}
