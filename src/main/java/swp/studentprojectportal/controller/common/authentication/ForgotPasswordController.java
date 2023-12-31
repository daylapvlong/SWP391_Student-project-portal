package swp.studentprojectportal.controller.common.authentication;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import swp.studentprojectportal.model.User;
import swp.studentprojectportal.service.servicesimpl.RegisterService;
import swp.studentprojectportal.service.servicesimpl.UserService;
import swp.studentprojectportal.utils.Utility;
import swp.studentprojectportal.utils.Validate;

import java.security.NoSuchAlgorithmException;

@Controller
public class ForgotPasswordController {
    @Autowired
    UserService userService;
    @Autowired
    RegisterService registerService;

    @GetMapping("/forgotPassword")
    public String forgotPasswordPage(HttpSession session) {
        return "authentication/forgotPassword";
    }

    @PostMapping("/forgotPassword")
    public String forgotPassword(Model model, WebRequest request, HttpSession session) {
        String username = request.getParameter("username").replace(" ", "");;

        User user = new User();
        if(user == null) {
            model.addAttribute("errmsg", "Username is't not correct");
            return "authentication/forgotPassword";
        }

        if(Validate.validEmail(username)) {
            user.setEmail(username);
            session.setAttribute("verifyMail", true);
        }
        if(Validate.validPhoneNumber(username)) {
            user.setPhone(username);
            session.setAttribute("verifyMail", false);
        }

        if(user.getEmail() != null && !userService.checkExistMail(user.getEmail())) {
            model.addAttribute("errmsg", "Your email is not exist");
            return "authentication/forgotPassword";
        }

        if(user.getEmail() != null && !userService.checkEmailDomain(user.getEmail())) {
            model.addAttribute("errmsg", "Your email domain is not accepted");
            return "authentication/forgotPassword";
        }

        System.out.println(user.getPhone());
        if(user.getPhone() != null && !userService.checkExistPhoneNumber(user.getPhone())) {
            model.addAttribute("errmsg", "Your phone number is not exist");
            return "authentication/forgotPassword";
        }

        User userauthen  = userService.getUserByEmailOrPhone(username);

        if(userauthen == null) {
            model.addAttribute("errmsg", "Username is't not correct");
            return "authentication/forgotPassword";
        }

        session.setAttribute("userauthen", userauthen);
        session.setAttribute("href", "reset-password");
        return "redirect:/verifypage";
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm(HttpSession session,@RequestParam("key") String token) {
        User user = userService.resetPasswordByToken(token);
        if(user != null) {
            session.removeAttribute("user");
            session.setAttribute("user", user);
            return "authentication/resetPassword";
        } else {
            return "redirect:/forgotPassword";
        }
    }

    @PostMapping("/reset-password")
    public String resetPassword(WebRequest request, Model model, HttpSession session) {
        String newPassword = request.getParameter("newPassword");
        String reNewPassword = request.getParameter("reNewPassword");
        User user= (User) session.getAttribute("user");

        // check old password empty
        try {
            if(!user.getPassword().equals(Utility.hash(""))) {
                return "redirect:/forgotPassword";
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        if(!newPassword.equals(reNewPassword)) {
            model.addAttribute("errmsg", "New Password and Re-new Password do not match");
            return "authentication/resetPassword";

        }

        if(!Validate.validPassword(newPassword)) {
            model.addAttribute("errmsg", "Password must be at least 8 characters, including uppercase, lowercase letters and numbers");
            return "authentication/resetPassword";
        }

        // check equal password and re-password
        if(newPassword.equals(reNewPassword)){
            try {
                user.setPassword(newPassword);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            session.setAttribute("user", user);
            model.addAttribute("errmsg", "Reset password successfully");
            //save to database
            User u = userService.saveUser(user);
            return "redirect:/login";
        }

        return "authentication/resetPassword";
    }

}
