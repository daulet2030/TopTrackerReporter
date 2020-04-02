import io.github.bonigarcia.wdm.ChromeDriverManager;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;


public class ScreenshotTest {
    private WebDriver driver;
    private String screenShotPath = System.getProperty("user.dir") + "/target/screenshot.png";
    private WebDriverWait wait;
    private String token;
    private String channel;
    private String username;
    private String password;

    @Parameters({"username", "password", "token", "channel"})
    @BeforeClass
    public void init(String username, String password, String token, String channel) {
        ChromeDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1400,2400"); //width, height
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, 10);
        this.username = username;
        this.password = password;
        this.token = token;
        this.channel = channel;
    }

    @Test
    public void main() throws IOException {
        login(username, password);
        WebElement table = getReportTable();
        scrollToBottom();
        takeScreenshot(table, screenShotPath);
        sendScreenShotToSlackChannel(screenShotPath);
    }

    private WebElement getReportTable() {
        driver.navigate().to(getReportUrlForCurrentWeek());
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("reports_table_time_scroll_wrapper-scrollable_container")));
        return driver.findElement(By.className("reports-content"));
    }

    private void login(String username, String password) {
        driver.get("https://tracker.toptal.com/signin");
        driver.findElement(By.id("email")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.cssSelector("button[title='Sign In']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("side_navigation_user_menu")));
    }

    private String getReportUrlForCurrentWeek() {
        LocalDate now = LocalDate.now();
        DayOfWeek firstDayOfWeek = DayOfWeek.MONDAY;
        LocalDate startOfCurrentWeek = now.with(TemporalAdjusters.previousOrSame(firstDayOfWeek));
        DayOfWeek lastDayOfWeek = firstDayOfWeek.plus(4); // or minus(1)
        LocalDate endOfWeek = now.with(TemporalAdjusters.nextOrSame(lastDayOfWeek));
        return "https://tracker.toptal.com/app/reports?start=" + startOfCurrentWeek + "&end=" + endOfWeek + "&workers=all&projects=latest&chart=projects&table=timesheet&grouping=workers";
    }

    private void sendScreenShotToSlackChannel(String screenShotPath) {
        RestAssured.baseURI = "https://slack.com/api/";
        RestAssured.given()
                .header(new Header("content-type", "multipart/form-data"))
                .multiPart("file", new File(screenShotPath))
                .formParam("token", token)
                .formParam("channels", channel)
                .when()
                .log().body()
                .post("files.upload")
                .then()
                .log().body();
    }

    private void scrollToBottom() {
        Actions actions = new Actions(driver);
        actions.keyDown(Keys.CONTROL).sendKeys(Keys.END).perform();
    }

    private void takeScreenshot(WebElement table, String screenShotPath) throws IOException {
        File scrFile = table.getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(scrFile, new File(screenShotPath));
    }
}
