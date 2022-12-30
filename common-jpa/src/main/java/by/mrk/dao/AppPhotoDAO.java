package by.mrk.dao;

import by.mrk.entity.AppPhoto;
import by.mrk.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.NamedNativeQuery;
import java.util.List;

public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long> {

//    @NamedNativeQuery(query = "SELECT p.id from AppPhoto p where p.user_id = :id")
//    List<Long> findAllByUserId(@Param("id") AppUser id);
@Query(value = "SELECT t.id FROM app_photo t WHERE t.user_id=:id ", nativeQuery = true)
List<Long> findAllByUserId(@Param("id") Long id);

}
