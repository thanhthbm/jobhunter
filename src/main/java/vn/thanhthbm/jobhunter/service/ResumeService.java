package vn.thanhthbm.jobhunter.service;

import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import vn.thanhthbm.jobhunter.domain.Job;
import vn.thanhthbm.jobhunter.domain.Notification;
import vn.thanhthbm.jobhunter.domain.Resume;
import vn.thanhthbm.jobhunter.domain.User;
import vn.thanhthbm.jobhunter.domain.response.ResultPaginationDTO;
import vn.thanhthbm.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.thanhthbm.jobhunter.domain.response.resume.ResFetchResumeDTO;
import vn.thanhthbm.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.thanhthbm.jobhunter.repository.JobRepository;
import vn.thanhthbm.jobhunter.repository.ResumeRepository;
import vn.thanhthbm.jobhunter.repository.UserRepository;
import vn.thanhthbm.jobhunter.util.SecurityUtil;

@Service
@RequiredArgsConstructor
public class ResumeService {
  @Autowired
  FilterBuilder fb;

  @Autowired
  private FilterParser filterParser;

  @Autowired
  private FilterSpecificationConverter filterSpecificationConverter;

  private final ResumeRepository resumeRepository;
  private final UserRepository userRepository;
  private final JobRepository jobRepository;
  private final NotificationService notificationService;


  public Optional<Resume> fetchById(long id) {
    return this.resumeRepository.findById(id);
  }

  public boolean checkResumeExistByUserAndJob(Resume resume) {
    // check user by id
    if (resume.getUser() == null)
      return false;
    Optional<User> userOptional = this.userRepository.findById(resume.getUser().getId());
    if (userOptional.isEmpty())
      return false;

    // check job by id
    if (resume.getJob() == null)
      return false;
    Optional<Job> jobOptional = this.jobRepository.findById(resume.getJob().getId());
    if (jobOptional.isEmpty())
      return false;

    return true;
  }

  public ResCreateResumeDTO create(Resume resume) {
    resume = this.resumeRepository.save(resume);

    ResCreateResumeDTO res = new ResCreateResumeDTO();
    res.setId(resume.getId());
    res.setCreatedBy(resume.getCreatedBy());
    res.setCreatedAt(resume.getCreatedAt());

    return res;
  }

  public ResUpdateResumeDTO update(Resume resume) {
    resume = this.resumeRepository.save(resume);

    if (resume.getUser() != null){
      String candidateEmail = resume.getUser().getEmail();

      this.notificationService.createAndSendNotification(
          candidateEmail,
          "Trạng thái hồ sơ thay đổi",
          "Hồ sơ " + resume.getJob().getName() + " đã chuyển sang " +resume.getStatus(),
          "RESUME_UPDATE"
      );
    }

    ResUpdateResumeDTO res = new ResUpdateResumeDTO();
    res.setUpdatedAt(resume.getUpdatedAt());
    res.setUpdatedBy(resume.getUpdatedBy());
    return res;
  }

  public void delete(long id) {
    this.resumeRepository.deleteById(id);
  }

  public ResFetchResumeDTO getResume(Resume resume) {
    ResFetchResumeDTO res = new ResFetchResumeDTO();
    res.setId(resume.getId());
    res.setEmail(resume.getEmail());
    res.setUrl(resume.getUrl());
    res.setStatus(resume.getStatus());
    res.setCreatedAt(resume.getCreatedAt());
    res.setCreatedBy(resume.getCreatedBy());
    res.setUpdatedAt(resume.getUpdatedAt());
    res.setUpdatedBy(resume.getUpdatedBy());

    if (resume.getJob() != null) {
      res.setCompanyName(resume.getJob().getCompany().getName());
    }

    res.setUser(new ResFetchResumeDTO.UserResume(resume.getUser().getId(), resume.getUser().getName()));
    res.setJob(new ResFetchResumeDTO.JobResume(resume.getJob().getId(), resume.getJob().getName()));

    return res;
  }

  public ResultPaginationDTO fetchAllResume(Specification<Resume> spec, Pageable pageable) {
    Page<Resume> pageUser = this.resumeRepository.findAll(spec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();
    ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

    mt.setPage(pageable.getPageNumber() + 1);
    mt.setPageSize(pageable.getPageSize());

    mt.setPages(pageUser.getTotalPages());
    mt.setTotal(pageUser.getTotalElements());

    rs.setMeta(mt);

    // remove sensitive data
    List<ResFetchResumeDTO> listResume = pageUser.getContent()
        .stream().map(item -> this.getResume(item))
        .collect(Collectors.toList());

    rs.setResult(listResume);

    return rs;
  }

  public ResultPaginationDTO fetchResumeByUser(Pageable pageable) {
    // query builder
    String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
        ? SecurityUtil.getCurrentUserLogin().get()
        : "";
    FilterNode node = filterParser.parse("email='" + email + "'");
    FilterSpecification<Resume> spec = filterSpecificationConverter.convert(node);
    Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);

    ResultPaginationDTO rs = new ResultPaginationDTO();
    ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

    mt.setPage(pageable.getPageNumber() + 1);
    mt.setPageSize(pageable.getPageSize());

    mt.setPages(pageResume.getTotalPages());
    mt.setTotal(pageResume.getTotalElements());

    rs.setMeta(mt);

    // remove sensitive data
    List<ResFetchResumeDTO> listResume = pageResume.getContent()
        .stream().map(item -> this.getResume(item))
        .collect(Collectors.toList());

    rs.setResult(listResume);

    return rs;
  }
}

