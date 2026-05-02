package com.example.chatapp.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.chatapp.repository.ChatRepository;
import com.example.chatapp.entity.Message;

@Service
public class ChatService {
    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    /**
     * メッセージを DB に保存
     * @param username
     * @param content
     * @return
     */
    public Message save(String username, String content) {
        Message message = new Message();
        message.setUsername(username);
        message.setContent(content);
        return chatRepository.save(message);
    }

    /**
     * 最新 50 件のメッセージを返す
     * @return
     */
    public List<Message> getRecentMessages() {
        List<Message> list = chatRepository.findTop50ByOrderByIdDesc();
        //Collections.reverse : 引数に与えた List の順番を逆にする
        Collections.reverse(list);  //古い順
        return list;
    }

    /**
     * 差分取得
     * @param lastId
     * @return
     */
    public List<Message> getNewMessages(Long lastId) {
        return chatRepository.findByIdGreaterThanOrderByIdAsc(lastId);
    }
 }
