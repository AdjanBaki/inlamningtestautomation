package stepDefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class SupporterRegistrationSteps {

    WebDriver driver;
    WebDriverWait wait;

    @Before
    public void setup() {
        // WebDriver initieras i Given-steget
    }

    @Given("I open the registration page in {string}")
    public void i_open_the_registration_page_in_browser(String browser) {
        if (browser.equalsIgnoreCase("chrome")) {
            io.github.bonigarcia.wdm.WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
        } else if (browser.equalsIgnoreCase("firefox")) {
            io.github.bonigarcia.wdm.WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver();
        } else {
            throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get("https://membership.basketballengland.co.uk/NewSupporterAccount");
    }

    @When("I fill in the form with:")
    public void i_fill_in_the_form_with(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> data = dataTable.asMap();

        setDateOfBirth(data.get("Date of Birth"));
        setInputById("member_firstname", data.get("First Name"));
        setInputById("member_lastname", data.get("Last Name"));
        setInputById("member_emailaddress", data.get("Email"));
        setInputById("member_confirmemailaddress", data.get("Confirm Email"));
        setInputById("signupunlicenced_password", data.get("Password"));
        setInputById("signupunlicenced_confirmpassword", data.get("Confirm Password"));

        selectRole(data.get("Choose Role"));

        if (Boolean.parseBoolean(data.getOrDefault("Accept Terms", "false"))) clickTermsCheckbox();
        if (Boolean.parseBoolean(data.getOrDefault("Accept Age Confirm", "false"))) clickAgeCheckbox();
        if (Boolean.parseBoolean(data.getOrDefault("Accept Code", "false"))) clickCodeOfEthicsCheckbox();
    }

    @And("I submit the registration form")
    public void i_submit_the_form() {
        clickConfirmAndJoin();
    }

    @Then("I should see a registration success message")
    public void i_should_see_success() {
        WebElement successMessage = waitUntilVisible(By.xpath("//h2[contains(text(),'THANK YOU FOR CREATING AN ACCOUNT')]"));
        scrollToElement(successMessage);
        Assert.assertTrue(successMessage.isDisplayed());
    }

    @Then("I should see an error for missing last name")
    public void i_should_see_error_missing_last_name() {
        WebElement error = waitUntilVisible(By.xpath("//*[contains(text(),'Last Name is required')]"));
        scrollToElement(error);
        Assert.assertTrue("❌ Missing last name error not visible", error.isDisplayed());
    }

    @Then("I should see an error for password mismatch")
    public void i_should_see_error_password_mismatch() {
        try {
            List<WebElement> errors = driver.findElements(By.xpath("//*[contains(text(),'Password did not match')]"));
            boolean found = false;
            for (WebElement e : errors) {
                if (e.isDisplayed()) {
                    scrollToElement(e);
                    found = true;
                    break;
                }
            }
            Assert.assertTrue("❌ Password mismatch error not found or not visible!", found);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("❌ Exception while checking for password mismatch error.");
        }
    }

    @Then("I should see an error for terms not accepted")
    public void i_should_see_error_terms_not_accepted() {
        WebElement errorElement = waitUntilVisible(By.cssSelector("span[data-valmsg-for='TermsAccept']"));
        Assert.assertTrue("Felmeddelande saknas eller felaktigt",
                errorElement.getText().contains("Terms and Conditions"));
    }


    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ------------------ Hjälpmetoder ------------------

    private void setInputById(String id, String value) {
        if (value != null && !value.isBlank()) {
            WebElement input = waitUntilVisible(By.id(id));
            input.clear();
            input.sendKeys(value);
        }
    }

    private void setDateOfBirth(String dob) {
        if (dob == null || dob.isBlank()) return;
        WebElement dateInput = waitUntilVisible(By.id("dp"));
        dateInput.click();
        dateInput.clear();
        dateInput.sendKeys(dob);
        dateInput.sendKeys(Keys.TAB); // stänger datepicker
    }

    private void selectRole(String role) {
        if (role == null || role.isBlank()) return;
        List<WebElement> labels = driver.findElements(By.cssSelector("label[for^='signup_basketballrole_']"));
        for (WebElement label : labels) {
            if (label.getText().trim().equalsIgnoreCase(role)) {
                scrollToElement(label);
                label.click();
                return;
            }
        }
        throw new NoSuchElementException("Role not found: " + role);
    }

    private void clickTermsCheckbox() {
        WebElement box = waitUntilVisible(By.xpath("//*[@id='signup_form']/div[11]/div/div[2]/div[1]/label/span[3]"));
        scrollToElement(box);
        box.click();
    }

    private void clickAgeCheckbox() {
        WebElement box = waitUntilVisible(By.xpath("//*[@id='signup_form']/div[11]/div/div[2]/div[2]/label/span[3]"));
        scrollToElement(box);
        box.click();
    }

    private void clickCodeOfEthicsCheckbox() {
        WebElement box = waitUntilVisible(By.xpath("//*[@id='signup_form']/div[11]/div/div[7]/label/span[3]"));
        scrollToElement(box);
        box.click();
    }

    private void clickConfirmAndJoin() {
        WebElement confirmBtn = waitUntilVisible(By.xpath("//*[@id='signup_form']/div[12]/input"));
        scrollToElement(confirmBtn);
        confirmBtn.click();
    }

    private void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    private WebElement waitUntilVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
}
