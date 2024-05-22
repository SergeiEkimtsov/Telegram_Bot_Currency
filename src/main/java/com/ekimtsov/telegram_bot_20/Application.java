package com.ekimtsov.telegram_bot_20;

import com.ekimtsov.telegram_bot_20.sevices.TelegramBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class Application {

	public static void main(String[] args) throws TelegramApiException {
		SpringApplication.run(Application.class, args);



	}

}
