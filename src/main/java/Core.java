import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Created by A147882 on 28/03/2022.
 */
public class Core {

    private static WebDriver driver;

    public static WebDriver getDriver() {
        if (driver != null) {
            return driver;
        } else {
            throw new IllegalStateException("Browser non ancora inizializzato");
        }
    }

    public static void startBrowser() {
        if (driver != null) {
            teardownBrowser();
        }
        if (driver == null) {
            String chromeDriverPath = System.getProperty("webdriver.chrome.driver");
            if (chromeDriverPath == null) {
                System.setProperty("webdriver.chrome.driver", readDataProperty("chrome.driver.path"));
            }
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("enable-automation");
            chromeOptions.addArguments("user-agent=\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36\"");
            driver = new ChromeDriver(chromeOptions);
            driver.manage().window().maximize();
            driver.manage().deleteAllCookies();
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        } else {
            throw new IllegalStateException("Browser già inizializzato");
        }
    }

    public static void teardownBrowser() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    protected static String readDataProperty(String propName) {
        String propVal = null;

        try {
            String pathFolder = System.getProperty("properties_path");
            if (pathFolder == null) {
                pathFolder = "./src/main/resources/";
            }
            String pathPropertyFile = pathFolder + "data.properties";
            InputStream input = new FileInputStream(new File(pathPropertyFile.replace("file:", "")));
            Properties prop = new Properties();
            prop.load(input);
            propVal = prop.getProperty(propName);
        } catch (Exception myEParam) {
            // errore nella lettura dei dati dal file di property
            myEParam.printStackTrace();
        }

        return propVal;
    }

    public static void goToTheFork() throws Exception {
        Core.getDriver().get(readDataProperty("url"));

        Thread.sleep(4000);
        try {
            Core.getDriver().findElement(By.xpath("//button[@id='_evidon-accept-button']")).click();
        } catch (Exception e) {
            //nessun popup dei cookie
        }
    }

    public static void doLogin() throws Exception {
        Thread.sleep(2000);
        Core.getDriver().findElement(By.xpath("//button[@data-testid='user-space']")).click();

        Thread.sleep(3000);
        Core.getDriver().findElement(By.xpath("//input[@id='identification_email']")).sendKeys(readDataProperty("email"));

        Thread.sleep(1000);
        Core.getDriver().findElement(By.xpath("//button[@data-testid='checkout-submit-email']")).click();

        Thread.sleep(7000);
        Core.getDriver().findElement(By.xpath("//input[@data-testid='password-input']")).sendKeys(readDataProperty("psw"));

        Thread.sleep(2000);
        Core.getDriver().findElement(By.xpath("//button[@data-testid='submit-password']")).click();
    }

    public static void goToPersonalInfo() throws Exception {
        Thread.sleep(3000);
        Core.getDriver().findElement(By.xpath("//section[@id='USER_SPACE_FIRST_PANEL']//ul[@role='listbox']/li//button[@aria-controls='user-space-user-information']")).click();
    }

    public static String urlCall(String stringUrl) throws Exception {

        URL url = new URL(stringUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {

            conn.setRequestMethod("GET");

            String resp = readFullyAsString(conn.getInputStream(), "UTF-8");

            return resp;

        } finally {
            conn.disconnect();
        }

    }

    private static String readFullyAsString(InputStream inputStream, String encoding) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toString(encoding);
    }

}
