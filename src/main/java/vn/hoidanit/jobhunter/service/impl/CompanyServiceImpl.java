package vn.hoidanit.jobhunter.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.service.CompanyService;
import vn.hoidanit.jobhunter.service.util.error.NotFoundException;

import java.util.Optional;

@Service
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public Company handleFindById(Long id) {
        Optional<Company> companyOptional = this.companyRepository.findById(id);
        if(companyOptional.isPresent()){
            return companyOptional.get();
        }
        return null;
    }

    @Override
    public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }

    @Override
    public ResultPaginationDTO handleFindAll(Specification<Company> spec, Pageable pageable) {
        Page<Company> companyPage = this.companyRepository.findAll(pageable);

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        Meta  meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(companyPage.getTotalPages());
        meta.setTotal(companyPage.getTotalElements());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(companyPage.getContent());
        return resultPaginationDTO;
    }

    @Override
    public Company handleUpdateCompany(Long id, Company company) {
        Optional<Company> companyOptional = this.companyRepository.findById(id);
        if(!companyOptional.isPresent()){
            throw new NotFoundException("Company not found");
        }
        Company existingCompany = companyOptional.get();
        existingCompany.setName(company.getName());
        existingCompany.setAddress(company.getAddress());
        existingCompany.setDescription(company.getDescription());
        existingCompany.setLogo(company.getLogo());
        return this.companyRepository.save(existingCompany);
    }

    @Override
    public void handleDeleteCompany(Long id) {
        Optional<Company> companyOptional = this.companyRepository.findById(id);
        if(!companyOptional.isPresent()){
            throw new NotFoundException("Company not found");
        }
        this.companyRepository.deleteById(id);
    }
}
