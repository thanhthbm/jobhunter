package vn.thanhthbm.jobhunter.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.thanhthbm.jobhunter.domain.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
  List<Notification> findByReceiverEmailOrderByCreatedAtDesc(String receiverEmail);
}
