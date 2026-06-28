package com.management.securityconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // No server-side sessions
            )
            .authorizeHttpRequests(auth -> auth
            	    .requestMatchers(
            	        "/", "/index.html", "/login.html", "/register.html",
            	        "/admin-login.html",
            	        "/restaurants.html", "/admin.html", "/orders.html",
            	        "/menu.html", "/menu-view.html", "/order.html",   // ← add order.html
            	        "/services.html", "/about.html", "/contact.html",
            	        "/menu-management.html",
            	        "/*.css", "/*.js", "/css/**", "/js/**",
            	        "/images/**", "/favicon.ico"
            	    ).permitAll()
            	    .requestMatchers(
            	        "/api/auth/**",
            	        "/restaurant/**",      // ← all restaurant endpoints
            	        "/menu/**",            // ← all menu endpoints
            	        "/order/**",           // ← all order endpoints (auth done client-side via userId)
            	        "/category/**",
            	        "/user/**",              // ← /user/all for customers list
            	        "/delivery-agent/**",
            	        "/api/auth/setup-admin"// ← if you have category endpoints
            	    ).permitAll()
            	    .anyRequest().authenticated()
            )
            // NO formLogin — we handle auth via REST
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write(
                        "{\"success\":false,\"message\":\"Unauthorized. Please log in.\"}"
                    );
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write(
                        "{\"success\":false,\"message\":\"Access denied.\"}"
                    );
                })
            );

        return http.build();
    }

   @Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    
    // Allow both your local environment and your live production URL
    config.setAllowedOrigins(List.of(
        "http://localhost:8080", 
        "http://127.0.0.1:8080",
        "https://restaurant-management-system-2-x0md.onrender.com"
    ));
    
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}



    // Expose AuthenticationManager so AuthController can use it
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
