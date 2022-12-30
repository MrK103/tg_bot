package by.mrk.service;

import by.mrk.entity.AppDocument;
import by.mrk.entity.AppPhoto;
import by.mrk.entity.AppUser;
import by.mrk.service.enums.LinkType;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);

    String generateLink(Long docId, LinkType linkType);
    String getAllFile(Long userId, LinkType linkType);
}
