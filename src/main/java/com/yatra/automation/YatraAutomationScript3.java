package com.yatra.automation;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class YatraAutomationScript3 {
	public static void main(String[] args) throws InterruptedException, TimeoutException {
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--- disable- notifications--");

		// Step 1: Launch the browser. WebDriver is a interface. WebDriver is a class.
		// Object should always be created in child class.
		// this is loosely couple.
		WebDriver wd = new ChromeDriver(chromeOptions);
		WebDriverWait wait = new WebDriverWait(wd, Duration.ofSeconds(20)); // Synchronizing the webDriver

		// Step 2: Load the page for us
		wd.get("https://www.yatra.com");

		// Maximize the browser window
		wd.manage().window().maximize();

	
		closePopUp(wait);

		clickOnDepartureDate(wait);

		WebElement currentMonthWebElement = selectTheMonthFromCalender(wait, 0); // current month
		WebElement nextMonthWebElement = selectTheMonthFromCalender(wait, 1); // next month

		Thread.sleep(2000);

		String lowestPriceForCurrentMonth = getMeLowestPrice(currentMonthWebElement);
		String lowestPriceForNextMonth = getMeLowestPrice(nextMonthWebElement);
		System.out.println(lowestPriceForCurrentMonth + "  \n   " + lowestPriceForNextMonth);

		compareTwoMonthsPrice(lowestPriceForCurrentMonth, lowestPriceForNextMonth);
	}

	public static void clickOnDepartureDate(WebDriverWait wait) {
		By departureDateButtonLocator = By.xpath("//div[@aria-label=\"Departure Date inputbox\" and @role=\"button\"]");

		WebElement departureDateButton = wait
				.until(ExpectedConditions.elementToBeClickable(departureDateButtonLocator));

		departureDateButton.click();
	}

	public static void closePopUp(WebDriverWait wait) {
		// Check for the popup!!
		By popUpLocator = By.xpath("//div[contains(@class ,\"style_popup\")][1]");
		WebElement popUpElement = wait.until(ExpectedConditions.visibilityOfElementLocated(popUpLocator));
		WebElement crossButton = popUpElement.findElement(By.xpath(".//img[@alt=\"cross\"]"));
		crossButton.click();
	}

	public static String getMeLowestPrice(WebElement monthWebElement) {
		By priceLocator = By.xpath(".//span[contains(@class, \"custom-day-content \")]"); // chaining of web element

		List<WebElement> junePriceList = monthWebElement.findElements(priceLocator);
		// How do you ensure that your scripts are non flaky?? or synchronized

		int lowestPrice = Integer.MAX_VALUE;
		WebElement priceElement = null;
		for (WebElement price : junePriceList) {
			System.out.println(price.getText());
			// Tell which is the lowest price.
			String priceString = price.getText();
			if (priceString.length() > 0) {
				priceString = priceString.replace("₹", "").replace(",", "");
				// System.out.println(priceString);

				// Find the lowest number
				// Convert the string value into Integer

				int priceInt = Integer.parseInt(priceString);
				if (priceInt < lowestPrice) {
					lowestPrice = priceInt;
					priceElement = price;

				}
			}
		}

		System.out.println(lowestPrice);
		WebElement dataElement = priceElement.findElement(By.xpath(".//../.."));
		System.out.println(dataElement.getAttribute("aria-label"));
		String result = dataElement.getAttribute("aria-label") + "----Price is Rs" + lowestPrice;
		return result;
	}

	public static WebElement selectTheMonthFromCalender(WebDriverWait wait, int index) {
		By calenderMonthsLocator = By.xpath("//div[@class=\"react-datepicker__month-container\"]");
		// List<WebElement> calenderMonthsWebElement
		// =wd.findElements(calenderMonthsLocator);

		List<WebElement> calenderMonthsList = wait
				.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(calenderMonthsLocator));

		System.out.println(calenderMonthsList.size());

		// We want to Focus on the currentMonth (June)

		WebElement monthCalenderWebElement = calenderMonthsList.get(index); // currentMonth
		return monthCalenderWebElement;
	}

	public static void compareTwoMonthsPrice(String currentMonthPrice, String nextMonthPrice) {
		int currentMonthRSIndex = currentMonthPrice.indexOf("Rs");
		int nextMonthRSIndex = nextMonthPrice.indexOf("Rs");
		System.out.println(currentMonthRSIndex);
		System.out.println(nextMonthRSIndex);

		String currentPrice = currentMonthPrice.substring(currentMonthRSIndex + 2);
		String nextPrice = nextMonthPrice.substring(nextMonthRSIndex + 2);

		int current = Integer.parseInt(currentPrice);
		int next = Integer.parseInt(nextPrice);

		if (current < next) {
			System.out.println("The Lowest price for the 2 months is " + current);
		} else if (current == next) {
			System.out.println("Price is same for both months!! Choose whatever you prefer");

		} else {
			System.out.println("The Lowest price for months is " + next);

		}

	}

}
