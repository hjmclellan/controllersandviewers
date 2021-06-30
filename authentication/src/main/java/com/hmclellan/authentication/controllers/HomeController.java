package com.hmclellan.authentication.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hmclellan.authentication.models.User;
import com.hmclellan.authentication.services.UserService;
import com.hmclellan.authentication.validator.UserValidator;

@Controller
public class HomeController {

	private final UserService userServ;
	//Bring in Validator
	private final UserValidator userVal;
	
	
	public HomeController(UserService userServ, UserValidator userVal) {
		this.userServ = userServ;
		this.userVal = userVal;
	}
	
	@GetMapping("/")
	public String index(@ModelAttribute("user")User user) {
		return "index.jsp";
	}
	@GetMapping("/home")
	public String home() {
		return "homePage.jsp";
	}
	
	@PostMapping("/registration")
	public String register(@Valid @ModelAttribute("user")User newUser, BindingResult result, HttpSession session) {
		userVal.validate(newUser, result);
		if(result.hasErrors()) {
			return "index.jsp";
		} else {
			userServ.registerUser(newUser);
			return "redirect:/home";
		}
	}
	
	@PostMapping("/login")
	public String login(@RequestParam("email")String email,@RequestParam("password")String password, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
		boolean isAuth = userServ.authenticateUser(email, password);
		if(isAuth) {
			User u = userServ.findByEmail(email);
			session.setAttribute("userId", u.getId());
			return "redirect:/home";
		} else {
			redirectAttributes.addFlashAttribute("error", "Invalid Login Attempt!");
			return "redirect:/";
		}
	}
	
	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}
}