package chatbot.Controller;

import chatbot.Model.User;
import chatbot.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String root() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String index() {
        return "index";  // имя шаблона, который должен быть в resources/templates/index.html
    }


    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model,
                        HttpSession session) {
        try {
            User user = userService.loginUser(username, password);
            session.setAttribute("username", user.getUsername());
            return "chatbot";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "index";
        }
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           Model model,
                           HttpSession session) {
        try {
            System.out.println("Попытка регистрации: " + username);
            User user = userService.registerUser(username, password);
            session.setAttribute("username", user.getUsername());
            return "chatbot";
        } catch (RuntimeException e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "index";
        }
    }

    @GetMapping("/profile")
    public String showProfile(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/index";
        }

        model.addAttribute("username", username);
        // Можно загрузить доп. информацию, если нужно
        return "profile";
    }
    @GetMapping("/login")
    public String showLoginPage() {
        return "index"; // или страница логина, если у тебя отдельная
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "index"; // если регистрация тоже на index, иначе другую страницу
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/index";
    }
}
