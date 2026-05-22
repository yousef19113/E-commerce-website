package pompages;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AddEmployeePage {

    WebDriver driver;
    WebDriverWait wait;

    // ===== Locators =====
    @FindBy(name = "firstName")
    WebElement txtFirstName;

    @FindBy(name = "lastName")
    WebElement txtLastName;

    @FindBy(xpath = "//label[text()='Employee Id']/../..//input")
    WebElement txtEmployeeId;

    @FindBy(xpath = "//button[@type='submit']")
    WebElement btnSave;

    @FindBy(xpath = "//h6[text()='Personal Details']")
    WebElement headerPersonalDetails;

    @FindBy(xpath = "//span[contains(@class, 'oxd-input-field-error-message') or contains(@class, 'oxd-input-group__message')]")
    List<WebElement> listRequiredMessages;

    // ===== Constructor =====
    public AddEmployeePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    // ===== Actions =====
    public void enterFirstName(String firstName) {
        wait.until(ExpectedConditions.visibilityOf(txtFirstName)).sendKeys(firstName);
    }

    public void enterLastName(String lastName) {
        txtLastName.sendKeys(lastName);
    }

    public String getEmployeeId() {
        wait.until(ExpectedConditions.visibilityOf(txtEmployeeId));
        wait.until(d -> !txtEmployeeId.getAttribute("value").trim().isEmpty());
        return txtEmployeeId.getAttribute("value");
    }

    public void clickSave() {
        // Temporarily disable implicit wait to avoid the 10-second block if the loader is not present
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.invisibilityOfElementLocated(By.className("oxd-form-loader")));
        } catch (Exception e) {
            // Ignore if loader doesn't disappear in 5s or any other issue
        } finally {
            // Restore implicit wait to its original 10 seconds
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        }
        wait.until(ExpectedConditions.elementToBeClickable(btnSave)).click();
    }

    public String getPersonalDetailsHeader() {
        return new WebDriverWait(driver, Duration.ofSeconds(20))
            .until(ExpectedConditions.visibilityOf(headerPersonalDetails)).getText();
    }

    public boolean isRequiredMessageDisplayed() {
        return !listRequiredMessages.isEmpty() && listRequiredMessages.get(0).isDisplayed();
    }

    public String getFirstRequiredMessage() {
        return wait.until(ExpectedConditions.visibilityOf(listRequiredMessages.get(0))).getText();
    }
}
