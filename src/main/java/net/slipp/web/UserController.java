package net.slipp.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import net.slipp.domain.User;
import net.slipp.domain.UserRepository;

@Controller
@RequestMapping("/users")
public class UserController {
	private List<User> users = new ArrayList<User>();

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/loginForm")
	public String loginForm() {
		return "/user/login";
	}

	@PostMapping("/login")
	public String login(String userId, String password, HttpSession session) {
		User user = userRepository.findByUserId(userId);
		System.out.println(user + "useriD : " + userId + " password : " + password);
		if (user == null) {
			System.out.println("Login Failure!");
			return "redirect:/users/loginForm";
		}

		// if (!password.equals(user.getPassword())) {
		if (!user.matchPassword(password)) {
			System.out.println("Login Failure!");
			return "redirect:/users/loginForm";
		}

		session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, user);
		System.out.println("Login success!");
		return "redirect:/";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute(HttpSessionUtils.USER_SESSION_KEY);
		return "redirect:/";
	}

	@GetMapping("/form")
	public String form() {
		return "/user/form";
	}

	@PostMapping("")
	public String create(User user) {
		System.out.println("user : " + user);
		// users.add(user);
		userRepository.save(user);
		return "redirect:/users";
	}

	@GetMapping("")
	public String list(Model model) {
		model.addAttribute("users", userRepository.findAll());
		return "/user/list";
	}

	@GetMapping("/{id}/form")
	public String updateForm(@PathVariable Long id, Model model, HttpSession session) {
		// Object tempUser = session.getAttribute("sessionedUser");
		// if (tempUser == null) {
		if (HttpSessionUtils.isLoginuser(session)) {
			return "redirect:/users/loginForm";
		}

		// User sessionedUser = (User) tempUser;
		User sessionedUser = HttpSessionUtils.getUserFromSession(session);

		//if (!id.equals(sessionedUser.getId())) {
		if(!sessionedUser.matchId(id)){
			throw new IllegalStateException("You can't update the anther user");
		}

		model.addAttribute("user", userRepository.findOne(id));
		// model.addAttribute("user",
		// userRepository.findOne(sessionedUser.getId()));
		return "/user/updateForm";
	}

	@PutMapping("/{id}")
	public String update(@PathVariable Long id, User updatedUser, HttpSession session) {
		System.out.println("update newUser :" + updatedUser);
		// Object tempUser = session.getAttribute("sessionedUser");
		// if (tempUser == null) {
		if (HttpSessionUtils.isLoginuser(session)) {
			return "redirect:/users/loginForm";
		}
		// User sessionedUser = (User) tempUser;
		User sessionedUser = HttpSessionUtils.getUserFromSession(session);

		//if (!id.equals(sessionedUser.getId())) {
		if(!sessionedUser.matchId(id)){
			throw new IllegalStateException("You can't update the anther user");
		}

		User user = userRepository.findOne(id);
		user.update(updatedUser);
		userRepository.save(user);
		return "redirect:/users";
	}
}
