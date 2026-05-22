package orangehrm;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginTest {

    WebDriver driver;
    String baseUrl = "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";

    @BeforeMethod
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(baseUrl);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @Test(priority = 1)
    public void doLoginWithInvalidCredentials() {
        driver.findElement(By.name("username")).sendKeys("Admin");
        driver.findElement(By.name("password")).sendKeys("admin12345");
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        String actualErrorMessage = driver.findElement(By.xpath("//p[text()='Invalid credentials']")).getText();
        Assert.assertEquals(actualErrorMessage, "Invalid credentials");
        System.out.println("Invalid Login Test: Passed");
    }

    @Test(priority = 2)
    public void loginTestWithValidCredentials() {
        driver.findElement(By.name("username")).sendKeys("Admin");
        driver.findElement(By.name("password")).sendKeys("admin123");
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        String actualTitle = driver.findElement(By.xpath("//h6[text()='Dashboard']")).getText();
        Assert.assertEquals(actualTitle, "Dashboard");
        System.out.println("Valid Login Test: Passed");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}