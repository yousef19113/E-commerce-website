package pompages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PIMPage {

    WebDriver driver;
    WebDriverWait wait;

    // ===== Locators =====
    @FindBy(xpath = "//a[text()='Add Employee']")
    WebElement tabAddEmployee;

    @FindBy(xpath = "//a[text()='Employee List']")
    WebElement tabEmployeeList;

    @FindBy(xpath = "//label[text()='Employee Name']/../..//input[@placeholder='Type for hints...']")
    WebElement txtSearchEmployeeName;

    @FindBy(xpath = "//label[text()='Employee Id']/../..//input")
    WebElement txtSearchEmployeeId;

    @FindBy(xpath = "//button[@type='submit']")
    WebElement btnSearch;

    @FindBy(xpath = "//span[contains(text(), 'Record Found') or contains(., 'Found')]")
    WebElement textRecordFound;

    @FindBy(xpath = "//div[@role='rowgroup']//div[@role='row'][1]/div[@role='cell'][2]")
    WebElement cellFirstEmployeeId;

    @FindBy(xpath = "//div[@role='rowgroup']//div[@role='row'][1]//i[@class='oxd-icon bi-trash']")
    WebElement iconDeleteFirstEmployee;

    @FindBy(xpath = "//button[contains(., 'Yes, Delete')]")
    WebElement btnConfirmDelete;

    @FindBy(xpath = "//div[contains(@class,'oxd-toast')]")
    WebElement toastMessage;

    // ===== Constructor =====
    public PIMPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    // ===== Actions =====
    public void clickAddEmployeeTab() {
        wait.until(ExpectedConditions.elementToBeClickable(tabAddEmployee)).click();
    }

    public void clickEmployeeListTab() {
        wait.until(ExpectedConditions.elementToBeClickable(tabEmployeeList)).click();
    }

    public void searchByName(String name) {
        wait.until(ExpectedConditions.visibilityOf(txtSearchEmployeeName)).clear();
        txtSearchEmployeeName.sendKeys(name);
        btnSearch.click();
        waitForSearchToComplete();
    }

    public void searchById(String id) {
        wait.until(ExpectedConditions.visibilityOf(txtSearchEmployeeId)).clear();
        txtSearchEmployeeId.sendKeys(id);
        btnSearch.click();
        waitForSearchToComplete();
    }

    private void waitForSearchToComplete() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getRecordFoundText() {
        return wait.until(ExpectedConditions.visibilityOf(textRecordFound)).getText();
    }

    public String getFirstRowEmployeeId(String expectedId) {
        By idCellLocator = By.xpath("//div[@role='rowgroup']//div[@role='row'][1]/div[@role='cell'][2]");
        wait.until(ExpectedConditions.textToBePresentInElementLocated(idCellLocator, expectedId));
        return cellFirstEmployeeId.getText();
    }

    public void deleteFirstEmployee() {
        wait.until(ExpectedConditions.elementToBeClickable(iconDeleteFirstEmployee)).click();
        wait.until(ExpectedConditions.elementToBeClickable(btnConfirmDelete)).click();
    }

    public String getToastMessageText() {
        return wait.until(ExpectedConditions.visibilityOf(toastMessage)).getText();
    }

    public void waitForToastInvisibility() {
        wait.until(ExpectedConditions.invisibilityOf(toastMessage));
    }
}
