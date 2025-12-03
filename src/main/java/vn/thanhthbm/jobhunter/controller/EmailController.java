package vn.thanhthbm.jobhunter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.thanhthbm.jobhunter.service.EmailService;
import vn.thanhthbm.jobhunter.service.SubscriberService;
import vn.thanhthbm.jobhunter.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class EmailController {

  private final EmailService emailService;
  private final SubscriberService subscriberService;

  public EmailController(EmailService emailService, SubscriberService subscriberService) {
    this.emailService = emailService;
    this.subscriberService = subscriberService;
  }

  @GetMapping("/email")
  @ApiMessage("Send simple email aa")
  // @Scheduled(cron = "*/30 * * * * *")
  // @Transactional
  public String sendSimpleEmail() {

    this.subscriberService.sendSubscribersEmailJobs();
    return "ok";
  }
}

