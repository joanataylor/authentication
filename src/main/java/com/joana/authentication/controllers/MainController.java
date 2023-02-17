package com.joana.authentication.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.joana.authentication.models.LoginUser;
import com.joana.authentication.models.User;
import com.joana.authentication.services.UserService;

@Controller
public class MainController {
  // Add once service is implemented:
  @Autowired
  private UserService userServ;

  @GetMapping("/")
  public String index(Model model) {

    // Bind empty User and LoginUser objects to the JSP
    // to capture the form input
    model.addAttribute("newUser", new User());
    model.addAttribute("newLogin", new LoginUser());
    return "index.jsp";
  }

  @GetMapping("/home")
  public String dashboard(Model model, HttpSession session) {
    if(session.getAttribute("userId") == null){
      return "redirect:/";
    }
    Long id = (Long) session.getAttribute("userId");
    User user = userServ.getById(id);
    model.addAttribute("user", user);
    return "dashboard.jsp";
  }

  @PostMapping("/register")
  public String register(@Valid @ModelAttribute("newUser") User newUser,
      BindingResult result, Model model, HttpSession session) {

    // TO-DO Later -- call a register method in the service
    // to do some extra validations and create a new user!
    User user = userServ.register(newUser, result);

    if (result.hasErrors()) {
      // Be sure to send in the empty LoginUser before
      // re-rendering the page.
      model.addAttribute("newLogin", new LoginUser());
      return "index.jsp";
    }

    // No errors!
    // TO-DO Later: Store their ID from the DB in session,
    // in other words, log them in.
    session.setAttribute("userId", user.getId());
    return "redirect:/home";
  }

  @PostMapping("/login")
  public String login(@Valid @ModelAttribute("newLogin") LoginUser newLogin,
      BindingResult result, Model model, HttpSession session) {

    // Add once service is implemented:
    User user = userServ.login(newLogin, result);

    if (result.hasErrors()) {
      model.addAttribute("newUser", new User());
      return "index.jsp";
    }

    // No errors!
    // TO-DO Later: Store their ID from the DB in session,
    // in other words, log them in.
    session.setAttribute("userId", user.getId());
    return "redirect:/home";
  }

  @RequestMapping("/logout")
  public String logout(HttpSession session){
    session.invalidate();
    return "redirect:/";
  }

}