package by.mrk.service;

import by.mrk.entity.AppDocument;
import by.mrk.entity.AppPhoto;
import by.mrk.entity.BinaryContent;
import org.springframework.core.io.FileSystemResource;

public interface FileService {
    AppDocument getDocument(String id);
    AppPhoto getPhoto(String id);
    FileSystemResource getFileSystemResource(BinaryContent binaryContent);
}
