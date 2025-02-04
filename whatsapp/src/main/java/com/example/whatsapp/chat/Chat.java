package com.example.whatsapp.chat;

import com.example.whatsapp.common.BaseAuditingEntity;
import com.example.whatsapp.message.Message;
import com.example.whatsapp.message.MessageState;
import com.example.whatsapp.message.MessageType;
import com.example.whatsapp.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import static jakarta.persistence.GenerationType.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chats")
@NamedQuery(
        name = ChatConstants.FIND_CHAT_BY_SENDER_ID,
        query = "select distinct c from Chat c where c.sender.id = :senderId or c.recipient.id = :senderId order by createdDate desc"
)
@NamedQuery(
        name = ChatConstants.FIND_CHAT_BY_SENDER_ID_AND_RECEIVER_ID,
        query = "select distinct c from Chat c where (c.sender.id = :senderId and c.recipient.id = :recipientId) or (c.sender.id = :recipientId and c.recipient.id = :senderId)"
)
public class Chat extends BaseAuditingEntity {
    @Id
    @GeneratedValue(strategy = UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER)
    @OrderBy("createdDate desc")
    private List<Message> messages;

    @Transient
    public String getChatName(final String senderId) {
        if (recipient.getId().equals(senderId)) return sender.getFirstName() + " " + sender.getLastName();
        return recipient.getFirstName() + " " + recipient.getLastName();
    }

    @Transient
    public long getUnreadMessages(final String senderId) {
        return messages
                .stream()
                .filter(m -> m.getReceiverId().equals(senderId))
                .filter(m -> MessageState.SENT == m.getState())
                .count();
    }

    @Transient
    public String getLastMessage() {
        if (messages != null && !messages.isEmpty()) {
            if (messages.get(0).getType() != MessageType.TEXT) return "Attachment";
            return messages.get(0).getContent();
        }
        return null;
    }

    @Transient
    public LocalDateTime getLastMessageTime() {
        if (messages != null && !messages.isEmpty()) return messages.get(0).getCreatedDate();
        return null;
    }
}
