package com.github.igorsuhorukov.phantomjs

@Grab(group='commons-io', module='commons-io', version='2.2')
import org.apache.commons.io.IOUtils
@Grab(group='com.github.detro', module='phantomjsdriver', version='1.2.0')
import org.openqa.selenium.*
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.phantomjs.PhantomJSDriverService
import org.openqa.selenium.remote.DesiredCapabilities
@Grab(group='com.github.igor-suhorukov', module='phantomjs-runner', version='1.0')
import com.github.igorsuhorukov.phantomjs.PhantomJsDowloader

/**
 */
public class Crawler {

    public static final java.lang.String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36"

    public static void run(String baseUrl) {

        def phantomJsPath = PhantomJsDowloader.getPhantomJsPath()

        def DesiredCapabilities settings = new DesiredCapabilities()
        settings.setJavascriptEnabled(true)
        settings.setCapability("takesScreenshot", true)
        settings.setCapability("userAgent", com.github.igorsuhorukov.phantomjs.Crawler.USER_AGENT)
        settings.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomJsPath)

        def WebDriver driver = new PhantomJSDriver(settings)


        def randomUrl = null
        def lastVisited=null
        def name=null

        boolean pass=true
        while (pass){
            try {
                randomUrl = getUrl(driver, baseUrl)
                driver.get(randomUrl)
                def titleElement = driver.findElement(By.id("title"))
                lastVisited = titleElement.findElement(By.id("profile_time_lv")).getText()
                name = titleElement.findElement(By.tagName("a")).getText()
                pass=false
            } catch (NoSuchElementException e) {
                System.out.println(e.getMessage()+". Try again.")
            }
        }
        String screenshotAs = driver.getScreenshotAs(OutputType.BASE64)
        File resultFile = File.createTempFile("phantomjs", ".html")
        OutputStreamWriter streamWriter = new OutputStreamWriter(new FileOutputStream(resultFile), "UTF-8")
        IOUtils.write("""<html><head><meta http-equiv="content-type" content="text/html; charset=UTF-8"></head><body>
                                <p>${name}</p><p>${lastVisited}</p>
                                <img alt="Embedded Image" src="data:image/png;base64,${screenshotAs}"></body>
                        </html>""", streamWriter)
        IOUtils.closeQuietly(streamWriter)

        println "html ${resultFile} created"

        driver.quit();
    }

    static String getUrl(WebDriver driver, String baseUrl) {

        driver.get(baseUrl)

        def elements =  driver.findElements(By.xpath("//div[@id='content']//a"))
        def element = elements.get((int) Math.ceil(Math.random() * elements.size()))
        String randomUrl = element.getAttribute("href")
        randomUrl.contains("catalog") ? getUrl(driver, randomUrl) : randomUrl
    }
}

Crawler.run(this.args.getAt(0))

