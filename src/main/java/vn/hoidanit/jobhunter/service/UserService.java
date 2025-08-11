package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;

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
