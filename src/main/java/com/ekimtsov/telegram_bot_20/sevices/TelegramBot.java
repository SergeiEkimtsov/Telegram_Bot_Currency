package com.ekimtsov.telegram_bot_20.sevices;

import com.ekimtsov.telegram_bot_20.config.Config;
//import com.ekimtsov.telegram_bot_20.dao.UserRepository;
//import com.ekimtsov.telegram_bot_20.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.*;

@Component
public class TelegramBot  extends TelegramLongPollingBot{
  //  @Autowired
    //private UserRepository userRepository;
    private  final CurrencyModeServicies servicies =  CurrencyModeServicies.getInstance();
    private Config config;
    private final String HELP_TEXT = "Telegram bot can obtain currency rates to RUB";

    public TelegramBot(Config config) {
        this.config = config;
        List<BotCommand> commandList = new ArrayList<>();
        commandList.add(new BotCommand("/start", "To start bot session"));
        commandList.add(new BotCommand("/all_currency", "list of all currency rates"));
        commandList.add(new BotCommand("/help", "help"));

        try {
            this.execute(new SetMyCommands(commandList));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    HashMap<Long, String> mapOriginal = new HashMap<>();
    HashMap<Long, String> mapTarget = new HashMap<>();

    public static void main(String[] args) {
    }


    @Override
    public String getBotUsername() {
        return config.getName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

//    @Override
//    public String getBotUsername() {
//        return config.getName();
//    }
//
//    @Override
//    public String getBotToken() {
//        return config.getToken();
//    }
//
//    @Override
//    public void onUpdateReceived(Update update) {
//
//    }
//public static void main(String[] args) throws Exception {
//    TelegramBot bot = new TelegramBot(new DefaultBotOptions());
//    bot.execute(SendMessage.builder().chatId("858197192").text(ParsingWebPageJsoup.respondWeather()).build());
//    System.out.println("!!!");
//}

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage()) {
            Message message = update.getMessage();
            long chatId = message.getChatId();
            String action = message.getText();
            //registerUser(message);
            switch (action) {
                case "/start":
                    getStart(chatId);
                    break;
                case "/all_currency":
                   String respond = ParsingWebPageJsoup.getAllCurrencyRates();
                   executeSendMessage(chatId,respond);
                   break;
                case "/help": executeSendMessage(chatId, HELP_TEXT);
                break;
                default:
                    Optional<Double> optional = parsingMessage(action);
                    if (optional.isPresent()){
                        double amount = optional.get();
                        getCurrencyRate(mapOriginal.get(chatId), mapTarget.get(chatId), amount, chatId);
                    }
            }
        }
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            long chatId = callbackQuery.getMessage().getChatId();
            String [] data = callbackQuery.getData().split(":");
            String currencyOriginal= mapOriginal.get(chatId);
            String currencyTarget = mapTarget.get(chatId);

            switch (data[0]){
                case "ORIGINAL": currencyOriginal = data[1];
                break;
                case "TARGET":   currencyTarget = data[1];
                break;
            }
            if (!currencyOriginal.equals(mapOriginal.get(chatId)) | !currencyTarget.equals(mapTarget.get(chatId))) {

                if (!mapOriginal.get(chatId).equals(currencyOriginal)) {
                    mapOriginal.put(chatId, currencyOriginal);
                }

                if (!mapTarget.get(chatId).equals(currencyTarget)) {
                    mapTarget.put(chatId, currencyTarget);
                }
                List<List<InlineKeyboardButton>> buttons = getButtons(chatId, mapOriginal, mapTarget);
                executeEditMessage(callbackQuery, buttons);
            }
        }
    }
    private void getCurrencyRate(String originalCurrency, String targetCurrency, double amount, long chatId) {
        String currency = originalCurrency+"-"+targetCurrency;
        String respond;
        switch (currency){
            case "RUB-USD":
                respond = amount + " " + originalCurrency + " = "
                        + String.format("%1$,.1f", amount/servicies.getRate("USD"))
                        + " " + targetCurrency;
                executeSendMessage(chatId,respond);
                break;
            case "USD-RUB":
                respond = amount + " " + originalCurrency + " = "
                        + String.format("%1$,.1f", amount*servicies.getRate("USD"))
                        + " " + targetCurrency;
                executeSendMessage(chatId,respond);
                break;
            case "RUB-EUR":
                respond = amount + " " + originalCurrency + " = "
                        + String.format("%1$,.1f", amount/servicies.getRate("EUR"))
                        + " " + targetCurrency;
                executeSendMessage(chatId,respond);
                break;
            case "EUR-RUB":
                respond = amount + " " + originalCurrency + " = "
                        + String.format("%1$,.1f", amount*servicies.getRate("EUR"))
                        + " " + targetCurrency;
                executeSendMessage(chatId,respond);
                break;
        }
        if (originalCurrency.equals(targetCurrency)){
            respond = amount + " " + originalCurrency + " = "
                    + String.format("%1$,.1f", amount)
                    +" "+ targetCurrency;
            executeSendMessage(chatId,respond);
        }

    }
    public Optional<Double> parsingMessage(String number) {
        try {
            return Optional.of(Double.parseDouble(number));
        }
        catch (Exception e){
            return Optional.empty();
        }
    }

    public String getCurrency(String saved, Currency current){
        return saved.equals(String.valueOf(current))?current+"✅":String.valueOf(current);
    }
        void getStart (long chatId){

        mapOriginal.put(chatId, String.valueOf(Currency.USD));
        mapTarget.put(chatId, String.valueOf(Currency.USD));

        List<List<InlineKeyboardButton>> buttons = getButtons(chatId, mapOriginal,mapTarget);
            try {
                execute(SendMessage
                        .builder()
                        .text("Original currency            Target currency")
                        .chatId(String.valueOf(chatId))
                        .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                        .build());
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    public List<List<InlineKeyboardButton>> getButtons(long chatId,
                                                       HashMap<Long, String> mapOriginal,
                                                       HashMap<Long, String> mapTarget ){
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        //long chatId = callbackQuery.getMessage().getChatId();
        for (Currency currency:Currency.values()) {
            buttons.add(Arrays.asList(
                    InlineKeyboardButton
                            .builder()
                            .text(getCurrency(mapOriginal.get(chatId),currency))
                            .callbackData("ORIGINAL:" + currency)
                            .build(),

                    InlineKeyboardButton
                            .builder()
                            .text(getCurrency(mapTarget.get(chatId),currency))
                            .callbackData("TARGET:" + currency)
                            .build()
            ));
        }
        return buttons;
    }
    public void  executeEditMessage(CallbackQuery callbackQuery, List<List<InlineKeyboardButton>> buttons){
        long chatId = callbackQuery.getMessage().getChatId();
        try {
            execute(EditMessageReplyMarkup
                    .builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                    .build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    public void executeSendMessage(long chatId, String message){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
//    public void registerUser(Message msg) {
//        // if (userRepository.findById(msg.getChatId()).isEmpty()){
//
//        var chatId = msg.getChatId();
//        var chat = msg.getChat();
//        User user = new User();
//        user.setChatId(chatId);
//        //user.setName(chat.getFirstName());
//        user.setFirstName(chat.getFirstName());
//        user.setLastName(chat.getLastName());
//        user.setUserName(chat.getUserName());
//        user.setTimeRegistered(new Timestamp(System.currentTimeMillis()));
//        userRepository.save(user);
//    }
}
