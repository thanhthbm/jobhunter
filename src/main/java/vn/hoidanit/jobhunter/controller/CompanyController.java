package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.CompanyService;
import vn.hoidanit.jobhunter.service.util.annotation.ApiMessage;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company company){
        Company createdCompany = this.companyService.handleCreateCompany(company);
        return ResponseEntity.ok().body(createdCompany);
    }

    @GetMapping("/companies")
    @ApiMessage("Fetch companies")
    public ResponseEntity<ResultPaginationDTO> getAllCompanies(
            @Filter Specification<Company> spec, Pageable pageable
    ){
        ResultPaginationDTO companiesPaginated = this.companyService.handleFindAll(spec, pageable);

        return  ResponseEntity.ok().body(companiesPaginated);
    }

    @PutMapping("/companies/{id}")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company company, @PathVariable Long id){
        Company updatedCompany = this.companyService.handleUpdateCompany(id, company);
        return ResponseEntity.ok().body(updatedCompany);
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id){
        this.companyService.handleDeleteCompany(id);
        return ResponseEntity.noContent().build();
    }
}
