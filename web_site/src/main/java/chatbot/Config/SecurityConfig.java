package chatbot.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**", "/", "/index", "/register", "/login", "/logout", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/api/chatbot").permitAll() // Разрешаем доступ к API без аутентификации
                        .requestMatchers("/profile").permitAll() // Доступ к профилю только для аутентифицированных пользователей
                        .requestMatchers("/chatbot").authenticated() // Доступ к чату только для аутентифицированных пользователей
                        .requestMatchers("/api/**").permitAll() // Доступ к другим API только для аутентифицированных пользователей
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form.disable());

        return http.build();
    }
}
