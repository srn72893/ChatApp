package com.example.chatapp.controller;

import com.example.chatapp.entity.Message;
import com.example.chatapp.repository.ChatRepository;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWsController {
    private final ChatRepository repository;

    public ChatWsController(ChatRepository repository) {
        this.repository = repository;
    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Message send(Message message) {
        repository.save(message);

        return message;
    }
}
