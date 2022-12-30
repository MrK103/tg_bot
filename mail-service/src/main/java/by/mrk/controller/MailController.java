package by.mrk.controller;

import by.mrk.dto.MailParam;
import by.mrk.service.MailSenderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/mail")
@RestController
public class MailController {
    private final MailSenderService mailSenderService;

    public MailController(MailSenderService mailSenderService) {
        this.mailSenderService = mailSenderService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendActivationMail(@RequestBody MailParam mailParams) {
        mailSenderService.send(mailParams);
        return ResponseEntity.ok().build();
    }
}