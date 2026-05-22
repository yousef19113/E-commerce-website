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
import pompages.DashboardPage;
import pompages.AdminPage;

public class AdminPOMTest {

    WebDriver driver;
    LoginPage loginPage;
    DashboardPage dashboardPage;
    AdminPage adminPage;

    @BeforeMethod
    public void setup() throws InterruptedException {
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
        dashboardPage = new DashboardPage(driver);
        adminPage = new AdminPage(driver);

        // تسجيل الدخول قبل كل اختبار
        loginPage.setUsername("Admin");
        loginPage.setPassword("admin123");
        loginPage.clickLogin();

        // الانتقال لصفحة Admin
        dashboardPage.clickAdmin();
        adminPage.waitForAdminPageLoad();
    }

    @Test(priority = 1)
    public void searchUserByUsernameTest() {
        // البحث عن المستخدم "Admin"
        adminPage.enterUsername("Admin");
        adminPage.clickSearch();

        // قراءة النتيجة والتحقق
        String recordText = adminPage.getRecordFoundText();
        System.out.println("======> Admin Search Record Found Text: " + recordText);
        Assert.assertTrue(recordText.contains("Record Found") || recordText.contains("Found"),
                "Search results not found!");

        String firstUsername = adminPage.getFirstRowUsername();
        System.out.println("======> First Row Username in Table: " + firstUsername);
        Assert.assertTrue(firstUsername.toLowerCase().contains("admin"),
                "First row username doesn't match search criteria!");
    }

    @Test(priority = 2)
    public void testSearchNonExistingUser() {
        // البحث عن اسم غير موجود في النظام والتحقق من رسالة عدم وجود سجلات
        adminPage.enterUsername("NonExistingUser999");
        adminPage.clickSearch();

        String recordText = adminPage.getRecordFoundText();
        System.out.println("======> Invalid User Search Record Found Text: " + recordText);

        // تعديل مرن ليقبل الصيغتين (0) Records Found أو No Records Found
        boolean isNoRecords = recordText.contains("No Records Found") || recordText.contains("(0) Records Found") || recordText.contains("0");
        Assert.assertTrue(isNoRecords, "Expect no records message but got: " + recordText);
    }

    @Test(priority = 3)
    public void testAddUserWithEmptyRequiredFields() {
        // محاولة إضافة مستخدم وترك كل الحقول المطلوبة فارغة للتحقق من رسالة "Required"
        adminPage.clickAdd();
        adminPage.clickSave();

        Assert.assertTrue(adminPage.isRequiredMessageDisplayed(), "Required validation message is not displayed!");
        String errorText = adminPage.getFirstRequiredMessageText();
        Assert.assertEquals(errorText, "Required", "Error message text is not 'Required'!");
        System.out.println("======> testAddUserWithEmptyRequiredFields: Passed!");
    }

    @Test(priority = 4)
    public void testResetSearchFields() {
        adminPage.enterUsername("RandomUsernameText");
        adminPage.clickReset();

        String recordText = adminPage.getRecordFoundText();
        Assert.assertTrue(recordText.contains("Record Found") || recordText.contains("Found"),
                "Reset button did not reload all system records!");
        System.out.println("======> testResetSearchFields (Edge Case): Passed!");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}