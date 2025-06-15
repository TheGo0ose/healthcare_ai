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
            throw new IllegalArgumentException("Username and password cannot be empty!");
        }

        User existingUser = userRepository.findByUsername(username);
        if (existingUser != null) {
            throw new RuntimeException("User already exist!");
        }

        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(username, hashedPassword);
        user.setEmail(username + "@example.com");

        User saved = userRepository.save(user);
        System.out.println("User created: " + saved.getUsername());
        return saved;
    }

    public User loginUser(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password are null!");
        }

        User user = userRepository.findByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Incorrect username or password!");
        }

        return user;
    }
}
