package by.mrk.service;

import by.mrk.dto.MailParam;

public interface MailSenderService {
    void send(MailParam mailParam);
}
