package net.slipp.web;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import net.slipp.domain.Question;
import net.slipp.domain.QuestionRepository;
import net.slipp.domain.User;

@Controller
@RequestMapping("/questions")
public class QuestionController {

	@Autowired
	private QuestionRepository questionRepository;
	
	@GetMapping("/form")
	public String Form(HttpSession session) {
		if (!HttpSessionUtils.isLoginUser(session)) {
			System.out.println(session);
			return "/users/loginForm";
		}
		return "/qna/form";
	}

	@PostMapping("")
	public String create(String title, String contents, HttpSession session) {
		//로그인 상태 체크해서 로그인 사용자만 글쓰기가 가능해야 함.
		if (!HttpSessionUtils.isLoginUser(session)) {
			
			return "/users/loginForm";
		}
		User sessionUser = HttpSessionUtils.getUserFromSession(session);
		Question newQuestion = new Question(sessionUser.getUserId(), title, contents);
		questionRepository.save(newQuestion);
		
		return "redirect:/"; //질문하기 완료시 처음으로 이동
	}
}
