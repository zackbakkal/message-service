package com.zack.projects.chatapp.message.service.service;

import com.zack.projects.chatapp.message.service.dto.SenderRecipient;
import com.zack.projects.chatapp.message.service.entity.Message;
import com.zack.projects.chatapp.message.service.exception.UserNameNotFoundException;
import com.zack.projects.chatapp.message.service.repository.MessageRepository;
import com.zack.projects.chatapp.message.service.template.MessageRequestTemplate;
import com.zack.projects.chatapp.message.service.template.MessageResponseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private RestTemplate restTemplate;

    public MessageResponseTemplate sendMessage(MessageRequestTemplate messageRequestTemplate) throws UserNameNotFoundException {

        String sender = messageRequestTemplate.getSenderRecipient().getSender();
        String recipient = messageRequestTemplate.getSenderRecipient().getRecipient();

        boolean userAIsRegistered =
                restTemplate.getForObject("http://USER-SERVICE/users/registered/" + sender, Boolean.class);

        boolean userBIsRegistered =
                restTemplate.getForObject("http://USER-SERVICE/users/registered/" + recipient, Boolean.class);

        if(userAIsRegistered && userBIsRegistered) {
            log.info(String.format("Creating new message with now's date"));
            Message message = new Message(messageRequestTemplate);

            messageRepository.save(message);

            // TODO: rest call to notification service to notify the recipient

            return new MessageResponseTemplate(message);
        }

        StringBuilder message = new StringBuilder();
        message.append(!userAIsRegistered ? String.format("User [%s] does not exist\n", sender).toString() : "");
        message.append(!userBIsRegistered ? String.format("User [%s] does not exist\n", recipient).toString() : "");

        throw new UserNameNotFoundException(message.toString());

    }

    public List<MessageResponseTemplate> loadMessages(String userA, String userB) throws UserNameNotFoundException {

        boolean userAIsRegistered =
                restTemplate.getForObject("http://USER-SERVICE/users/registered/" + userA, Boolean.class);

        boolean userBIsRegistered =
                restTemplate.getForObject("http://USER-SERVICE/users/registered/" + userB, Boolean.class);

        if(userAIsRegistered && userBIsRegistered) {
            log.info(String.format("Loading messages between users [%s] and [%s]", userA, userB));
            return  messageRepository.findAllBySenderRecipient(userA, userB);
        }

        StringBuilder message = new StringBuilder();
        message.append(!userAIsRegistered ? String.format("User [%s] does not exist\n", userA).toString() : "");
        message.append(!userBIsRegistered ? String.format("User [%s] does not exist\n", userB).toString() : "");

        throw new UserNameNotFoundException(message.toString());

    }

}
