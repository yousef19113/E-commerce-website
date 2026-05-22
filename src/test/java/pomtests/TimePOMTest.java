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
import pompages.TimePage;

public class TimePOMTest {

    WebDriver driver;
    LoginPage loginPage;
    DashboardPage dashboardPage;
    TimePage timePage;

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
        timePage = new TimePage(driver);

        // تسجيل الدخول قبل كل اختبار
        loginPage.setUsername("Admin");
        loginPage.setPassword("admin123");
        loginPage.clickLogin();

        // الانتقال لصفحة Time
        dashboardPage.clickTime();
        timePage.waitForTimePageLoad();
    }

    @Test(priority = 1)
    public void verifyPendingTimesheetsDefaultDisplayTest() {
        // التحقق من أن قائمة جدول المواعيد المعلقة (Timesheets Pending Action) تعرض السجلات بشكل افتراضي
        String recordText = timePage.getRecordFoundText();
        System.out.println("======> Default Timesheets Record Found Text: " + recordText);
        
        Assert.assertTrue(recordText.contains("Found") || recordText.contains("Records"), 
                "No records displayed by default on Time page!");
    }

    @Test(priority = 2)
    public void searchTimesheetByEmployeeNameTest() throws InterruptedException {
        // بدلاً من الاسم الثابت "Nursen" الذي قد يتم حذفه من قاعدة البيانات التجريبية،
        // نقوم بجلب اسم أول موظف يظهر في الجدول بشكل ديناميكي والبحث عنه.
        String firstEmployeeBeforeSearch = timePage.getFirstRowEmployeeName();
        System.out.println("======> Dynamic Employee Name to search for: " + firstEmployeeBeforeSearch);
        
        // استخلاص الاسم الأول فقط للبحث به
        String searchKey = firstEmployeeBeforeSearch.split(" ")[0];
        System.out.println("======> Using search key: " + searchKey);
        
        timePage.enterEmployeeName(searchKey);
        timePage.clickView();

        // التحقق من ظهور النتيجة وأن الاسم الأول في الجدول أو الهيدر يطابق الموظف المبحوث عنه
        String firstEmployeeAfterSearch = timePage.getEmployeeNameAfterSearch();
        System.out.println("======> Search Result Text: " + firstEmployeeAfterSearch);
        
        Assert.assertTrue(firstEmployeeAfterSearch.toLowerCase().contains(searchKey.toLowerCase()), 
                "The search results do not match the searched employee name: " + searchKey);
    }

    @Test(priority = 3)
    public void testSearchTimesheetForInvalidEmployee() throws InterruptedException {
        // 5️⃣ البحث عن تايم شيت لموظف غير موجود بالنظام والتحقق من ظهور رسالة "Invalid"
        timePage.enterEmployeeName("InvalidEmployeeNameXYZ");
        timePage.clickView();

        String validationText = timePage.getErrorValidationMessage();
        System.out.println("======> Validation message for invalid employee search: " + validationText);
        
        Assert.assertEquals(validationText, "Invalid", "Validation message should be 'Invalid' for non-existing employee!");
        System.out.println("======> testSearchTimesheetForInvalidEmployee: Passed!");
    }

    /* 
     * 💡 Edge Case / Test Case إضافية:
     * هذا الاختبار يتحقق من ترك حقل اسم الموظف فارغاً والضغط على View.
     * فائدة الاختبار: التأكد من ظهور رسالة "Required" لحث المستخدم على تعبئة الحقل الإلزامي قبل الإرسال.
     */
    @Test(priority = 4)
    public void testSearchTimesheetWithEmptyEmployeeField() {
        // اختبار ترك الحقل فارغاً تماماً
        try {
            timePage.enterEmployeeName("");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        timePage.clickView();

        String validationText = timePage.getErrorValidationMessage();
        System.out.println("======> Validation message for empty employee field: " + validationText);
        
        Assert.assertEquals(validationText, "Required", "Validation message should be 'Required' for empty field!");
        System.out.println("======> testSearchTimesheetWithEmptyEmployeeField (Edge Case): Passed!");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
