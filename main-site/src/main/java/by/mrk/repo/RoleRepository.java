package by.mrk.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import by.mrk.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
