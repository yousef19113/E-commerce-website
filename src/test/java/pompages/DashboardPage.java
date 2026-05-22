package pompages;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DashboardPage {
    WebDriver driver;
    WebDriverWait wait;

    // ===== Locators =====
    @FindBy(xpath = "//h6[contains(@class, 'oxd-topbar-header-breadcrumb-module')]")
    WebElement dashboardHeader;

    @FindBy(xpath = "//a[contains(@href, 'viewPimModule')]")
    WebElement menuPIM;

    @FindBy(xpath = "//a[contains(@href, 'viewLeaveModule')]")
    WebElement menuLeave;

    @FindBy(xpath = "//a[contains(@href, 'viewMyDetails')]")
    WebElement menuMyInfo;

    @FindBy(xpath = "//a[contains(@href, 'viewAdminModule')]")
    WebElement menuAdmin;

    @FindBy(xpath = "//a[contains(@href, 'viewTimeModule')]")
    WebElement menuTime;

    @FindBy(xpath = "//span[@class='oxd-userdropdown-tab']")
    WebElement tabUserDropdown;

    @FindBy(xpath = "//a[contains(@href, 'logout')]")
    WebElement linkLogout;

    // ===== Constructor =====
    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    // ===== Actions =====
    public boolean isDashboardDisplayed() {
        return wait.until(ExpectedConditions.visibilityOf(dashboardHeader)).isDisplayed();
    }

    public void clickPIM() {
        wait.until(ExpectedConditions.elementToBeClickable(menuPIM)).click();
    }

    public void clickLeave() {
        wait.until(ExpectedConditions.elementToBeClickable(menuLeave)).click();
    }

    public void clickMyInfo() {
        wait.until(ExpectedConditions.elementToBeClickable(menuMyInfo)).click();
    }

    public void clickAdmin() {
        wait.until(ExpectedConditions.elementToBeClickable(menuAdmin)).click();
    }

    public void clickTime() {
        wait.until(ExpectedConditions.elementToBeClickable(menuTime)).click();
    }

    public void logout() {
        wait.until(ExpectedConditions.elementToBeClickable(tabUserDropdown)).click();
        wait.until(ExpectedConditions.elementToBeClickable(linkLogout)).click();
    }
}