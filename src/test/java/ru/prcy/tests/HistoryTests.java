package ru.prcy.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.prcy.pages.HistoryPage;

import static org.assertj.core.api.Assertions.assertThat;

public class HistoryTests extends BaseTest {

    private HistoryPage historyPage;
    
    public static final String TEST_DOMAIN = "vsedoma-family.ru";

    @BeforeEach
    public void setUp() {
        historyPage = new HistoryPage();
        historyPage.openPage(TEST_DOMAIN);
    }

    @Test
    public void H_01_selectWeekPeriodTest() {
        historyPage.selectWeekPeriod()
                   .assertPeriodSelected("Неделя");
    }

    @Test
    public void H_03_switchSearchEngineTest() {
        historyPage.switchSearchEngine("Google")
                   .assertSearchEngineSelected("Google");
    }

    @Test
    public void H_04_filterByKeywordTest() {
        // Ищем ключ, который точно есть, или просто проверяем применение фильтра
        historyPage.searchKeyword("Пряники")
                   .assertAllRowsContainKeyword("Пряники");
    }

    @Test
    public void H_09_searchNonExistentKeywordTest() {
        historyPage.searchKeyword("zxy123")
                   .assertEmptyTableMessageDisplayed();
    }

    @Test
    public void H_10_filterByTopSummaryTest() {
        historyPage.clickTopSummaryBlock("4-10")
                   .assertTopSummaryApplied("4-10");
    }

    @Test
    public void H_14_updateButtonDisabledWhenEmptyCheckboxesTest() {
        historyPage.clickUpdatePositions()
                   .uncheckAllSearchEnginesInModal()
                   .assertModalSubmitButtonDisabled();
    }

    @Test
    public void H_02_switchRegionTest() {
        historyPage.switchRegion("Санкт-Петербург")
                   .assertRegionSelected("Санкт-Петербург");
    }

    @Test
    public void H_05_detailedHistoryKeywordTest() {
        historyPage.openDetailedHistoryForFirstKeyword()
                   .assertDetailedHistoryModalIsDisplayed();
    }

    @Test
    public void H_06_exportDataTest() throws java.io.FileNotFoundException {
        java.io.File exportedFile = historyPage.downloadExportFile();
        assertThat(exportedFile).isNotNull();
        assertThat(exportedFile.length()).isGreaterThan(0);
    }

    @Test
    public void H_07_graphTooltipsTest() {
        historyPage.openDetailedHistoryForFirstKeyword()
                   .assertDetailedHistoryModalIsDisplayed()
                   .hoverOverGraphPoint()
                   .assertGraphTooltipIsDisplayed();
    }

    @Test
    public void H_08_relevantUrlCorrectnessTest() {
        String url = historyPage.getFirstRelevantUrl();
        if (!"EMPTY".equals(url)) {
            assertThat(url).isNotNull().contains(TEST_DOMAIN);
        }
    }

    @Test
    public void H_11_customizeColumnsTest() {
        historyPage.openColumnsSettings()
                   .toggleColumn("URL")
                   .assertColumnAbsent("URL");
    }

    @Test
    public void H_12_groupByRelevantUrlTest() {
        historyPage.groupByRelevantUrl()
                   .assertGroupByUrlApplied();
    }

    @Test
    public void H_13_dynamicsColorIndicationTest() {
        historyPage.assertDynamicsColors();
    }

    @Test
    public void H_15_successfulUpdateTest() {
        historyPage.clickUpdatePositions()
                   .clickConfirmUpdatePositions()
                   .assertUpdateModalClosed();
    }

    @Test
    public void H_16_limitsDeductionTest() {
        historyPage.clickUpdatePositions();
        String cost = historyPage.getLimitsCostText();
        assertThat(cost).contains("Стоимость в лимитах");
        historyPage.clickConfirmUpdatePositions()
                   .assertUpdateModalClosed();
    }
}
