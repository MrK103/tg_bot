package by.mrk.service.impl;

import by.mrk.repository.PointRepository;
import by.mrk.service.PointService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@AllArgsConstructor
@Service
@Transactional
@Log4j
public class PointServiceImpl implements PointService {

    private final PointRepository pointRepository;

    @Override
    public Integer getOnline() {
        var online = pointRepository.online(1);
        log.debug(LocalDateTime.now() + " online: " + online );
        return online;
    }
}
