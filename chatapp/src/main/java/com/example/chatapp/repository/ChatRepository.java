package com.example.chatapp.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.chatapp.entity.Message;
import java.time.LocalDateTime;


public interface ChatRepository extends JpaRepository<Message, Long> {

    /**
     * 最新 50 件を取得
     * @return
     */
    List<Message> findTop50ByOrderByIdDesc();

    /**
     * 差分更新（前回から増えた分だけ取得）
     * @param lastId
     * @return
     */
    List<Message> findByIdGreaterThanOrderByIdAsc(Long lastId);

    /**
     * ある程度の日時たったら DB から削除
     * 18 年前の PC に DB 乗せるので・・・
     * @param time
     */
    void deledeleteByCreatedAtBefore(LocalDateTime time);
}