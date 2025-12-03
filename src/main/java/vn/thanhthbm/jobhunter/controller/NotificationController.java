package vn.thanhthbm.jobhunter.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.thanhthbm.jobhunter.domain.Notification;
import vn.thanhthbm.jobhunter.repository.NotificationRepository;
import vn.thanhthbm.jobhunter.service.NotificationService;
import vn.thanhthbm.jobhunter.util.SecurityUtil;
import vn.thanhthbm.jobhunter.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class NotificationController {
  private final NotificationService notificationService;

  @GetMapping("/notifications")
  @ApiMessage("Fetch all notifications")
  public ResponseEntity<List<Notification>> fetchAllNotifications() {
    String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
    return ResponseEntity.ok().body(this.notificationService.fetchByUserEmail(email));
  }

  @PutMapping("/notifications/{id}")
  @ApiMessage("Mark notification as read")
  public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
    this.notificationService.markAsRead(id);
    return ResponseEntity.ok().build();
  }

}
