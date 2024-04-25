package in.ashokit.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import in.ashokit.dto.LoginDto;
import in.ashokit.dto.RegesterDto;
import in.ashokit.dto.ResetPwdDto;
import in.ashokit.dto.UserDto;
import in.ashokit.service.UserService;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        RegesterDto registerDto = new RegesterDto();
        model.addAttribute("registerDto", registerDto);
        
        // Fetching countries and adding them to the model
        Map<Integer, String> countries = userService.getCountries();
        model.addAttribute("countries", countries);
        
        return "registerView"; // Returning the registration view
    }


    @GetMapping("/states/{cid}")
    @ResponseBody
    public Map<Integer, String> getStates(@PathVariable("cid") Integer cid) {
        return userService.getStates(cid);
    }

    @GetMapping("/cities/{sid}")
    @ResponseBody
    public Map<Integer, String> getCities(@PathVariable("sid") Integer sid) {
        return userService.getCities(sid);
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("registerDto") RegesterDto regDto, Model model) {
        UserDto user = userService.getUser(regDto.getEmail());
        if (user != null) {
            model.addAttribute("emsg", "Duplicate Email");
            return "registerView";
        }
            boolean regesterUser = userService.regesterUser(regDto);
            if(regesterUser) {
            	model.addAttribute("smsg", "User Registered");
            }else {
            	model.addAttribute("emsg", "Registartion failed");
            }
            model.addAttribute("countries", userService.getCountries());
            return "registerView";
    }

    @GetMapping("/")
    public String loginPage(Model model) {
        model.addAttribute("loginObj", new LoginDto());
        return "index";
    }

    @PostMapping("/login")
    public String login( LoginDto loginDto, Model model) {   //@ModelAttribute("loginObj")
        UserDto user = userService.getUser(loginDto);
        if (user == null) {
            model.addAttribute("emsg", "Invalid Credentials");
            return "index";
        }
        
        // Check if the user has updated their password
        if ("YES".equals(user.getPwdUpdate())){
            return "redirect:dashBoard";
            
        }else {
        // pwd not updated go to rest pwd page
        	ResetPwdDto resetPwdDto = new ResetPwdDto();
        	resetPwdDto.setEmail(user.getEmail());
        	model.addAttribute("resetPwdDto", resetPwdDto);
        	return "resetPwdView";
     }
    }



   
    @PostMapping("/resetPwd")
    public String resetPwd(@ModelAttribute("resetPwdDto") ResetPwdDto resetPwdDto, Model model) {
//        // Check if newPwd or confirmPwd is null
//        if (resetPwdDto.getNewPwd() == null || resetPwdDto.getConfirmPwd() == null) {
//            model.addAttribute("emsg", "New password or confirm password cannot be null");
//            return "resetPwdView";
//        }

        if (!resetPwdDto.getNewPwd().equals(resetPwdDto.getConfirmPwd())) {
            model.addAttribute("emsg", "New password and confirm password should be same");
            return "resetPwdView";
        }

        UserDto user = userService.getUser(resetPwdDto.getEmail());
        if ( user.getPwd().equals(resetPwdDto.getOldPwd())) {
            boolean resetPwd = userService.resetPwd(resetPwdDto);
            if (resetPwd) {
                return "redirect:/dashBoard";
            } else {
                model.addAttribute("emsg", "Password Update Failed");
                return "resetPwdView";
            }
        } else {
            model.addAttribute("emsg", "Given Old Password is wrong");
            return "resetPwdView";
        }
    }


    @GetMapping("/dashBoard")
    public String dashboard(Model model) {
        String quote = userService.getQuote();
        model.addAttribute("quote", quote);
        return "dashBoard";
    }

    @GetMapping("/logOut")
    public String logout() {
        return "index";
    }
}


