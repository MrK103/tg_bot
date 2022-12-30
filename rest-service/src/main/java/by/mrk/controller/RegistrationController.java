package by.mrk.controller;

import by.mrk.service.RegistrationService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j
@RestController
@RequestMapping("/mail")
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;


    @GetMapping("activate/{id}")
    public ResponseEntity<?> activateMail(@PathVariable(value = "id") String id){
        registrationService.activateUserAccount(id);
        return ResponseEntity.ok().build();
    }

}
