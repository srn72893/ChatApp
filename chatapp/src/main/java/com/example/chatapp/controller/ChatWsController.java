package com.example.chatapp.controller;

import com.example.chatapp.entity.Message;
import com.example.chatapp.repository.ChatRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWsController {
    private final ChatRepository repository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public ChatWsController(ChatRepository repository) {
        this.repository = repository;
    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Message send(Message message) {
        message.setRead(false);
        return repository.save(message);
    }

    /**
     * 既読通知
     */
    public void notifyReadUpdate() {
        messagingTemplate.convertAndSend("/topic/read", "updated");
    }
}
