package com.example.chatapp.controller;

import com.example.chatapp.entity.Message;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.chatapp.service.ChatService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * メッセージ送信
     * @param username
     * @param content
     * @return
     */
    @PostMapping
    public Message postMessage(@RequestParam String username, @RequestParam String content) {
        return chatService.save(username, content);
    }

    /**
     * 初期表示 最新 50 件取得
     * @param listId
     * @return
     */
    @GetMapping
    public List<Message> getMessage() {
        return chatService.getRecentMessages();
    }
    
    /**
     * 差分取得（受信メッセージ更新）
     * @param listId
     * @return
     */
    @GetMapping("/new")
    public List<Message> getNewMessage(@RequestParam Long lastId) {
        return chatService.getNewMessages(lastId);
    }
    
}
