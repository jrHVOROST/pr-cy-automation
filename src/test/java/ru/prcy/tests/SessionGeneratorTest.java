package ru.prcy.tests;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class SessionGeneratorTest extends BaseTest {

    @Test
    public void generateSession() throws InterruptedException, IOException {
        // Открываем главную страницу, чтобы получить контекст домена
        Selenide.open("/");
        
        System.out.println("=====================================================");
        System.out.println("ПОЖАЛУЙСТА, АВТОРИЗУЙТЕСЬ В ОТКРЫВШЕМСЯ БРАУЗЕРЕ!");
        System.out.println("У вас есть 60 секунд. Как только введете код,");
        System.out.println("скрипт автоматически сохранит сессию.");
        System.out.println("=====================================================");

        // Ждем 60 секунд (или пока тест не завершится руками)
        Thread.sleep(60000);

        // Получаем все куки
        Set<Cookie> cookies = WebDriverRunner.getWebDriver().manage().getCookies();
        
        // Получаем LocalStorage через JS
        JavascriptExecutor js = (JavascriptExecutor) WebDriverRunner.getWebDriver();
        String localStorageLengthStr = js.executeScript("return window.localStorage.length;").toString();
        int length = Integer.parseInt(localStorageLengthStr);

        // Сохраняем в файл, который игнорируется Git
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("target/session_data.txt"))) {
            // Пишем куки
            for (Cookie c : cookies) {
                long expiry = c.getExpiry() != null ? c.getExpiry().getTime() : -1;
                writer.write(String.format("COOKIE|%s|%s|%s|%s|%d|%b\n",
                        c.getName(), c.getValue(), c.getDomain(), c.getPath(), expiry, c.isSecure()));
            }

            // Пишем LocalStorage
            for (int i = 0; i < length; i++) {
                String key = (String) js.executeScript(String.format("return window.localStorage.key(%d);", i));
                String value = (String) js.executeScript("return window.localStorage.getItem(arguments[0]);", key);
                if (value != null) {
                    // Заменяем переносы строк на пробелы, чтобы не ломать парсер
                    value = value.replace("\n", " ").replace("\r", " ");
                    writer.write(String.format("LOCALSTORAGE|%s|%s\n", key, value));
                }
            }
        }

        System.out.println("СЕССИЯ УСПЕШНО СОХРАНЕНА В target/session_data.txt!");
    }
}
