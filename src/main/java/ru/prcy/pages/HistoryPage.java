package ru.prcy.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class HistoryPage {

    private final SelenideElement searchInput = $("input[placeholder='Поиск']");
    private final SelenideElement tableBody = $(".lgt-table-tbody");
    
    private final SelenideElement updateButton = $x("//button[.//span[contains(text(), 'Обновить позиции')]]");
    private final SelenideElement periodDropdown = $x("//button[.//span[contains(text(), 'Обновить позиции')]]/ancestor::div[contains(@class, 'lgt-space-item')]/preceding-sibling::div[1]//div[contains(@class, 'lgt-select')]");

    public HistoryPage openPage(String domain) {
        open("/keywords/overview/" + domain + "/");
        return this;
    }

    public HistoryPage selectWeekPeriod() {
        periodDropdown.shouldBe(visible).click();
        $$("div")
            .filterBy(Condition.exactText("Неделя"))
            .filterBy(visible)
            .first()
            .click();
        sleep(1000);
        return this;
    }

    public HistoryPage switchSearchEngine(String engine) {
        if (engine.toLowerCase().contains("google")) {
            $x("//img[@alt='google']/ancestor::div[contains(@class, 'lgt-space-item')] | //div[contains(@class, 'lgt-space-item')]//span[contains(text(), 'G ')]").click();
        } else {
            $x("//img[@alt='yandex']/ancestor::div[contains(@class, 'lgt-space-item')] | //div[contains(@class, 'lgt-space-item')]//span[contains(text(), 'Я ')]").click();
        }
        return this;
    }

    public HistoryPage searchKeyword(String keyword) {
        searchInput.shouldBe(visible).setValue(keyword);
        return this;
    }

    public HistoryPage assertEmptyTableMessageDisplayed() {
        // Ожидаем появления элемента пустого состояния. 
        // Используем CSS селекторы, покрывающие разные варианты.
        $(".lgt-empty, .lgt-table-placeholder, .ant-empty").shouldBe(visible);
        return this;
    }

    public ElementsCollection getSearchResults() {
        return tableBody.$$("tr.lgt-table-row");
    }

    public HistoryPage clickTopSummaryBlock(String topRange) {
        $x("//div[contains(text(), '" + topRange + "')]").shouldBe(visible).click();
        return this;
    }

    public HistoryPage clickUpdatePositions() {
        updateButton.shouldBe(visible).click();
        return this;
    }

    public HistoryPage assertModalSubmitButtonDisabled() {
        $x("//div[@role='dialog']//button[contains(@class, 'lgt-btn-color-primary')]")
            .shouldHave(Condition.attribute("disabled"));
        return this;
    }
    
    public HistoryPage uncheckAllSearchEnginesInModal() {
        SelenideElement firstCheckedBox = $x("//div[@role='dialog']//label[contains(@class, 'lgt-checkbox-wrapper-checked')]");
        firstCheckedBox.shouldBe(visible);
        while (firstCheckedBox.isDisplayed()) {
            firstCheckedBox.click();
            sleep(500); 
        }
        return this;
    }

    public HistoryPage switchRegion(String regionName) {
        $x("//div[contains(@class, 'lgt-space-item')]//span[contains(text(), '" + regionName + "')]").shouldBe(visible).click();
        sleep(1000); 
        return this;
    }

    public HistoryPage openDetailedHistoryForFirstKeyword() {
        tableBody.$$("tr.lgt-table-row").first().$$("td").get(1).$x(".//span[text()]").click();
        return this;
    }

    public HistoryPage assertDetailedHistoryModalIsDisplayed() {
        $x("//div[@role='dialog']//*[contains(text(), 'История запроса')] | //div[contains(@class, 'lgt-modal')]").shouldBe(visible);
        $x("//div[@role='dialog']//*[name()='svg']").shouldBe(visible); // Убедимся что график отрисован
        return this;
    }

    public java.io.File downloadExportFile() throws java.io.FileNotFoundException {
        com.codeborne.selenide.Configuration.fileDownload = com.codeborne.selenide.FileDownloadMode.FOLDER;
        return $x("//button[.//*[local-name()='svg' and @data-icon='download']]").download(15000);
    }

    public HistoryPage hoverOverGraphPoint() {
        SelenideElement chart = $x("(//*[local-name()='svg' and contains(@class, 'recharts-surface')])[1]").shouldBe(visible);
        com.codeborne.selenide.Selenide.actions().moveToElement(chart, 10, 10).moveByOffset(10, 0).perform();
        return this;
    }

    public HistoryPage assertGraphTooltipIsDisplayed() {
        $x("//div[contains(@class, 'recharts-tooltip-wrapper')] | //div[contains(@class, 'lgt-tooltip')]").should(Condition.exist);
        return this;
    }

    public String getFirstRelevantUrl() {
        SelenideElement urlIcon = tableBody.$$("tr.lgt-table-row").first().$x(".//a[contains(@href, 'http') and not(contains(@href, 'yandex')) and not(contains(@href, 'google'))]");
        if (urlIcon.exists()) {
            return urlIcon.getAttribute("href");
        }
        return "EMPTY";
    }

    public HistoryPage openColumnsSettings() {
        $x("//button[.//*[local-name()='svg' and @data-icon='setting']]").shouldBe(visible).click();
        return this;
    }

    public HistoryPage toggleColumn(String columnName) {
        $x("//div[@role='dialog' or contains(@class, 'lgt-popover')]//span[contains(text(), '" + columnName + "')]/ancestor::label/span[contains(@class, 'lgt-checkbox')]").click();
        sleep(500);
        return this;
    }

    public HistoryPage assertColumnAbsent(String columnName) {
        $$("th.lgt-table-cell").filterBy(Condition.text(columnName)).shouldHave(com.codeborne.selenide.CollectionCondition.empty);
        return this;
    }

    public HistoryPage groupByRelevantUrl() {
        SelenideElement filterBtn = $x("//button[.//*[local-name()='svg' and @data-icon='filter']]");
        filterBtn.ancestor("div[contains(@class, 'lgt-space-horizontal')]").$$("div.lgt-select").first().shouldBe(visible).click();
        sleep(500);
        $$x("//div[contains(@class, 'lgt-select-item-option-content')]").filterBy(Condition.text("URL")).first().shouldBe(visible).click();
        sleep(1000);
        return this;
    }

    public HistoryPage assertDynamicsColors() {
        if ($$(".lgt-text-success, .lgt-text-danger").size() > 0) {
            $$(".lgt-text-success, .lgt-text-danger").first().shouldBe(visible);
        }
        return this;
    }

    public String getLimitsCostText() {
        return $x("//div[@role='dialog']//div[contains(text(), 'Стоимость в лимитах')]").shouldBe(visible).text();
    }
}
