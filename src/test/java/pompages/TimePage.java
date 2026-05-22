package pompages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TimePage {
    WebDriver driver;
    WebDriverWait wait;

    // ===== Locators =====
    @FindBy(xpath = "//input[@placeholder='Type for hints...']")
    WebElement txtEmployeeName;

    @FindBy(xpath = "//button[@type='submit']")
    WebElement btnView;

    @FindBy(xpath = "//span[contains(., 'Found') or contains(., 'Records')]")
    WebElement textRecordFound;

    @FindBy(xpath = "//div[@class='oxd-table-body']//div[@role='row'][1]//div[@role='cell'][1]")
    WebElement firstRowEmployeeName;

    @FindBy(xpath = "//span[contains(@class, 'oxd-input-group__message')]")
    WebElement errorValidationMsg;

    // ===== Constructor =====
    public TimePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    // ===== Actions =====
    public void waitForTimePageLoad() {
        wait.until(ExpectedConditions.urlContains("viewEmployeeTimesheet"));
    }

    public void enterEmployeeName(String employeeName) throws InterruptedException {
        WebElement input = wait.until(ExpectedConditions.visibilityOf(txtEmployeeName));
        input.click();
        input.sendKeys(org.openqa.selenium.Keys.CONTROL + "a");
        input.sendKeys(org.openqa.selenium.Keys.DELETE);
        input.sendKeys(employeeName);
        
        // انتظار كافٍ لظهور الاقتراحات من السيرفر
        Thread.sleep(2000);
        
        try {
            By optionLocator = By.xpath("//div[@role='listbox']//div[@role='option'] | //div[contains(@class, 'oxd-autocomplete-dropdown')]//div[@role='option'] | //div[@role='listbox']//span");
            wait.until(ExpectedConditions.elementToBeClickable(optionLocator)).click();
            System.out.println("======> Autocomplete option clicked successfully!");
        } catch (Exception e) {
            System.out.println("======> Autocomplete option click timed out or failed, trying keyboard fallback...");
            try {
                input.sendKeys(org.openqa.selenium.Keys.ARROW_DOWN);
                Thread.sleep(500);
                input.sendKeys(org.openqa.selenium.Keys.ENTER);
                System.out.println("======> Autocomplete selected via keyboard fallback!");
            } catch (Exception ex) {
                System.out.println("======> Keyboard fallback failed: " + ex.getMessage());
            }
        }
    }

    public void clickView() {
        wait.until(ExpectedConditions.elementToBeClickable(btnView)).click();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getRecordFoundText() {
        return wait.until(ExpectedConditions.visibilityOf(textRecordFound)).getText();
    }

    public String getFirstRowEmployeeName() {
        return wait.until(ExpectedConditions.visibilityOf(firstRowEmployeeName)).getText().trim();
    }

    public String getEmployeeNameAfterSearch() {
        try {
            // محاولة جلب الهيدر أولاً في حالة الانتقال لصفحة التايم شيت الفردية للموظف
            By headerLocator = By.xpath("//h6[contains(@class, 'orangehrm-main-title') or contains(normalize-space(), 'Timesheet')]");
            WebElement header = wait.until(ExpectedConditions.visibilityOfElementLocated(headerLocator));
            return header.getText().trim();
        } catch (Exception e) {
            // إذا لم يظهر الهيدر، نجلب اسم أول موظف في الجدول
            return getFirstRowEmployeeName();
        }
    }

    public String getErrorValidationMessage() {
        return wait.until(ExpectedConditions.visibilityOf(errorValidationMsg)).getText().trim();
    }
}
