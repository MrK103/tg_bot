package by.mrk.controller;

import by.mrk.service.PointService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/online")
public class OnlineController {
    private final PointService pointService;

    @GetMapping("/get-online")
    public ResponseEntity<?> getOnline(){
        var online = pointService.getOnline();
        return new ResponseEntity<>(online, HttpStatus.OK);
    }
}
