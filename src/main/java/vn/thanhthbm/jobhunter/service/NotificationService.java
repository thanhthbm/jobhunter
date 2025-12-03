package vn.thanhthbm.jobhunter.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import vn.thanhthbm.jobhunter.domain.Notification;
import vn.thanhthbm.jobhunter.repository.NotificationRepository;

@Service
@RequiredArgsConstructor
public class NotificationService {
  private final NotificationRepository notificationRepository;
  private final SimpMessagingTemplate simpMessagingTemplate;

  public void createAndSendNotification(String email, String title, String content, String type) {
    Notification notification = new Notification();
    notification.setReceiverEmail(email);
    notification.setTitle(title);
    notification.setContent(content);
    notification.setType(type);
    notification.setRead(false);

    Notification savedNotification = notificationRepository.save(notification);

    try {
      this.simpMessagingTemplate.convertAndSend("/topic/user/" + email, savedNotification);
    } catch (Exception e) {
      System.out.println("Lỗi gửi WebSocket: " + e.getMessage());
    }
    simpMessagingTemplate.convertAndSend("/topic/user/" + email, savedNotification);
  }

  public List<Notification> fetchByUserEmail(String email) {
    return notificationRepository.findByReceiverEmailOrderByCreatedAtDesc(email);
  }

  public void markAsRead(Long id) {
    Optional<Notification> notificationOptional = notificationRepository.findById(id);
    if (notificationOptional.isPresent()) {
      notificationOptional.get().setRead(true);
      notificationRepository.save(notificationOptional.get());
    }
  }

}
