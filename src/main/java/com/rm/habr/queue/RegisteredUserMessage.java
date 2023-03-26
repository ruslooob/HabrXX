package com.rm.habr.queue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class RegisteredUserMessage {
    private String email;
    private String login;
}
