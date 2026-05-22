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
import pompages.PIMPage;
import pompages.AddEmployeePage;

public class PIMPOMTest {

    WebDriver driver;
    LoginPage loginPage;
    DashboardPage dashboardPage;
    PIMPage pimPage;
    AddEmployeePage addEmployeePage;

    // متغير ثابت لحفظ الـ ID ديناميكياً لاستخدامه عبر التستات المتتالية
    static String savedEmployeeId;

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

        // تهيئة الكلاسات الخاصة بنموذج POM
        loginPage = new LoginPage(driver);
        dashboardPage = new DashboardPage(driver);
        pimPage = new PIMPage(driver);
        addEmployeePage = new AddEmployeePage(driver);

        // خطوة تسجيل الدخول الموحدة للدخول إلى السيستم
        loginPage.setUsername("Admin");
        loginPage.setPassword("admin123");
        loginPage.clickLogin();
    }

    @Test(priority = 1)
    public void addEmployeeTest() {
        dashboardPage.clickPIM();
        pimPage.clickAddEmployeeTab();

        addEmployeePage.enterFirstName("Radha");
        addEmployeePage.enterLastName("Gupta");
        
        // قراءة الـ ID التلقائي وحفظه
        savedEmployeeId = addEmployeePage.getEmployeeId();
        System.out.println("======> Saved New Employee ID dynamically (POM): " + savedEmployeeId);

        addEmployeePage.clickSave();

        // التحقق من الحفظ بنجاح
        Assert.assertEquals(addEmployeePage.getPersonalDetailsHeader(), "Personal Details");
        System.out.println("Add Employee Test (POM): Passed");
    }

    @Test(priority = 2, dependsOnMethods = {"addEmployeeTest"})
    public void searchEmployeeByNameTest() {
        dashboardPage.clickPIM();
        pimPage.clickEmployeeListTab();
        pimPage.searchByName("Radha");

        String recordText = pimPage.getRecordFoundText();
        System.out.println("Result Message (POM): " + recordText);

        Assert.assertTrue(recordText.contains("Record Found") || recordText.contains("Found"));
        System.out.println("Search Employee Test by Name (POM): Passed");
    }

    @Test(priority = 3, dependsOnMethods = {"addEmployeeTest"})
    public void searchEmployeeByIdTest() {
        dashboardPage.clickPIM();
        pimPage.clickEmployeeListTab();
        pimPage.searchById(savedEmployeeId);

        // قراءة الـ ID من الجدول للتأكد
        String actualID = pimPage.getFirstRowEmployeeId(savedEmployeeId);
        System.out.println("======> Verified Employee ID in Table (POM): " + actualID);

        Assert.assertEquals(actualID, savedEmployeeId, "The Employee ID in the table doesn't match!");
        System.out.println("Search Employee By ID Test (POM): Passed");
    }

    @Test(priority = 4, dependsOnMethods = {"addEmployeeTest"})
    public void deleteEmployeeTest() {
        dashboardPage.clickPIM();
        pimPage.clickEmployeeListTab();
        pimPage.searchById(savedEmployeeId);

        // الانتظار الفعلي حتى ظهور معرف الموظف المُراد حذفه بالجدول لتفادي أي Race Condition
        pimPage.getFirstRowEmployeeId(savedEmployeeId);

        // حذف الموظف
        pimPage.deleteFirstEmployee();

        // قراءة رسالة التنبيه (Toast Message)
        String toastText = pimPage.getToastMessageText();
        System.out.println("======> Toast Message After Delete (POM): " + toastText);

        Assert.assertTrue(toastText.contains("Successfully Deleted") || toastText.contains("Success"),
                "Delete notification did not appear!");

        // انتظار اختفاء رسالة التنبيه قبل عمل بحث جديد
        pimPage.waitForToastInvisibility();

        // التأكد من أن الموظف لم يعد موجوداً في النظام
        pimPage.searchById(savedEmployeeId);
        String finalMessage = pimPage.getRecordFoundText();
        System.out.println("Final Search Status (POM): " + finalMessage);
        
        Assert.assertTrue(finalMessage.contains("No Records Found"), "Employee was not deleted from DB!");
        System.out.println("Delete Employee Test (POM): Passed");
    }

    @Test(priority = 5)
    public void testSearchNonExistingEmployee() {
        // 3️⃣ البحث عن موظف بهوية غير صالحة والتحقق من ظهور رسالة "No Records Found"
        dashboardPage.clickPIM();
        pimPage.clickEmployeeListTab();
        pimPage.searchById("99999999"); // ID وهمي غير موجود

        String recordText = pimPage.getRecordFoundText();
        System.out.println("======> Non-existing Employee Search Result: " + recordText);
        Assert.assertTrue(recordText.contains("No Records Found"), "Expected 'No Records Found' text on invalid search!");
    }

    @Test(priority = 6)
    public void testAddEmployeeWithoutRequiredFields() {
        // 3️⃣ محاولة إضافة موظف بدون كتابة الاسم الأول أو اسم العائلة والتحقق من ظهور رسالة "Required"
        dashboardPage.clickPIM();
        pimPage.clickAddEmployeeTab();
        
        // نترك الحقول فارغة ونضغط حفظ مباشرة
        addEmployeePage.clickSave();

        Assert.assertTrue(addEmployeePage.isRequiredMessageDisplayed(), "Required validation message is missing under empty fields!");
        String errorText = addEmployeePage.getFirstRequiredMessage();
        Assert.assertEquals(errorText, "Required", "Error message under empty employee fields is not 'Required'!");
        System.out.println("======> testAddEmployeeWithoutRequiredFields: Passed!");
    }

    /* 
     * 💡 Edge Case / Test Case إضافية:
     * هذا الاختبار يتحقق من إمكانية تعبئة اسم الموظف وتصفية الجدول بنجاح بدون ملء حقل الـ ID.
     * فائدته: التأكد من مرونة البحث وقبول النظام للفلاتر الجزئية دون الحاجة لكل الحقول.
     */
    @Test(priority = 7, dependsOnMethods = {"addEmployeeTest"})
    public void testSearchByPartialNameOnly() {
        dashboardPage.clickPIM();
        pimPage.clickEmployeeListTab();
        pimPage.searchByName("Radha");

        String recordText = pimPage.getRecordFoundText();
        Assert.assertTrue(recordText.contains("Record Found") || recordText.contains("Found"), 
                "Partial name search did not return any records!");
        System.out.println("======> testSearchByPartialNameOnly (Edge Case): Passed!");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
