package com.example.chatapp.controller;

import com.example.chatapp.entity.Message;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.chatapp.service.ChatService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@Controller
public class ChatController {
    private final ChatService chatService;
    private final ChatWsController chatWsController;

    public ChatController(ChatService chatService, ChatWsController chatWsController) {
        this.chatService = chatService;
        this.chatWsController = chatWsController;
    }

    /**
     * 未ログインなら login.html リダイレクト
     * ログイン済なら index.html へ
     * @param session
     * @return
     */
    @GetMapping("/")
    public String index(HttpSession session) {
        if (session.getAttribute("username") == null) {
            return "redirect:/login.html";
        }
        return "forward:/index.html";
    }

    /**
     * メッセージ送信
     * @param username
     * @param content
     * @return
     */
    @PostMapping("/messages")
    public void postMessage(
                @RequestParam String content, HttpSession session) {
        String username = (String) session.getAttribute("username");
        chatService.save(username, content);
    }

    /**
     * 初期表示 最新 50 件取得
     * @param listId
     * @return
     */
    @GetMapping("/messages")
    @ResponseBody
    public List<Message> getMessage() {
        return chatService.getRecentMessages();
    }
    
    /**
     * 差分取得（受信メッセージ更新）
     * @param listId
     * @return
     */
    @GetMapping("/messages/new")
    @ResponseBody
    public List<Message> getNewMessage(@RequestParam Long lastId) {
        return chatService.getNewMessages(lastId);
    }

    /**
     * ユーザーをセッション保存
     * @param session
     * @return
     */
    @GetMapping("/me")
    @ResponseBody
    public String me(HttpSession session) {
        return (String) session.getAttribute("username");
    }

    /**
     * 既読つける
     */
    @PostMapping("/messages/read")
    @ResponseBody
    public void markAsRead() {
        chatService.markAllAsRead();
        chatWsController.notifyReadUpdate();
    }
    
}
