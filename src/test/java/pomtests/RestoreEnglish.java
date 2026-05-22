package pomtests;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class RestoreEnglish {

    @Test
    public void restoreLanguageToEnglish() {
        System.out.println("======> Starting language restoration process...");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--lang=en-US");

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("intl.accept_languages", "en-US,en");
        options.setExperimentalOption("prefs", prefs);

        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        try {
            System.out.println("======> Logging in to OrangeHRM...");
            driver.get("https://opensource-demo.orangehrmlive.com/");
            
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username"))).sendKeys("Admin");
            driver.findElement(By.name("password")).sendKeys("admin123");
            driver.findElement(By.xpath("//button[@type='submit']")).click();

            System.out.println("======> Navigating to Localization settings page directly...");
            driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/admin/localization");

            System.out.println("======> Locating Language dropdown...");
            // يدعم البحث بـ Language أو Idioma (بالإسبانية)
            WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//label[text()='Language' or text()='Idioma']/ancestor::div[contains(@class,'oxd-input-group')]//div[@class='oxd-select-text-input']")
            ));
            dropdown.click();

            System.out.println("======> Selecting English (US) from options...");
            WebElement englishOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@role='listbox']//*[contains(text(), 'English')]")
            ));
            englishOption.click();

            System.out.println("======> Clicking Save button...");
            driver.findElement(By.xpath("//button[@type='submit']")).click();

            // انتظر ظهور رسالة التنبيه بالنجاح
            WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'oxd-toast')]")
            ));
            System.out.println("======> Success Toast Message: " + toast.getText());
            System.out.println("======> System Language has been successfully changed back to English!");

            // انتظر ثانيتين للتأكيد
            Thread.sleep(2000);
        } catch (Exception e) {
            System.err.println("======> An error occurred during restoration: " + e.getMessage());
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}
