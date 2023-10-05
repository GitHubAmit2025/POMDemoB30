package testScripts;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class LoginPageTest {
	WebDriver driver;
	Properties prop;

	@BeforeMethod
	public void setup() throws IOException {

		String path = System.getProperty("user.dir") + "//src//test//resources//configFiles//config.properties";
		prop = new Properties();
		FileInputStream fin = new FileInputStream(path);
		prop.load(fin);
		String strBrowser = prop.getProperty("browser");
		if (strBrowser.equalsIgnoreCase("chrome")) {
			driver = new ChromeDriver();
		}
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
		driver.manage().window().maximize();

	}

	@Test(dataProvider = "loginData")
	public void validLogin(String strUser, String strPwd) {
		// driver.get("http://the-internet.herokuapp.com/login");
		driver.get(prop.getProperty("url"));
//		driver.findElement(By.xpath("//input[@name='username']")).sendKeys(strUser);
//		driver.findElement(By.id("password")).sendKeys(strPwd);
//		driver.findElement(By.cssSelector(".fa.fa-2x.fa-sign-in")).click();
		driver.findElement(By.xpath(readObjRepo("userName"))).sendKeys(strUser);
		driver.findElement(By.id(readObjRepo("password"))).sendKeys(strPwd);
		driver.findElement(By.cssSelector(readObjRepo("loginBtn"))).click();
		boolean isDisp = driver.findElement(By.cssSelector("div.flash.success")).isDisplayed();
		Assert.assertTrue(isDisp);
	}

	@DataProvider(name = "loginData")
	public Object[][] getData() throws CsvValidationException, IOException {
		String path = System.getProperty("user.dir") + "//src//test//resources//testData//loginData.csv";
		CSVReader reader = new CSVReader(new FileReader(path));
		String arr[];
		ArrayList<Object> dataList = new ArrayList<Object>();
		while ((arr = reader.readNext()) != null) {
			Object[] record = { arr[0], arr[1] };
			dataList.add(record);

		}

		return dataList.toArray(new Object[dataList.size()][]);
	}

	public String readObjRepo(String objName) {
		String objPath = null;
		String path = System.getProperty("user.dir") + "//src//test//resources//testData//logRepo.xlsx";
		//HSSF.... -> .xls
		//XSSF.... -> .xlsx
		FileInputStream fin;
		XSSFWorkbook workbook = null;
		try {
			workbook = new XSSFWorkbook(new FileInputStream(path));
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		XSSFSheet sheet = workbook.getSheet("loginPage");
		int numRows = sheet.getLastRowNum();
		for(int i = 1; i <=numRows; i++) {
			XSSFRow row = sheet.getRow(i);
			if((row.getCell(0).getStringCellValue().equalsIgnoreCase(path))) {
			objPath = row.getCell(1).getStringCellValue();
			}
		}
		return objPath;
	}
	@AfterMethod
	public void teardown() {
		driver.close();
	}
}
