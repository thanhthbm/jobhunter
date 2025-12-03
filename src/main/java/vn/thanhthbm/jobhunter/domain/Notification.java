package vn.thanhthbm.jobhunter.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "notifications")
@Getter
@Setter
public class Notification {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String title;
  private String content;
  private String type;
  private boolean isRead = false;

  private String receiverEmail;
  private Instant createdAt;

  @PrePersist
  public void handleBeforeCreate() {
    this.createdAt = Instant.now();
  }
}
