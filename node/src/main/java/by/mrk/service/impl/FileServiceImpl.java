package by.mrk.service.impl;

import by.mrk.CryptoTool;
import by.mrk.dao.AppDocumentDAO;
import by.mrk.dao.AppPhotoDAO;
import by.mrk.dao.AppUserDAO;
import by.mrk.dao.BinaryContentDAO;
import by.mrk.entity.AppDocument;
import by.mrk.entity.AppPhoto;
import by.mrk.entity.BinaryContent;
import by.mrk.exceptions.UploadFileException;
import by.mrk.service.FileService;
import by.mrk.service.enums.LinkType;
import lombok.extern.log4j.Log4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Log4j
@Service
public class FileServiceImpl implements FileService {
    @Value("${token}")
    private String token;
    @Value("${service.file.info.url}")
    private String fileInfoUri;
    @Value("${service.file.storage.uri}")
    private String fileStorageUri;
    @Value("${link.adress}")
    private String linkAdress;
    private final CryptoTool cryptoTool;
    private final AppDocumentDAO appDocumentDAO;
    private final BinaryContentDAO binaryContentDAO;
    private final AppPhotoDAO appPhotoDAO;
    private final AppUserDAO appUserDAO;

    public FileServiceImpl(CryptoTool cryptoTool, AppDocumentDAO appDocumentDAO, BinaryContentDAO binaryContentDAO, AppPhotoDAO appPhotoDAO, AppUserDAO appUserDAO) {
        this.cryptoTool = cryptoTool;
        this.appDocumentDAO = appDocumentDAO;
        this.binaryContentDAO = binaryContentDAO;
        this.appPhotoDAO = appPhotoDAO;
        this.appUserDAO = appUserDAO;
    }


    @Override
    public AppDocument processDoc(Message telegramMessage) {
        Document telegramDocument = telegramMessage.getDocument();
        String fieldId = telegramDocument.getFileId();
        ResponseEntity<String> response = getFilePath(fieldId);
        if (response.getStatusCode() == HttpStatus.OK) {
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            AppDocument appDocument = buildTransientAppDoc(telegramDocument, persistentBinaryContent);
            return appDocumentDAO.save(appDocument);
        } else {
            throw new UploadFileException("Bad response " + response);
        }
    }

    @Override
    public AppPhoto processPhoto(Message telegramMessage) {


        var photoSizeCount = telegramMessage.getPhoto().size();
        var photoIndex = photoSizeCount > 1 ? telegramMessage.getPhoto().size() - 1 : 0;
        PhotoSize telegramDocument = telegramMessage.getPhoto().get(photoIndex);
        String fieldId = telegramDocument.getFileId();
        ResponseEntity<String> response = getFilePath(fieldId);
        if (response.getStatusCode() == HttpStatus.OK) {
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            //todo обавить
            AppPhoto appPhoto = buildTransientAppPhoto(telegramDocument, persistentBinaryContent);

            return appPhotoDAO.save(appPhoto);
        } else {
            throw new UploadFileException("Bad response " + response);
        }
    }

    @Override
    public String generateLink(Long docId, LinkType linkType) {
        var hash = cryptoTool.hashOf(docId);
        return "http://" + linkAdress + "/" + linkType + "/" + hash;
    }

    private AppPhoto buildTransientAppPhoto(PhotoSize telegramDocument, BinaryContent persistentBinaryContent) {
        return AppPhoto.builder()
                .telegramField(telegramDocument.getFileId())
                .binaryContent(persistentBinaryContent)
                .fileSize(Long.valueOf(telegramDocument.getFileSize()))
                .build();
    }

    private BinaryContent getPersistentBinaryContent(ResponseEntity<String> response) {
        String filePath = getFilePath(response);
        byte[] fileInByte = downloadFile(filePath);
        var transientBinaryContent = BinaryContent.builder()
                .fileAsArrayOfBytes(fileInByte)
                .build();
        return binaryContentDAO.save(transientBinaryContent);
    }

    @Override
    public String getAllFile(Long userId, LinkType linkType) {
        List<Long> listFile = getListWithFoundedFile(userId, linkType);
            if (listFile == null || listFile.isEmpty()) {
                return "У вас нет загруженных файлов";
            }
            StringBuilder answer = new StringBuilder("Найдено " + listFile.size() + " файла(ов):\n");
            listFile.forEach(id -> answer.append(generateLink(id, linkType)).append("\n"));
            return answer.toString();
    }

    private List<Long> getListWithFoundedFile(Long userId, LinkType linkType) {
        try {
            if (LinkType.GET_DOC.equals(linkType)){
                return  appDocumentDAO.findAllByUserId(userId);
            } else if (LinkType.GET_PHOTO.equals(linkType)){
                return appPhotoDAO.findAllByUserId(userId);
            } else {
                var error = "Неправильный тип данных (" + linkType.toString() +"), возможно вы забыли добавить его обработку";
                log.error(error);
                return null;
            }
        } catch (RuntimeException e){
            log.error(e);
            return null;
        }
    }


    private static String getFilePath(ResponseEntity<String> response) {
        var jsonObject = new JSONObject(response.getBody());
        return String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path"));
    }


    private AppDocument buildTransientAppDoc(Document telegramDocument, BinaryContent persistentBinaryContent) {
        return AppDocument.builder()
                .telegramField(telegramDocument.getFileId())
                .documentName(telegramDocument.getFileName())
                .binaryContent(persistentBinaryContent)
                .mimeType(telegramDocument.getMimeType())
                .fileSize(telegramDocument.getFileSize())
                .build();
    }

    private byte[] downloadFile(String filePath) {
        String fullUrl = fileStorageUri.replace("{token}", token).replace("{filePath}", filePath);
        URL urlObj = null;
        try {
            urlObj = new URL(fullUrl);
        } catch (MalformedURLException e) {
            throw new UploadFileException(e.getMessage());
        }
        //TODO оптимизация скачивания больших файлов
        try (InputStream is = urlObj.openStream()) {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new UploadFileException(e.getMessage());
        }

    }


    private ResponseEntity<String> getFilePath(String fieldId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);

        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                token,
                fieldId
        );
    }
}
