package com.rm.habr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HabrApplication {
    /* todo сделать адаптив*/
    /* todo make tooltips over icons*/
    /* todo сделать бан пользователей*/
    /* todo сделать валидацию на клиенте*/
    /* todo сделать профиль пользователя, чтобы можно было узнать под чьим именем зашел*/
    /* todo сделать так в бд, чтобы нельзя было удалить пользователя, если у него остались еще публикации*/
    public static void main(String[] args) {
        SpringApplication.run(HabrApplication.class, args);
    }
}
