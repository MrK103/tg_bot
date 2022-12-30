package by.mrk.service.impl;

import by.mrk.CryptoTool;
import by.mrk.dao.AppUserDAO;
import by.mrk.dao.RawDataDAO;
import by.mrk.entity.AppDocument;
import by.mrk.entity.AppPhoto;
import by.mrk.entity.AppUser;
import by.mrk.entity.RawData;
import by.mrk.exceptions.UploadFileException;
import by.mrk.service.FileService;
import by.mrk.service.MainService;
import by.mrk.service.ProducerService;
import by.mrk.service.RegistrationService;
import by.mrk.service.enums.LinkType;
import by.mrk.service.enums.ServiceCommands;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.Objects;

import static by.mrk.entity.enums.UserState.BASIC_STATE;
import static by.mrk.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static by.mrk.service.enums.ServiceCommands.*;

@Transactional
@Service
@Log4j
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;
    private final FileService fileService;

    private final RegistrationService registrationService;

    @Value(value = "${url.rest-pw-test-server.online_controller}")
    private String getOnlineUrl;

    public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService, AppUserDAO appUserDAO, FileService fileService, RegistrationService registrationService) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
        this.fileService = fileService;
        this.registrationService = registrationService;
    }

    @Override
    public void processTextMessage(Update update) {

//        if (update.getMessage().getChatId() == 1016234436l) {
//            sendAnswer(update.getMessage().getText(), 930673757l);
//            sendAnswer("Отправлено Максимке", 1016234436l);
//            return;
//        }

        saveRawData(update);
        var appUser = findOrSaveAppUser(update);

        if (!update.getMessage().getChatId().equals(1016234436l)) {
            sendAnswer(getLogMessage(update), 1016234436l);
        }


        var userState = appUser.getState();

        var text = update.getMessage().getText();
        var answer = "";
        var serviceCommand = ServiceCommands.fromValue(text);

        if (CANCEL.equals(serviceCommand)) {
            answer = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            answer = processServiceCommand(appUser, serviceCommand);
            if (answer.equals("")) return;
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            //todo вынести в отдельный сервис и проверка емаил
            answer = registrationService.sendRegistrationLink(appUser, text);
        } else {
            log.error("Unknown user state " + userState);
            answer = "Ошибка, введите /cansel и попробуйте еще раз";
        }

        var chatId = update.getMessage().getChatId();
        sendAnswer(answer, chatId);
    }

    private String getLogMessage(Update update) {
        var log = "";
        log = "Пользователь @"
                + update.getMessage().getFrom().getUserName()
                + " отправил сообщение в чат: \n"
                + update.getMessage().getText();
        return log;
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();

        if (isNotSupported(chatId, appUser)) {
            return;
        }

        try {
            AppPhoto photo = fileService.processPhoto(update.getMessage());
            if (photo != null) {
                var userPhotoList = appUser.getAppPhotos();
                assert userPhotoList != null;
                userPhotoList.add(photo);
                appUserDAO.save(appUser);
            }
            String link = fileService.generateLink(Objects.requireNonNull(photo).getId(), LinkType.GET_PHOTO);

            var answer = "Фото успешно загружено, ссылка для скачивая: " + link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException exception) {
            log.error(exception);
            String error = "К сожалению, загрузка фото не удалась. Повторите попытку позже";
            sendAnswer(error, chatId);
        }

    }

    @Override
    public void processDocumentMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();

        if (isNotSupported(chatId, appUser)) {
            return;
        }

        try {
            AppDocument document = fileService.processDoc(update.getMessage());
            String link = fileService.generateLink(document.getId(), LinkType.GET_DOC);
            var answer = "Документ успешно загружен, ссылка для скачивая " + link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException exception) {
            log.error(exception);
            String error = "К сожалению, загрузка файла не удалась. Повторите попытку позже";
            sendAnswer(error, chatId);
        }
    }

    private boolean isNotSupported(Long chatId, AppUser appUser) {
        var userState = appUser.getState();
        if (!appUser.getIsActive()) {
            var error = "Зарегестрируйся или подтверди почту";
            sendAnswer(error, chatId);
            return true;
        } else if (!BASIC_STATE.equals(userState)) {
            var error = "Отмение текущую команду с помощью /cansel для отправки файлов";
            sendAnswer(error, chatId);
            return true;
        }
        return false;
    }

    private void sendAnswer(String answer, Long chatId) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(answer);
        producerService.producerAnswer(sendMessage);
    }

    private String processServiceCommand(AppUser appUser, ServiceCommands text) {
        if (GET_ONLINE.equals(text)) {
            return getOnlineFromTestServer();
        } else if (REGISTRATION.equals(text)) {
            if (appUser.getIsActive()) {
                return "Вы уже зарегестрированы";
            }
            if (appUser.getState().equals(BASIC_STATE)) {
                appUser.setState(WAIT_FOR_EMAIL_STATE);
                appUserDAO.save(appUser);
                return "Введите email";
            }

        } else if (GET_ALL_PHOTO_LINK.equals(text)) {
            return fileService.getAllFile(appUser.getId(), LinkType.GET_PHOTO);
        } else if (GET_ALL_DOC_LINK.equals(text)) {
            return fileService.getAllFile(appUser.getId(), LinkType.GET_DOC);
        } else if (HELP.equals(text)) {
            return help();
        } else if (START.equals(text)) {
            return "Привет, я хрен знает для чего данный бот, введи /help для получения всех команд";
        } else return "Неизвестная комнда, введите /help";
        return "";
    }

    private String getOnlineFromTestServer() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);
        var message = "Онлаин на тестовом сервере: ";
        var answer = "";
        try {
            answer = restTemplate.exchange(
                    getOnlineUrl,
                    HttpMethod.GET,
                    request,
                    Integer.class
            ).getBody().toString();
        } catch (HttpClientErrorException ex) {
            log.error(ex);
            answer = "ошибка подключения";
        }

        System.out.println(answer);

        return message + answer;
    }


    private String help() {
        return "Список доступных команд:\n"
                + "/cansel - отмена\n"
                + "/registration - регистрация\n"
                + "/get_online - онлаин тестового сервер\n"
                + "/get_all_photo - загруженные фото\n"
                + "/get_all_doc - загруженные документы";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE);
        appUserDAO.save(appUser);
        return "Команда отменена";
    }

    private AppUser findOrSaveAppUser(Update update) {
        var telegramUser = update.getMessage().getFrom();
        AppUser persistentAppUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());
        if (persistentAppUser == null) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    //TODO изменить значение по умолчанию после добавления регистрации
                    .isActive(false)
                    .state(BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser);
        }
        return persistentAppUser;
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDAO.save(rawData);
    }

}
