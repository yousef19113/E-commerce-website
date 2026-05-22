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

public class PIMTest {

    WebDriver driver;
    WebDriverWait wait;
    String baseUrl = "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";

    // متغير ثابت لحفظ الـ ID اللي السيرفر هيكتبه للموظف ديناميكياً عشان نستخدمه في البحث
    static String savedEmployeeId;

    @BeforeMethod
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(baseUrl);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // تجهيز الـ Explicit Wait للاستخدام جوه التستات
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // خطوة الـ Login الأساسية لدخول السيستم
        driver.findElement(By.name("username")).sendKeys("Admin");
        driver.findElement(By.name("password")).sendKeys("admin123");
        driver.findElement(By.xpath("//button[@type='submit']")).click();
    }

    @Test(priority = 1)
    public void addEmployeeTest() {
        // انتظر ذكياً لحد ما زرار الـ PIM يظهر في القائمة واضغط عليه
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='PIM']"))).click();

        // Click on Add Employee
        driver.findElement(By.xpath("//a[text()='Add Employee']")).click();

        // Enter First Name & Last Name
        driver.findElement(By.name("firstName")).sendKeys("Radha");
        driver.findElement(By.name("lastName")).sendKeys("Gupta");

        // لقطة ذكية: بنقرا الـ ID الديناميكي
        savedEmployeeId = driver.findElement(By.xpath("//label[text()='Employee Id']/../..//input")).getAttribute("value");
        System.out.println("======> Saved New Employee ID dynamically: " + savedEmployeeId);

        // Click Save Button
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        // انتظر ذكياً لحد ما صفحة تفاصيل الموظف تظهر والتكست يتغير
        WebElement personalDetailsHeader = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//h6[text()='Personal Details']")));

        Assert.assertEquals(personalDetailsHeader.getText(), "Personal Details");
        System.out.println("Add Employee Test: Passed");
    }

    @Test(priority = 2)
    public void searchEmployeeByNameTest() {
        // انتظر ذكياً لحد ما زرار الـ PIM يظهر في القائمة واضغط عليه
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='PIM']"))).click();

        // Click on Employee List
        driver.findElement(By.xpath("//a[text()='Employee List']")).click();

        // خانة الـ Employee Name
        WebElement nameSearchField = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//label[text()='Employee Name']/../..//input[@placeholder='Type for hints...']")));
        nameSearchField.sendKeys("Radha");

        // Click Search Button
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        // بنستنى الرسالة اللي بتقول لقينا سجلات تظهر وتتحدث
        WebElement recordFoundText = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//span[contains(text(), 'Record Found') or contains(., 'Found')]")));

        String actualMessage = recordFoundText.getText();
        System.out.println("Result Message: " + actualMessage);

        Assert.assertTrue(actualMessage.contains("Record Found") || actualMessage.contains("Found"));
        System.out.println("Search Employee Test: Passed");
    }

    @Test(priority = 3)
    public void searchEmployeeByIdTest() {
        // 1. ندخل على القائمة PIM والـ Employee List
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='PIM']"))).click();
        driver.findElement(By.xpath("//a[text()='Employee List']")).click();

        // 2. نروح لخانة الـ Employee ID
        WebElement idSearchField = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//label[text()='Employee Id']/../..//input")));

        // بنبحث بالـ ID الفعلي الديناميكي
        idSearchField.sendKeys(savedEmployeeId);

        // 3. نضغط Search
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        // 4. الحل الأكيد: بنحدد مكان الخلية الأول
        By idCellLocator = By.xpath("//div[@role='rowgroup']//div[@role='row'][1]/div[@role='cell'][2]");

        // بنقول للـ wait: استنى لحد ما الـ ID الفعلي يظهر كـ تكست جوه الخلية دي بالذات عشان ما تقراش خانة فاضية!
        wait.until(ExpectedConditions.textToBePresentInElementLocated(idCellLocator, savedEmployeeId));

        // دلوقتي اقرأ وأنت مطمن 100%
        String actualID = driver.findElement(idCellLocator).getText();
        System.out.println("======> Verified Employee ID in Table: " + actualID);

        // 5. التحقق
        Assert.assertEquals(actualID, savedEmployeeId, "The Employee ID in the table doesn't match!");
        System.out.println("Search Employee By ID Test: Passed");
    }
    @Test(priority = 4, dependsOnMethods = {"addEmployeeTest"})
    public void deleteEmployeeTest() {
        // 1. ندخل على القائمة PIM والـ Employee List
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='PIM']"))).click();

        // 2. البحث بالـ ID الديناميكي اللي حفظناه
        WebElement idSearchField = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//label[text()='Employee Id']/../..//input")));
        idSearchField.sendKeys(savedEmployeeId);

        // 3. نضغط Search
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        // 4. استنى لحد ما سلة المهملات الخاصة بالموظف تظهر في الجدول واضغط عليها
        WebElement deleteIcon = wait.until(ExpectedConditions
                .elementToBeClickable(By.xpath("//div[@role='rowgroup']//div[@role='row'][1]//i[@class='oxd-icon bi-trash']")));
        deleteIcon.click();

        // 5. زرار التأكيد جوه الـ Pop-up
        WebElement confirmDeleteBtn = wait.until(ExpectedConditions
                .elementToBeClickable(By.xpath("//button[contains(., 'Yes, Delete')]")));
        confirmDeleteBtn.click();

        // 6. التحقق الذكي من الـ Toast Message
        WebElement toastMessage = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//div[contains(@class,'oxd-toast')]")));

        String toastText = toastMessage.getText();
        System.out.println("======> Toast Message After Delete: " + toastText);

        Assert.assertTrue(toastText.contains("Successfully Deleted") || toastText.contains("Success"),
                "Delete notification did not appear!");

        // 🚨 الحركة الصايعة: استنى لحد ما الـ Toast تختفي خالص من الشاشة والـ UI يروق
        wait.until(ExpectedConditions.invisibilityOf(toastMessage));

        // 7. خطوة تأكيدية (الاختبار العكسي): نضغط سيرش تاني على نظافة
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        // استنى لحد ما رسالة الجدول الفاضي تظهر
        WebElement recordFoundText = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//span[contains(text(), 'No Records Found') or contains(., 'Found')]")));

        System.out.println("Final Search Status: " + recordFoundText.getText());
        Assert.assertTrue(recordFoundText.getText().contains("No Records Found"), "Employee was not deleted from DB!");
        System.out.println("Delete Employee Test: Passed Dynamically and Safely!");
    }
    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}