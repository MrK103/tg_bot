package by.mrk.service.impl;

import by.mrk.CryptoTool;
import by.mrk.dao.AppUserDAO;
import by.mrk.entity.AppUser;
import by.mrk.service.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Service
@AllArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final AppUserDAO appUserDAO;
    private final CryptoTool cryptoTool;

    @Override
    public void activateUserAccount(String id){
        appUserDAO.activateUserAccount(cryptoTool.idOf(id));
    }

}
