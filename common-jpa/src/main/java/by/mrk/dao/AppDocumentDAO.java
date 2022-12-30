package by.mrk.dao;

import by.mrk.entity.AppDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AppDocumentDAO extends JpaRepository<AppDocument, Long> {

    @Query(value = "SELECT t.id FROM app_document t WHERE t.user_id=:id ", nativeQuery = true)
    List<Long> findAllByUserId(@Param("id") Long id);
}
