package pompages;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MyInfoPage {

    WebDriver driver;
    WebDriverWait wait;

    // ===== Locators =====
    @FindBy(name = "firstName")
    WebElement txtFirstName;

    // ===== Constructor =====
    public MyInfoPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    // ===== Actions =====
    public void waitForPersonalDetailsLoad() {
        wait.until(ExpectedConditions.urlContains("viewPersonalDetails"));
    }

    public boolean isFirstNameFieldDisplayed() {
        return wait.until(ExpectedConditions.visibilityOf(txtFirstName)).isDisplayed();
    }
}
