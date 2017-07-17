import io.github.bonigarcia.wdm.InternetExplorerDriverManager
import io.github.bonigarcia.wdm.ChromeDriverManager
import io.github.bonigarcia.wdm.FirefoxDriverManager
import io.github.bonigarcia.wdm.PhantomJsDriverManager

import org.openqa.selenium.ie.InternetExplorerDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.phantomjs.PhantomJSDriver

import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.Dimension

environments {
  chrome {
    def options = new ChromeOptions()
    def capabilities = DesiredCapabilities.chrome()

    options.addArguments("headless", "disable-gpu")
    capabilities.setCapability(ChromeOptions.CAPABILITY, options)

    ChromeDriverManager.getInstance().setup()

    driver = {
      //new ChromeDriver(capabilities)
      new ChromeDriver()
    }
  }

  firefox {
    FirefoxDriverManager.getInstance().setup()
    driver = {
      new FirefoxDriver()
    }
  }

  ie {
    InternetExplorerDriverManager.getInstance().setup()
    driver = {
      new InternetExplorerDriver()
    }
  }

  phantomjs {
    PhantomJsDriverManager.getInstance().setup()
    driver = {
      new PhantomJSDriver()
    }
  }
  
  reportsDir = 'gebreports/'
}
