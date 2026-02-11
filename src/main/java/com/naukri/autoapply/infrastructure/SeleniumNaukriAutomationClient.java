package com.naukri.autoapply.infrastructure;

import com.naukri.autoapply.config.NaukriAutomationProperties;
import com.naukri.autoapply.domain.JobSearchCriteria;
import com.naukri.autoapply.domain.NaukriCredentials;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
@ConditionalOnProperty(name = "automation.mode", havingValue = "real", matchIfMissing = true)
public class SeleniumNaukriAutomationClient implements NaukriAutomationClient {
    private static final Logger log = LoggerFactory.getLogger(SeleniumNaukriAutomationClient.class);

    private final NaukriAutomationProperties properties;

    public SeleniumNaukriAutomationClient(NaukriAutomationProperties properties) {
        this.properties = properties;
    }

    @Override
    public AutomationRunResult applyToMatchingJobs(NaukriCredentials credentials, JobSearchCriteria criteria) {
        List<String> notes = new ArrayList<>();
        ChromeOptions options = new ChromeOptions();
        if (properties.headless()) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--disable-dev-shm-usage", "--no-sandbox", "--window-size=1920,1080");

        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(properties.implicitWaitSeconds()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(properties.pageLoadTimeoutSeconds()));

        try {
            login(driver, credentials, notes);
            openSearch(driver, criteria, notes);
            int submitted = applyJobs(driver, criteria.applicationsToSubmit(), notes);
            notes.add("Automation run finished.");
            return AutomationRunResult.of(submitted, notes);
        } catch (TimeoutException ex) {
            notes.add("Timeout while automating Naukri: " + ex.getMessage());
            return AutomationRunResult.of(0, notes);
        } catch (Exception ex) {
            log.error("Naukri automation failed", ex);
            notes.add("Automation failed: " + ex.getMessage());
            return AutomationRunResult.of(0, notes);
        } finally {
            driver.quit();
        }
    }

    private void login(WebDriver driver, NaukriCredentials credentials, List<String> notes) {
        driver.get(properties.loginUrl());
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(properties.implicitWaitSeconds()));

        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[placeholder='Enter your active Email ID / Username']")));
        WebElement password = driver.findElement(By.cssSelector("input[placeholder='Enter your password']"));
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));

        username.clear();
        username.sendKeys(credentials.username());
        password.clear();
        password.sendKeys(credentials.password());
        loginButton.click();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("naukri.com/mnjuser/homepage"),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.nI-gNb-header"))
        ));

        notes.add("Successfully logged into Naukri.");
    }

    private void openSearch(WebDriver driver, JobSearchCriteria criteria, List<String> notes) {
        String keyword = criteria.role() + " " + String.join(" ", criteria.skills());
        StringBuilder query = new StringBuilder(properties.searchUrl())
                .append("?k=").append(URLEncoder.encode(keyword, StandardCharsets.UTF_8));

        if (criteria.location() != null && !criteria.location().isBlank()) {
            query.append("&l=").append(URLEncoder.encode(criteria.location(), StandardCharsets.UTF_8));
        }

        if (criteria.freshnessInDays() != null) {
            query.append("&experience=0");
        }

        driver.get(query.toString());
        notes.add("Opened search page with configured filters.");
    }

    private int applyJobs(WebDriver driver, int requiredApplications, List<String> notes) {
        int applied = 0;
        List<WebElement> applyButtons = driver.findElements(By.cssSelector("button.apply-button, button[data-job-apply='true']"));

        for (WebElement button : applyButtons) {
            if (applied >= requiredApplications) {
                break;
            }

            try {
                if (!button.isDisplayed() || !button.isEnabled()) {
                    continue;
                }

                button.click();
                maybeSubmitFinalStep(driver);
                applied++;
                notes.add("Applied to job index " + applied + ".");
                Thread.sleep(1000);
            } catch (NoSuchElementException ignored) {
                notes.add("Skipped a listing due to missing quick-apply controls.");
            } catch (Exception ex) {
                notes.add("Skipped a listing due to: " + ex.getMessage());
            }
        }

        notes.add("Submitted applications: " + applied + "/" + requiredApplications + ".");
        return applied;
    }

    private void maybeSubmitFinalStep(WebDriver driver) {
        List<WebElement> submitButtons = driver.findElements(By.cssSelector("button[data-testid='submit-application'], button[aria-label='Submit']"));
        if (!submitButtons.isEmpty() && submitButtons.get(0).isDisplayed() && submitButtons.get(0).isEnabled()) {
            submitButtons.get(0).sendKeys(Keys.ENTER);
        }
    }
}
