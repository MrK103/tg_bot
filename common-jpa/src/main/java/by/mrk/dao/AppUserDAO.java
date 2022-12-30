package by.mrk.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import by.mrk.entity.AppUser;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.lang.annotation.Native;

public interface AppUserDAO extends JpaRepository<AppUser, Long> {
    AppUser findAppUserByTelegramUserId(Long id);

    @Modifying
    @Query(value = "UPDATE AppUser u SET u.isActive = TRUE WHERE u.id = :id")
    void activateUserAccount(@Param("id")Long id);
}
