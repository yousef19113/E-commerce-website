package pomtests;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import pompages.LoginPage;

public class LoginPOMTest {

    WebDriver driver;
    LoginPage loginPage;

    @BeforeMethod
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--lang=en-US");
        
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("intl.accept_languages", "en-US,en");
        options.setExperimentalOption("prefs", prefs);
        
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get("https://opensource-demo.orangehrmlive.com/");

        loginPage = new LoginPage(driver);
    }

    @Test(priority = 1)
    public void loginWithValidCredentialsTest() throws InterruptedException {
        // اختبار تسجيل الدخول ببيانات صحيحة تماماً
        loginPage.setUsername("Admin");
        loginPage.setPassword("admin123");
        loginPage.clickLogin();

        Thread.sleep(2000);
        Assert.assertTrue(driver.getCurrentUrl().contains("dashboard"), "Login Failed with valid credentials!");
        System.out.println("======> POM Valid Login Test: Passed!");
    }

    @Test(priority = 2)
    public void testInvalidPassword() {
        // 1️⃣ اختبار كلمة مرور خاطئة مع اسم مستخدم صحيح
        loginPage.setUsername("Admin");
        loginPage.setPassword("wrongpassword123");
        loginPage.clickLogin();

        String actualErrorMessage = loginPage.getInvalidMessage();
        Assert.assertEquals(actualErrorMessage, "Invalid credentials", "Error message for invalid password is incorrect!");
        System.out.println("======> testInvalidPassword: Passed!");
    }

    @Test(priority = 3)
    public void testInvalidUsername() {
        // 1️⃣ اختبار اسم مستخدم خاطئ مع كلمة مرور صحيحة
        loginPage.setUsername("InvalidAdminName");
        loginPage.setPassword("admin123");
        loginPage.clickLogin();

        String actualErrorMessage = loginPage.getInvalidMessage();
        Assert.assertEquals(actualErrorMessage, "Invalid credentials", "Error message for invalid username is incorrect!");
        System.out.println("======> testInvalidUsername: Passed!");
    }

    @Test(priority = 4)
    public void testEmptyFields() {
        // 1️⃣ اختبار ترك خانتي اليوزر والباسورد فارغتين
        loginPage.setUsername("");
        loginPage.setPassword("");
        loginPage.clickLogin();

        String usernameRequired = loginPage.getUsernameRequiredMessage();
        String passwordRequired = loginPage.getPasswordRequiredMessage();

        Assert.assertEquals(usernameRequired, "Required", "Username Required message is missing!");
        Assert.assertEquals(passwordRequired, "Required", "Password Required message is missing!");
        System.out.println("======> testEmptyFields: Passed!");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}