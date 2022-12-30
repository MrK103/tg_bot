package by.mrk.service.impl;

import by.mrk.CryptoTool;
import by.mrk.dao.AppUserDAO;
import by.mrk.entity.AppUser;
import by.mrk.service.RegistrationService;
import lombok.extern.log4j.Log4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Pattern;

import static by.mrk.entity.enums.UserState.BASIC_STATE;

@Service
@Log4j
public class RegistrationServiceImpl implements RegistrationService {

    private final AppUserDAO appUserDAO;
    private final CryptoTool cryptoTool;

    @Value(value = "${regexp.email}")
    private String regexEmail;

    @Value(value = "${url.mail-service.mail_controller}")
    private String emailControllerURL;

    public RegistrationServiceImpl(AppUserDAO appUserDAO, CryptoTool cryptoTool) {
        this.appUserDAO = appUserDAO;
        this.cryptoTool = cryptoTool;
    }


    private boolean validEmail(String emailAddress) {
        return Pattern.compile(regexEmail)
                .matcher(emailAddress)
                .matches();
    }

    @Override
    public String sendRegistrationLink(AppUser appUser, String email) {

        if (!validEmail(email)) {
            String invalidEmail = "Введенный email не корректный, попробуйте еще раз или введите /cansel";
            return invalidEmail;
        }
        appUser.setEmail(email);
        try {
            RestTemplate restTemplate = new RestTemplate();
            var headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            var personJsonObject = new JSONObject();
            personJsonObject.put("id", cryptoTool.hashOf(appUser.getId()));
            personJsonObject.put("emailTo", email);

            HttpEntity<String> request = new HttpEntity<>(personJsonObject.toString(), headers);
            restTemplate.postForEntity(emailControllerURL, request, String.class);

            return "Письмо отправлено на почту " + email + ", перейдите по ссылке из письма для регистрации";
        } catch (RuntimeException e) {
            log.error(e);
            return "Ошибка: " + e.getMessage() + ". Попробуйте еще раз /registration";
        } finally {
            appUser.setState(BASIC_STATE);
            appUserDAO.save(appUser);
        }

    }
}
