package by.mrk.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import by.mrk.model.User;
import by.mrk.service.RoleService;
import by.mrk.service.UserService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@Tag(name = "Users", description = "methods for work with users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(RoleService roleService, UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    @Operation(summary = "information about all users")
    public ResponseEntity<List<User>> getUsers() {
        return new ResponseEntity<>(userService.findAll(),HttpStatus.OK);
    }

    @Operation(summary = "Create new users")
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@Valid @RequestBody User user, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String error = getErrorsFromBindingResult(bindingResult);
            System.err.println(error);
            return ResponseEntity.ok(HttpStatus.BAD_REQUEST);
        }
        try {
            userService.save(user);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (RuntimeException u) {
            return ResponseEntity.ok(HttpStatus.IM_USED);
        }
    }

    @Operation(summary = "Delete user by id")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> pageDelete(@PathVariable("id") long id) {
        userService.deleteById(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Operation(summary = "information about user by id")

    @GetMapping("users/{id}")
    public ResponseEntity<User> getUser (@PathVariable("id") long id) {
        System.out.println(id);
        User user = userService.getById(id);
        return new ResponseEntity<>(user,HttpStatus.OK);
    }

    @Operation(summary = "information about user by name (for authentication)")
    @GetMapping("/user")
    public ResponseEntity<User> getUserByUsername (Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return new ResponseEntity<>(user,HttpStatus.OK);
    }

    @Hidden
    @PutMapping("/users/{id}")
    public ResponseEntity<?> pageEdit(@PathVariable("id") long id,
                         @Valid @RequestBody User user,
                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String error = getErrorsFromBindingResult(bindingResult);
            System.err.println(error);
            return ResponseEntity.ok(HttpStatus.BAD_REQUEST);
        }

        try {
            userService.save(user);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (RuntimeException u) {
            return ResponseEntity.ok(HttpStatus.BAD_REQUEST);
        }
    }
    @Operation(summary = "collect all errors")
    private String getErrorsFromBindingResult(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
    }
}