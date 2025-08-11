package vn.hoidanit.jobhunter.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;
import vn.hoidanit.jobhunter.service.CompanyService;
import vn.hoidanit.jobhunter.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompanyRepository companyRepository;

    public UserServiceImpl(UserRepository userRepository,  PasswordEncoder passwordEncoder, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.companyRepository = companyRepository;
    }

    @Override
    public ResultPaginationDTO getAllUsers(Specification<User> spec, Pageable pageable) {
        // repo return Page type, so convert it to List
        Page<User> userPage = userRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(userPage.getTotalPages());
        meta.setTotal(userPage.getTotalElements());
        resultPaginationDTO.setMeta(meta);

        List<ResUserDTO> listUser = userPage.getContent()
                .stream().map(item -> ResUserDTO.builder()
                        .address(item.getAddress())
                        .age(item.getAge())
                        .id(item.getId())
                        .name(item.getName())
                        .email(item.getEmail())
                        .gender(item.getGender())
                        .createdAt(item.getCreatedAt())
                        .updatedAt(item.getUpdatedAt())
                        .company(new ResUserDTO.CompanyUser(
                                item.getCompany() != null ? item.getCompany().getId() : 0,
                                item.getCompany() != null ? item.getCompany().getName() : null
                        ))
                        .build())
                .collect(Collectors.toList());
        resultPaginationDTO.setResult(listUser);
        return resultPaginationDTO;
    }

    @Override
    public User handleGetUserById(Long id) {
        Optional<User> user = this.userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }

    @Override
    public User handleCreateUser(User user) {
        if (user.getCompany() != null) {
            Optional<Company> companyOptional = this.companyRepository.findById(user.getCompany().getId());
            user.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);

        }

        return this.userRepository.save(user);
    }

    @Override
    public User handleUpdateUser(Long id, User user) {
        User currentUser = this.handleGetUserById(id);
        if (currentUser != null) {
            currentUser.setAddress(user.getAddress());
            currentUser.setAge(user.getAge());
            currentUser.setName(user.getName());
            currentUser.setGender(user.getGender());

            if (currentUser.getCompany() != null) {
                Optional<Company> companyOptional = this.companyRepository.findById(currentUser.getCompany().getId());
                currentUser.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
            }

            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    @Override
    public void handleDeleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        this.userRepository.deleteById(id);
    }

    @Override
    public User handleFindUserByUsername(String username) {
        if (!userRepository.existsByEmail(username)) {
            throw new RuntimeException("User not found");
        }
        return this.userRepository.findByEmail(username);
    }

    @Override
    public Boolean isExistByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    @Override
    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO resCreateUserDTO = new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser com = new ResCreateUserDTO.CompanyUser();

        resCreateUserDTO.setId(user.getId());
        resCreateUserDTO.setName(user.getName());
        resCreateUserDTO.setEmail(user.getEmail());
        resCreateUserDTO.setAge(user.getAge());
        resCreateUserDTO.setAddress(user.getAddress());
        resCreateUserDTO.setGender(user.getGender());
        resCreateUserDTO.setCreatedAt(user.getCreatedAt());

        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            resCreateUserDTO.setCompany(com);
        }
        return resCreateUserDTO;
    }

    @Override
    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO resUserDTO = new ResUserDTO();
        ResUserDTO.CompanyUser com = new ResUserDTO.CompanyUser();

        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            resUserDTO.setCompany(com);
        }

        resUserDTO.setId(user.getId());
        resUserDTO.setName(user.getName());
        resUserDTO.setEmail(user.getEmail());
        resUserDTO.setAge(user.getAge());
        resUserDTO.setAddress(user.getAddress());
        resUserDTO.setGender(user.getGender());
        resUserDTO.setCreatedAt(user.getCreatedAt());
        resUserDTO.setUpdatedAt(user.getUpdatedAt());
        return resUserDTO;
    }

    @Override
    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO resUpdateUserDTO = new ResUpdateUserDTO();
        ResUpdateUserDTO.CompanyUser com = new ResUpdateUserDTO.CompanyUser();

        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            resUpdateUserDTO.setCompany(com);
        }

        resUpdateUserDTO.setId(user.getId());
        resUpdateUserDTO.setName(user.getName());
        resUpdateUserDTO.setAge(user.getAge());
        resUpdateUserDTO.setAddress(user.getAddress());
        resUpdateUserDTO.setGender(user.getGender());
        resUpdateUserDTO.setUpdatedAt(user.getUpdatedAt());
        return resUpdateUserDTO;
    }

    @Override
    public void updateUserToken(String token, String email) {
        User currentUser = this.handleFindUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    @Override
    public User getUserByRefreshTokenAndEmail(String refreshToken, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(refreshToken, email);
    }

}
