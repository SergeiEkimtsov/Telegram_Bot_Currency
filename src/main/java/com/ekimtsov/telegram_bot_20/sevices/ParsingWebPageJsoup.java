package com.ekimtsov.telegram_bot_20.sevices;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParsingWebPageJsoup implements CurrencyModeServicies {
    public static String parsingData(String data) throws Exception {

        Pattern pattern = Pattern.compile("\\d{2}\\.\\d{2}");

        Matcher matcher = pattern.matcher(data);
        if (matcher.find()) return matcher.group() + ".2024";
        else throw new Exception("No matching of data");
    }

    // public static void main(String[] args) throws MalformedURLException {
    public static String respondWeather() throws Exception {
        List<String> list = new ArrayList<>();
        list.add("Время дня:        ");
        list.add("Погодные явления: ");
        list.add("Температура:      ");
        list.add("Давление:         ");
        list.add("Отн.Влажность:    ");
        list.add("Ветер:            ");
        String respond = "";
        String url = "https://pogoda.spb.ru/";
        Document page;
        try {
            page = Jsoup.connect(url).get();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Element table = page.select("table[class=wt]").first();
        //System.out.println(table);
        Element name = table.select("tr[class=wth]").first();
        System.out.println(name);
        Elements values = table.select("tr[valign=top]");
        //System.out.println(values.size());
        String data = name.select("th[id=dt]").first().text();
//          System.out.println(data);

        respond += "Дата: " + parsingData(data) + "\n";
        //System.out.println(dataOut);

        Element line = values.get(0);
        //System.out.println(line);
        int i=0;

        for (Element s : line.select("td")) {
            respond += list.get(i)+s.text()+"\n";
            i++;
        }
        return respond;
    }
    public  static  String getAllCurrencyRates(){
        String url = "https://cbr.ru/currency_base/daily/";
        Document document;
        StringBuilder temp = new StringBuilder("Цифр.Код  Букв.Код  Единиц  Валюта  Курс\n");

        try {
            document = Jsoup.connect(url).get();
            Elements table = document.select("table[class=data]");
            Elements currentLine = table.select("tr");

            for (Element element : currentLine) {
                Elements elementsCurrency = element.select("td");

                for (Element line : elementsCurrency) {
                    temp.append(line.text()+"\t\t\t");
                }
                temp.append("\n");
            }
            System.out.println(temp);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return temp.toString();
    }
    public double getRate(String currency){
        String url = "https://cbr.ru/currency_base/daily/";
        double course = 0;

        Document document;
        try {
            document = Jsoup.connect(url).get();
            Elements table = document.select("table[class=data]");
            Elements currentLine = table.select("tr");
            for (Element element:currentLine){

                if (element.text().contains(currency)){
                    System.out.println(element.text());
                    Pattern pattern = Pattern.compile("\\d+[\",\"]\\d+");
                    Matcher matcher = pattern.matcher(element.text());
                    if (matcher.find()) {
                        String courseDouble = matcher.group().replace(",",".");
                        course = Double.parseDouble(courseDouble);
                        System.out.println(course);
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return course;
    }
}

class Test{
    public static void main(String[] args) throws Exception {
        //ParsingWebPageJsoup.respondWeather();
        //ParsingWebPageJsoup.getCourseCurrency();
        ParsingWebPageJsoup.getAllCurrencyRates();
    }
}