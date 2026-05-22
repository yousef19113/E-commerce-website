package pompages;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LeavePage {

    WebDriver driver;
    WebDriverWait wait;

    // ===== Locators =====
    @FindBy(xpath = "//button[@type='submit']")
    WebElement btnSearch;

    @FindBy(xpath = "//div[@class='orangehrm-container']")
    WebElement resultsTable;

    @FindBy(xpath = "//label[text()='From Date']/ancestor::div[contains(@class,'oxd-input-group')]//input")
    WebElement txtFromDate;

    @FindBy(xpath = "//label[text()='To Date']/ancestor::div[contains(@class,'oxd-input-group')]//input")
    WebElement txtToDate;

    @FindBy(xpath = "//span[contains(@class, 'oxd-input-group__message')]")
    WebElement dateErrorMessage;

    @FindBy(xpath = "//span[contains(., 'Found') or contains(., 'Records')]")
    WebElement textNoRecordsFound;

    // ===== Constructor =====
    public LeavePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    // ===== Actions =====
    public void waitForLeaveListLoad() {
        wait.until(ExpectedConditions.urlContains("viewLeaveList"));
    }

    public void clickSearch() {
        wait.until(ExpectedConditions.elementToBeClickable(btnSearch)).click();
    }

    public boolean isResultsTableDisplayed() {
        return wait.until(ExpectedConditions.visibilityOf(resultsTable)).isDisplayed();
    }

    public void enterFromDate(String date) {
        wait.until(ExpectedConditions.visibilityOf(txtFromDate));
        txtFromDate.sendKeys(org.openqa.selenium.Keys.CONTROL + "a");
        txtFromDate.sendKeys(org.openqa.selenium.Keys.DELETE);
        txtFromDate.sendKeys(date);
    }

    public void enterToDate(String date) {
        wait.until(ExpectedConditions.visibilityOf(txtToDate));
        txtToDate.sendKeys(org.openqa.selenium.Keys.CONTROL + "a");
        txtToDate.sendKeys(org.openqa.selenium.Keys.DELETE);
        txtToDate.sendKeys(date);
    }

    public String getDateErrorMessage() {
        return wait.until(ExpectedConditions.visibilityOf(dateErrorMessage)).getText().trim();
    }

    public String getNoRecordsFoundMessage() {
        return wait.until(ExpectedConditions.visibilityOf(textNoRecordsFound)).getText().trim();
    }
}
