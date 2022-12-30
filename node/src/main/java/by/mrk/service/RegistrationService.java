package by.mrk.service;

import by.mrk.entity.AppUser;

public interface RegistrationService {
    String sendRegistrationLink(AppUser appUser, String email);
}
