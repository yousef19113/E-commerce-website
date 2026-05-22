package pompages;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AdminPage {
    WebDriver driver;
    WebDriverWait wait;

    // ===== Locators =====
    @FindBy(xpath = "//label[text()='Username']/ancestor::div[contains(@class,'oxd-input-group')]//input")
    WebElement txtUsername;

    @FindBy(xpath = "//label[text()='Employee Name']/ancestor::div[contains(@class,'oxd-input-group')]//input")
    WebElement txtEmployeeName;

    @FindBy(xpath = "//button[@type='submit']")
    WebElement btnSearch;

    @FindBy(xpath = "//button[contains(@class, 'oxd-button') and (text()='Reset' or contains(., 'Reset'))]")
    WebElement btnReset;

    @FindBy(xpath = "//span[contains(., 'Found') or contains(., 'Records')]")
    WebElement textRecordFound;

    @FindBy(xpath = "//div[@class='oxd-table-body']//div[@role='row'][1]//div[@role='cell'][2]")
    WebElement firstRowUsername;

    @FindBy(xpath = "//button[contains(., 'Add') or contains(normalize-space(), 'Add')]")
    WebElement btnAdd;

    @FindBy(xpath = "//span[contains(@class, 'oxd-input-group__message')]")
    List<WebElement> listRequiredMessages;

    // ===== Constructor =====
    public AdminPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    // ===== Actions =====
    public void waitForAdminPageLoad() {
        wait.until(ExpectedConditions.urlContains("viewSystemUsers"));
    }

    public void enterUsername(String username) {
        wait.until(ExpectedConditions.visibilityOf(txtUsername)).clear();
        txtUsername.sendKeys(username);
    }

    public void enterEmployeeName(String employeeName) throws InterruptedException {
        wait.until(ExpectedConditions.visibilityOf(txtEmployeeName)).clear();
        txtEmployeeName.sendKeys(employeeName);
        // انتظار بسيط لظهور التلميحات واختيارها إن وجدت أو للتهدئة
        Thread.sleep(1500);
    }

    public void clickSearch() {
        wait.until(ExpectedConditions.elementToBeClickable(btnSearch)).click();
        // انتظار بسيط لتحميل الجدول بعد البحث
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void clickReset() {
        wait.until(ExpectedConditions.elementToBeClickable(btnReset)).click();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getRecordFoundText() {
        return wait.until(ExpectedConditions.visibilityOf(textRecordFound)).getText();
    }

    public String getFirstRowUsername() {
        return wait.until(ExpectedConditions.visibilityOf(firstRowUsername)).getText().trim();
    }

    public void clickAdd() {
        wait.until(ExpectedConditions.elementToBeClickable(btnAdd)).click();
    }

    public void clickSave() {
        // نستخدم زر submit الفعلي
        wait.until(ExpectedConditions.elementToBeClickable(btnSearch)).click();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isRequiredMessageDisplayed() {
        return !listRequiredMessages.isEmpty() && listRequiredMessages.get(0).isDisplayed();
    }

    public String getFirstRequiredMessageText() {
        return wait.until(ExpectedConditions.visibilityOf(listRequiredMessages.get(0))).getText();
    }
}
