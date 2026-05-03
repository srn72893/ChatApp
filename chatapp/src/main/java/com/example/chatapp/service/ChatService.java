package com.example.chatapp.service;

import java.time.LocalDateTime;
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
        //username, content, time stamp, 既読状態 セット
        message.setUsername(username);
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());
        message.setRead(false);

        //DB 保存 + 100 回に 1 回頻度で過去 30 日分のログを消す
        Message saved = chatRepository.save(message);
        if (saved.getId() % 100 == 0) {
            cleanupOldMessages();
        }
        
        return saved;
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

    /**
     * 読み込み時に既読化する
     */
    public void markAllAsRead() {
        //未読ログを取得
        List<Message> unread = chatRepository.findByReadFalse();
        //null チェック
        if (unread.isEmpty()) return;
        //既読化
        unread.forEach(m -> m.setRead(true));

        chatRepository.saveAll(unread);
    }

    /**
     * 古い PC に DB 乗ってるので 30 日おきにログ削除
     */
    public void cleanupOldMessages() {
        chatRepository.deleteByCreatedAtBefore(LocalDateTime.now().minusDays(30));
    }
 }
