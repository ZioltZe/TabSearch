package eliott.backend.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig
{

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JWTFilter filter;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .cors((cors) -> cors
                        .configurationSource(request -> {
                                    var corsConf = new CorsConfiguration();
                                    corsConf.setAllowedOrigins(List.of("https://localhost:3000", "https://127.0.0.1:3000", "https://192.168.1.23:3000"));
                                    corsConf.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                                    corsConf.setAllowedHeaders(List.of("*"));
                                    return corsConf;
                                }
                        )
                )
                .authorizeHttpRequests(requests -> {
                    requests.requestMatchers("/auth/**").permitAll();
                    requests.requestMatchers("/v3/api-docs/**").permitAll();
                    requests.requestMatchers("/h2-console/**").permitAll();
                    requests.requestMatchers("/spotify/auth/**").permitAll();
                    requests.requestMatchers("/user/newUser").permitAll();
                    requests.requestMatchers("/songsterr/**").permitAll();
                    requests.requestMatchers("/error").anonymous(); // To allow error messages to be shown
                    requests.anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers // Necessary to view the h2-console
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .exceptionHandling((exceptionHandling) ->
                        exceptionHandling
                                .authenticationEntryPoint((request, response, authException) ->
                                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
                )
                .userDetailsService(userDetailsService)
                .sessionManagement((sessionManagement) ->
                        sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);;
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}