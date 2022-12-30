package by.mrk.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "role")
public class Role implements GrantedAuthority {
    @Id
    private Long id;

    private String role;

    public Role(long id, String role_user) {
        this.id = id;
        this.role = role_user;
    }

    public Role() {

    }

    @Override
    public String getAuthority() {
        return role;
    }
    public String getRole() {
        return role;
    }
}
