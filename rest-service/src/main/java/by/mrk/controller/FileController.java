package by.mrk.controller;

import by.mrk.entity.AppDocument;
import by.mrk.entity.AppPhoto;
import by.mrk.service.FileService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j
@RestController
@RequestMapping("/file")
@AllArgsConstructor
public class FileController {
    private final FileService fileService;

    @GetMapping("/get-doc/{id}")
    public ResponseEntity<?> getDocument(@PathVariable("id") String id){
        //todo добавить контроллер эдвайс
        AppDocument document = fileService.getDocument(id);
        if (document == null){
            return ResponseEntity.badRequest().build();
        }
        var binaryContent = document.getBinaryContent();;
        var fileSystemResource = fileService.getFileSystemResource(binaryContent);
        if (fileSystemResource == null){
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getMimeType()))
                .header("Content-disposition", "attachment; filename=" + document.getDocumentName())
                .body(fileSystemResource);
    }

    @GetMapping("/get-photo/{id}")
    public ResponseEntity<?> getPhoto(@PathVariable("id") String id){
        //todo добавить контроллер эдвайс
        AppPhoto document = fileService.getPhoto(id);
        if (document == null){
            return ResponseEntity.badRequest().build();
        }
        var binaryContent = document.getBinaryContent();;
        var fileSystemResource = fileService.getFileSystemResource(binaryContent);

        if (fileSystemResource == null){
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header("Content-disposition", "attachment;")
                .body(fileSystemResource);
    }
}
