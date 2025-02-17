    package com.Ahmed.Banking.security;
    
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.web.cors.CorsConfiguration;
    import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
    import org.springframework.web.filter.CorsFilter;
    
    import java.util.List;
    
    @Configuration
    public class SecurityConfig {
    /*le probleme avec ca est qu'il donne l'acces au tt API sans jwt il faut pas ca i faut resoudre le prob apres*/
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable())  // Désactive CSRF
                    .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Active CORS
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/**").permitAll() // Autoriser toutes les routes temporairement
                    )
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // JWT => Stateless
    
            return http.build();
        }
    
        @Bean
        public CorsFilter corsFilter() {
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            CorsConfiguration config = new CorsConfiguration();
    
            config.setAllowCredentials(true);
            config.setAllowedOrigins(List.of("http://localhost:4200")); // Autoriser Angular
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    
            source.registerCorsConfiguration("/**", config);
            return new CorsFilter(source);
        }
    
        private UrlBasedCorsConfigurationSource corsConfigurationSource() {
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            CorsConfiguration config = new CorsConfiguration();
    
            config.setAllowCredentials(true);
            config.setAllowedOrigins(List.of("http://localhost:4200")); // Autoriser Angular
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    
            source.registerCorsConfiguration("/**", config);
            return source;
        }
    }
