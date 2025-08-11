package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {
    ResultPaginationDTO getAllUsers(Specification<User> spec, Pageable pageable);

    User handleGetUserById(Long id);

    User handleCreateUser(User user);

    User handleUpdateUser(Long id, User user);

    void handleDeleteUser(Long id);

    User handleFindUserByUsername(String username);

    Boolean isExistByEmail(String email);

    ResCreateUserDTO convertToResCreateUserDTO(User user);

    ResUserDTO convertToResUserDTO(User user);

    ResUpdateUserDTO convertToResUpdateUserDTO(User user);

    void updateUserToken(String token, String email);

    public User getUserByRefreshTokenAndEmail(String refreshToken, String email);
}
