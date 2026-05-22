package orangehrm;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LeaveTest {

    WebDriver driver;
    WebDriverWait wait;
    String baseUrl = "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";

    @BeforeMethod
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(baseUrl);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // خطوة الـ Login الأساسية لدخول السيستم
        driver.findElement(By.name("username")).sendKeys("Admin");
        driver.findElement(By.name("password")).sendKeys("admin123");
        driver.findElement(By.xpath("//button[@type='submit']")).click();
    }

    @Test(priority = 1)
    public void searchLeaveListTest() {
        // 1. اضغط على Leave من القائمة الجانبية
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Leave']"))).click();

        // 2. انتظر لحد ما صفحة الـ Leave List تحمل تماماً والـ URL يستقر
        wait.until(ExpectedConditions.urlContains("viewLeaveList"));

        // 3. اضغط على زرار الـ Search مباشرة عشان يعرض كل الإجازات المسجلة
        WebElement searchButton = wait.until(ExpectedConditions
                .elementToBeClickable(By.xpath("//button[@type='submit']")));
        searchButton.click();

        // 4. التحقق (Assert): نتأكد إن جدول الإجازات أو حاوية البيانات ظهرت بنجاح في الصفحة
        WebElement resultsTable = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//div[@class='orangehrm-container']")));

        Assert.assertTrue(resultsTable.isDisplayed(), "Leave List Table is not displayed!");
        System.out.println("Search Leave List Test: Passed Successfully Without Dropdown Issues!");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}