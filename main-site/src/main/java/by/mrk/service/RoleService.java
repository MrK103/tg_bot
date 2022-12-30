package by.mrk.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import by.mrk.model.Role;
import by.mrk.repo.RoleRepository;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Transactional
@Service
public class RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> findAllRole() {
        return roleRepository.findAll();
    }

    public Set<Role> findByIdRoles(List<Long> roles) {
        return new HashSet<>(roleRepository.findAllById(roles));
    }
    public Set<Role> findByIdRoles(Long id) {
        List<Long> roles  = new LinkedList<>();
        roles.add(id);
        return findByIdRoles(roles);
    }

    @PostConstruct
    public void addDefaultRole() {
        roleRepository.save(new Role(1L,"ROLE_USER"));
        roleRepository.save(new Role(2L,"ROLE_ADMIN"));
    }
}
