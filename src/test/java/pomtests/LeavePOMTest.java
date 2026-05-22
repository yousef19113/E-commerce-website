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
import pompages.LeavePage;
import pompages.MyInfoPage;

public class LeavePOMTest {

    WebDriver driver;
    LoginPage loginPage;
    DashboardPage dashboardPage;
    LeavePage leavePage;
    MyInfoPage myInfoPage;

    @BeforeMethod
    public void setup() throws InterruptedException {
        // إجبار المتصفح على الفتح باللغة الإنجليزية لتفادي مشكلة اللغة الإسبانية
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--lang=en-US");

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("intl.accept_languages", "en-US,en");
        options.setExperimentalOption("prefs", prefs);

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");

        loginPage = new LoginPage(driver);
        dashboardPage = new DashboardPage(driver);
        leavePage = new LeavePage(driver);
        myInfoPage = new MyInfoPage(driver);

        // تسجيل الدخول
        loginPage.setUsername("Admin");
        loginPage.setPassword("admin123");
        loginPage.clickLogin();

        // زقة وقتية مريحة لتستقر الصفحة والقائمة الجانبية تماماً
        Thread.sleep(3000);
    }

    @Test(priority = 1)
    public void searchLeaveListTest() {
        dashboardPage.clickLeave();

        // انتظر حتى يتم تحميل الصفحة تماماً
        leavePage.waitForLeaveListLoad();

        // اضغط على زر Search لعرض قائمة الإجازات
        leavePage.clickSearch();

        // التحقق من ظهور جدول النتائج بنجاح
        Assert.assertTrue(leavePage.isResultsTableDisplayed(), "Leave List Table is not displayed!");
        System.out.println("Search Leave List Test (POM): Passed Successfully!");
    }

    @Test(priority = 2)
    public void verifyMyInfoPageTest() throws InterruptedException {
        // الانتقال إلى صفحة My Info من القائمة الجانبية
        dashboardPage.clickMyInfo();

        // الانتظار حتى تحميل صفحة البيانات الشخصية
        myInfoPage.waitForPersonalDetailsLoad();
        System.out.println("======> Successfully navigated to My Info (Personal Details) Page (POM)");

        // التحقق من ظهور حقل الاسم الأول
        Assert.assertTrue(myInfoPage.isFirstNameFieldDisplayed(),
                "My Info Page failed to load personal details fields!");
        System.out.println("======> Verification Passed: First Name field is visible (POM).");

        // انتظار ثانية للتهدئة قبل تسجيل الخروج
        Thread.sleep(1000);

        // تسجيل الخروج
        dashboardPage.logout();
    }

    @Test(priority = 3)
    public void testSearchLeaveWithInvalidDateRange() {
        // 4️⃣ البحث عن إجازات بنطاق تاريخ غير صحيح (تاريخ البدء بعد تاريخ الانتهاء) والتحقق من رسالة الخطأ
        dashboardPage.clickLeave();
        leavePage.waitForLeaveListLoad();

        // إدخال تاريخ بداية بعد تاريخ النهاية
        leavePage.enterFromDate("2026-12-30");
        leavePage.enterToDate("2026-12-01");
        leavePage.clickSearch();

        // التحقق من ظهور رسالة الخطأ المناسبة للتواريخ المتداخلة
        String errorMsg = leavePage.getDateErrorMessage();
        System.out.println("======> Date Range Error Message: " + errorMsg);
        
        Assert.assertTrue(errorMsg.toLowerCase().contains("should be after") || 
                          errorMsg.toLowerCase().contains("required") || 
                          errorMsg.toLowerCase().contains("valid date") || 
                          errorMsg.toLowerCase().contains("format"), 
                "Expected validation error message for invalid date range is not displayed!");
        System.out.println("======> testSearchLeaveWithInvalidDateRange: Passed!");
    }

    /* 
     * 💡 Edge Case / Test Case إضافية:
     * هذا الاختبار يتحقق من كتابة تاريخ بصيغة غير مقبولة تماماً (نص بدلاً من أرقام) للتأكد من رفض النظام لها.
     * فائدة الاختبار: التأكد من قوة التحقق من المدخلات (Input Validation) في حقول التواريخ لمنع الأخطاء البرمجية.
     */
    @Test(priority = 4)
    public void testInvalidDateFormatText() {
        dashboardPage.clickLeave();
        leavePage.waitForLeaveListLoad();

        leavePage.enterFromDate("InvalidDateText");
        leavePage.clickSearch();

        String errorMsg = leavePage.getDateErrorMessage();
        System.out.println("======> Invalid Format Error Message: " + errorMsg);
        
        Assert.assertTrue(errorMsg.toLowerCase().contains("should be a valid date") || errorMsg.toLowerCase().contains("format"), 
                "System did not reject completely invalid date text format!");
        System.out.println("======> testInvalidDateFormatText (Edge Case): Passed!");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}