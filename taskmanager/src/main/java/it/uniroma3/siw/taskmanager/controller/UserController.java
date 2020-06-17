package it.uniroma3.siw.taskmanager.controller;

import it.uniroma3.siw.taskmanager.controller.session.SessionData;
import it.uniroma3.siw.taskmanager.controller.validation.CredentialsValidator;
import it.uniroma3.siw.taskmanager.controller.validation.UserValidator;
import it.uniroma3.siw.taskmanager.model.Credentials;
import it.uniroma3.siw.taskmanager.model.User;
import it.uniroma3.siw.taskmanager.repository.UserRepository;
import it.uniroma3.siw.taskmanager.service.CredentialsService;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserValidator userValidator;

    @Autowired
    CredentialsValidator credentialsValidator;
    
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    SessionData sessionData;
    
    @Autowired
    CredentialsService credentialsService;

    @RequestMapping(value = { "/home" }, method = RequestMethod.GET)
    public String home(Model model) {
        User loggedUser = sessionData.getLoggedUser();
        model.addAttribute("user", loggedUser);
        return "home";
    }

    @RequestMapping(value = { "/users/me" }, method = RequestMethod.GET)
    public String me(Model model) {
        User loggedUser = sessionData.getLoggedUser();
        Credentials credentials = sessionData.getLoggedCredentials();
        System.out.println(credentials.getPassword());
        model.addAttribute("user", loggedUser);
        model.addAttribute("credentials", credentials);

        return "userProfile";
    }

    @RequestMapping(value = { "/admin" }, method = RequestMethod.GET)
    public String admin(Model model) {
        User loggedUser = sessionData.getLoggedUser();
        model.addAttribute("user", loggedUser);
        return "admin";
    }
    
    @RequestMapping(value = {"/user/me/updateProfile"}, method = RequestMethod.GET)
    public String updateProfile(Model model) {
        User loggedUser = sessionData.getLoggedUser();
        Credentials credentials = sessionData.getLoggedCredentials();
        model.addAttribute("userForm", loggedUser);
        model.addAttribute("credentialsForm", credentials);

        return "updateProfile";
    }
    
    @RequestMapping(value = {"/users/me/updateProfile"}, method = RequestMethod.POST)
    public String updateProfile(@Valid @ModelAttribute("userForm") User user,
            BindingResult userBindingResult,
            @Valid @ModelAttribute("credentialsForm") Credentials credentials,
            BindingResult credentialsBindingResult,
            Model model) {
    	
    	  this.userValidator.validate(user, userBindingResult);
          this.credentialsValidator.validate(credentials, credentialsBindingResult);
          
          Credentials loggedCredentials=this.sessionData.getLoggedCredentials();
          User loggedUser=this.sessionData.getLoggedUser();
          
              
        	  if(!userBindingResult.hasErrors() && !credentialsBindingResult.hasErrors()) {
        		  loggedUser.setFirstName(user.getFirstName());
        		  loggedUser.setLastName(user.getLastName());
        		  loggedCredentials.setUser(loggedUser);
        		  loggedCredentials.setUserName(credentials.getUserName());
        		  loggedCredentials.setPassword(credentials.getPassword());
                  credentialsService.saveCredentials(loggedCredentials);
                  return "redirect:/users/me";
                         }
       
        return "updateProfile";
    }

}
