package com.example.whatsapp.user;

import com.example.whatsapp.chat.Chat;
import com.example.whatsapp.common.BaseAuditingEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@NamedQuery(
        name = UserConstants.FIND_USER_BY_EMAIL,
        query = "select u from User u where u.email = :email"
)
@NamedQuery(
        name = UserConstants.FIND_ALL_USERS_EXCEPT_SELF,
        query = "select u from User u where u.id != :publicId"
)
@NamedQuery(
        name = UserConstants.FIND_USER_BY_PUBLIC_ID,
        query = "select u from User u where u.id = :publicId"
)
public class User extends BaseAuditingEntity {
    private static final int LAST_ACTIVE_INTERVAL = 5;

    @Id
    private String id;

    private String firstName;

    private String lastName;

    private String email;

    private LocalDateTime lastSeen;

    @OneToMany(mappedBy = "sender")
    private List<Chat> chatsAsSender;

    @OneToMany(mappedBy = "recipient")
    private List<Chat> chatsAsRecipient;

    @Transient
    public boolean isUserOnline() {
        return lastSeen != null
                && lastSeen.isAfter(LocalDateTime.now().minusMinutes(LAST_ACTIVE_INTERVAL));
    }
}
