package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;

@Service
public interface CompanyService {
    Company handleFindById(Long id);
    Company handleCreateCompany(Company company);
    ResultPaginationDTO handleFindAll(Specification<Company> spec, Pageable pageable);
    Company handleUpdateCompany(Long id, Company company);
    void handleDeleteCompany(Long id);
}
