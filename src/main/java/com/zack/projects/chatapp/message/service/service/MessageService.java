package com.zack.projects.chatapp.message.service.service;

import com.zack.projects.chatapp.message.service.dto.SenderRecipient;
import com.zack.projects.chatapp.message.service.entity.Message;
import com.zack.projects.chatapp.message.service.repository.MessageRepository;
import com.zack.projects.chatapp.message.service.template.MessageRequestTemplate;
import com.zack.projects.chatapp.message.service.template.MessageResponseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public MessageResponseTemplate sendMessage(MessageRequestTemplate messageRequestTemplate) {

        String sender = messageRequestTemplate.getSenderRecipient().getSender();
        String recipient = messageRequestTemplate.getSenderRecipient().getRecipient();

        // TODO: rest call to user service to check if sender and recipient exist

        log.info(String.format("Creating new message with now's date"));
        Message message = new Message(messageRequestTemplate);

        messageRepository.save(message);

        // TODO: rest call to notification service to notify the recipient

        return new MessageResponseTemplate(message);

    }

    public List<MessageResponseTemplate> loadMessages(String userA, String userB) {

        SenderRecipient senderRecipient = new SenderRecipient(userA, userB);

        log.info(String.format("Loading messages between users [%s] and [%s]", userA, userB));
        return  messageRepository.findAllBySenderRecipient(userA, userB);

    }
}
