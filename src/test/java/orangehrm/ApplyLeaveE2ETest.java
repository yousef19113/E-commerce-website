package orangehrm;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

public class ApplyLeaveE2ETest {

    WebDriver driver;
    WebDriverWait wait;

    // ================== Setup ==================
    @BeforeTest
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://opensource-demo.orangehrmlive.com/");
    }

    // ================== Login Method ==================
    public void login() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username"))).sendKeys("Admin");
        driver.findElement(By.name("password")).sendKeys("admin123");
        driver.findElement(By.xpath("//button[@type='submit']")).click();
    }

    // ================== Logout Method ==================
    public void logout() {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[@class='oxd-userdropdown-tab']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Logout"))).click();
    }

    // ================== TEST CASE (سيناريو الـ My Info الآمن) ==================
    @Test(priority = 9)
    public void verifyMyInfoPageTest() throws InterruptedException {

        // 1. Login
        login();

        // 2. اضغط على My Info من القائمة الجانبية
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("My Info"))).click();

        // 3. انتظر لحد ما الـ URL يتغير ويأكد دخول صفحة البيانات الشخصية
        wait.until(ExpectedConditions.urlContains("viewPersonalDetails"));
        System.out.println("======> Successfully navigated to My Info (Personal Details) Page");

        // 4. التحقق (Assert): نتأكد إن خانة الاسم الأول (First Name) ظهرت وهي مرئية وجاهزة
        WebElement firstNameInput = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.name("firstName")));

        Assert.assertTrue(firstNameInput.isDisplayed(), "My Info Page failed to load personal details fields!");
        System.out.println("======> Verification Passed: First Name field is visible and page loaded correctly.");

        // ثانية واحدة للتهدئة قبل القفل
        Thread.sleep(1000);

        // 5. Logout
        logout();
    }

    // ================== TearDown ==================
    @AfterTest
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}