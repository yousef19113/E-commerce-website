package pompages; // تتبع الباكدج المعزولة الجديدة تماماً

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage {

    WebDriver driver;

    // ===== Locators =====
    @FindBy(name = "username")
    WebElement txtUsername;

    @FindBy(name = "password")
    WebElement txtPassword;

    @FindBy(xpath = "//button[@type='submit']")
    WebElement btnLogin;

    @FindBy(xpath = "//p[@class='oxd-text oxd-text--p oxd-alert-content-text']")
    WebElement invalidMessage;

    @FindBy(xpath = "//label[text()='Username']/ancestor::div[contains(@class,'oxd-input-group')]//span[contains(@class,'oxd-input-group__message')]")
    WebElement msgUsernameRequired;

    @FindBy(xpath = "//label[text()='Password']/ancestor::div[contains(@class,'oxd-input-group')]//span[contains(@class,'oxd-input-group__message')]")
    WebElement msgPasswordRequired;

    // ===== Constructor =====
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // ===== Actions =====
    public void setUsername(String username) {
        txtUsername.sendKeys(username);
    }

    public void setPassword(String password) {
        txtPassword.sendKeys(password);
    }

    public void clickLogin() {
        btnLogin.click();
    }

    public String getInvalidMessage() {
        return invalidMessage.getText();
    }

    public String getUsernameRequiredMessage() {
        return msgUsernameRequired.getText();
    }

    public String getPasswordRequiredMessage() {
        return msgPasswordRequired.getText();
    }
}