package ru.prcy.pages;

import com.codeborne.selenide.Selenide;

public class BasePage {
    
    public void refreshPage() {
        Selenide.refresh();
    }
}
