package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.service.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.service.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService,  PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users")
    @ApiMessage("Get all users")
    public ResponseEntity<ResultPaginationDTO> getUsers(
            @Filter Specification<User> spec,
            Pageable pageable
            ) {
        return ResponseEntity.ok().body(this.userService.getAllUsers(spec, pageable));
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Get user by id")
    public ResponseEntity<ResUserDTO> getUser(@PathVariable Long id) throws IdInvalidException {
        User fetchUser = this.userService.handleGetUserById(id);
        if (fetchUser == null) {
            throw new IdInvalidException("User not found");
        }
        return ResponseEntity.ok().body(this.userService.convertToResUserDTO(fetchUser));
    }

    @PostMapping("/users")
    @ApiMessage("Create a new user")
    public ResponseEntity<ResCreateUserDTO> createUser(@Valid @RequestBody User user) throws IdInvalidException {
        boolean isEmailExists = this.userService.isExistByEmail(user.getEmail());
        if(isEmailExists){
            throw new IdInvalidException("Email already exists");
        }
        String hashedPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        User createdUser = this.userService.handleCreateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(createdUser));
    }

    @PutMapping("/users/{id}")
    @ApiMessage("Update a user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@PathVariable("id") Long id, @RequestBody User user) throws IdInvalidException{
        User fetchUser = this.userService.handleUpdateUser(id, user);
        if (fetchUser == null) {
            throw new IdInvalidException("User not found");
        }
        return ResponseEntity.ok().body(this.userService.convertToResUpdateUserDTO(fetchUser));
    }


    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) throws IdInvalidException {
        User currentUser = this.userService.handleGetUserById(id);
        if(currentUser == null){
            throw new IdInvalidException("User not found");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.noContent().build();

    }
}
