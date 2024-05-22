package com.ekimtsov.telegram_bot_20.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class Config {
    @Value("${bot.name}")
    private String name;
    @Value("${bot.token}")
    private String token;
}
