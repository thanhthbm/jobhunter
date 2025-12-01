package vn.thanhthbm.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.thanhthbm.jobhunter.domain.Company;
import vn.thanhthbm.jobhunter.domain.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
  User findByEmail(String email);

  boolean existsByEmail(String email);

  User findByRefreshTokenAndEmail(String token, String email);

  List<User> findByCompany(Company company);
}

