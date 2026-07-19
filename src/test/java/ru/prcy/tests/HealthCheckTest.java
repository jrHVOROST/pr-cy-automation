package ru.prcy.tests;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;
import static com.codeborne.selenide.Selenide.title;
import static org.assertj.core.api.Assertions.assertThat;

public class HealthCheckTest extends BaseTest {

    @Test
    public void openMainPageTest() {
        Selenide.open("/");
        assertThat(title()).isNotEmpty();
    }
}
