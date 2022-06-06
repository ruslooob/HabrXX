package com.rm.habr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HabrApplication {
    /* todo сделать адаптив*/
    /* todo сделать бан пользователей*/
    /* todo сделать валидацию на клиенте (на всех формах)*/
    /* todo сделать профиль пользователя, чтобы можно было узнать под чьим именем зашел*/
    /* todo сделать локализацию */
    /* todo сделать api для комментариев */
    /* todo сделать 1 модель для каждой сущности и много дтошек для нее (чтобы всегда из контроллера возвращалось только то, что нужно) */
    /* todo убрать forbidden */
    /* todo сделать роль модератора, а админа сделать в единственном экземпляре */
    public static void main(String[] args) {
        SpringApplication.run(HabrApplication.class, args);
    }
}
