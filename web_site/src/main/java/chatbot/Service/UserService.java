package chatbot.Service;

import chatbot.Model.User;
import chatbot.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User registerUser(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Имя пользователя и пароль не могут быть пустыми!");
        }

        User existingUser = userRepository.findByUsername(username);
        if (existingUser != null) {
            throw new RuntimeException("Такой пользователь уже существует!");
        }

        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(username, hashedPassword);
        user.setEmail(username + "@example.com");

        User saved = userRepository.save(user);
        System.out.println("Пользователь зарегистрирован: " + saved.getUsername());
        return saved;
    }

    public User loginUser(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Имя пользователя и пароль обязательны.");
        }

        User user = userRepository.findByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Неправильное имя или пароль!");
        }

        return user;
    }
}
