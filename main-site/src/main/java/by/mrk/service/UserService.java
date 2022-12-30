package by.mrk.service;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import by.mrk.model.User;
import by.mrk.repo.UserRepository;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Transactional
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Autowired
    public UserService(UserRepository userRepository, RoleService service) {
        this.userRepository = userRepository;
        this.roleService = service;
        this.passwordEncoder = new BCryptPasswordEncoder(10);
    }

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public void deleteById(Long id){
        userRepository.deleteById(id);
    }

    public void save(User user){
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public User getById(@NonNull Long id){
        return userRepository.findById(id).get();
    }
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (userRepository.findAll().isEmpty()){
            userRepository.save(
                    new User("admin",
                            "admin",
                            (byte) 99,
                            "email@mail.ru",
                            "admin",
                            passwordEncoder.encode("admin"),
                            roleService.findByIdRoles(2L)));
        }

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }


}
