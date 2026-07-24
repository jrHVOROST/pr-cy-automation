package ru.prcy.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class BaseTest {

    @BeforeAll
    public static void setUpAll() {
        Configuration.baseUrl = "https://a.pr-cy.ru"; // Use api/dashboard base url
        Configuration.browserSize = "1920x1080";
        Configuration.pageLoadTimeout = 20000;
        Configuration.timeout = 10000;

        SelenideLogger.addListener("allure", new AllureSelenide()
                .screenshots(true)
                .savePageSource(true));
    }

    @BeforeEach
    public void authSetup() {
        // 1. Открываем базовый URL, чтобы браузер позволил подставить куку для этого домена
        Selenide.open("/");
        
        // 2. Очищаем локальное хранилище перед загрузкой
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();
        
        // 3. Пытаемся загрузить сгенерированную сессию (если она есть)
        java.io.File sessionFile = new java.io.File("target/session_data.txt");
        if (sessionFile.exists()) {
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(sessionFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\\|", 3);
                    if (parts[0].equals("COOKIE")) {
                        String[] cParts = line.split("\\|", 7);
                        if (cParts.length == 7) {
                            org.openqa.selenium.Cookie.Builder cb = new org.openqa.selenium.Cookie.Builder(cParts[1], cParts[2])
                                    .domain(cParts[3])
                                    .path(cParts[4])
                                    .isSecure(Boolean.parseBoolean(cParts[6]));
                            
                            long expiry = Long.parseLong(cParts[5]);
                            if (expiry != -1) {
                                cb.expiresOn(new java.util.Date(expiry));
                            }
                            com.codeborne.selenide.WebDriverRunner.getWebDriver().manage().addCookie(cb.build());
                        }
                    } else if (parts[0].equals("LOCALSTORAGE") && parts.length == 3) {
                        String key = parts[1];
                        String value = parts[2];
                        com.codeborne.selenide.Selenide.executeJavaScript("window.localStorage.setItem(arguments[0], arguments[1]);", key, value);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Перезагружаем страницу, чтобы куки и localstorage применились
            Selenide.refresh();
        }
    }

    @AfterEach
    public void tearDown() {
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();
        Selenide.closeWebDriver();
    }
}
