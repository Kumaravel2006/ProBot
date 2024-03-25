package com.servlets;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/BeautifyServlet")
public class BeautifyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static Map<String, String> beautifierLinks = new HashMap<String, String>();

    public BeautifyServlet() {
    beautifierLinks.put("java", "https://www.tutorialspoint.com/online_java_formatter.htm");
    beautifierLinks.put("javascript", "https://www.tutorialspoint.com/online_javascript_formatter.htm");
    beautifierLinks.put("python", "https://formatter.org/python-formatter");
    beautifierLinks.put("c", "https://www.tutorialspoint.com/online_c_formatter.htm");
    beautifierLinks.put("cpp", "https://www.tutorialspoint.com/online_c_formatter.htm");
//    beautifierLinks.put("ruby", "https://www.tutorialspoint.com/online_ruby_formatter.htm");
//    beautifierLinks.put("swift", "https://swift-format.com/");
    }
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		System.out.println("BeautifierServlet");
		
		String beautifiedCode = "Error occurred while connecting to the server";
		
		String code = request.getParameter("code").replaceAll("\u00a0\n|\u00a0", " ").replaceAll("·", " ");
//		String code = request.getParameter("code");
		
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection stringSelection = new StringSelection(code);
		clipboard.setContents(stringSelection, null);
		
		System.out.println(code);
		
		String language = request.getParameter("language");
		
		
		WebDriver driver = null;
			
		try {
			
			driver = new ChromeDriver();
			
			driver.get(beautifierLinks.get(language));
			
			driver.manage().window().maximize();
			
			WebElement inputArea = null;
			
			if(language.equals("python")) {
				
				inputArea = driver.findElement(By.cssSelector("#code-editor > textarea"));
				
				inputArea.sendKeys(Keys.chord(Keys.CONTROL, "v"));
				
				WebElement beautifyButton = driver.findElement(By.id("btn-format"));
				
				beautifyButton.click();
				
				Thread.sleep(1000);
				
				WebElement outputArea = driver.findElement(By.cssSelector("#code-preview > div.ace_scroller > div"));
				
				String output = outputArea.getAttribute("innerText");
				
				driver.quit();
				
				response.getWriter().write(output.replaceAll("\u00a0\n|\u00a0", " ").replaceAll("·", " "));
			}
			else {
				inputArea = driver.findElement(By.cssSelector((language.equals("javascript"))?"#editor > textarea":"#code > textarea"));

				inputArea.sendKeys(Keys.chord(Keys.CONTROL, "a"));
				
				inputArea.sendKeys(Keys.chord(Keys.CONTROL,"v"));
				
				WebElement beautifyButton = driver.findElement(By.id("beautify"));
				
				beautifyButton.click();
				
				Thread.sleep(1000);
				
				beautifiedCode = driver.findElement(By.cssSelector((language.equals("javascript"))? "#terminal > div.ace_scroller > div" :"#result > div.ace_scroller > div")).getAttribute("innerText").replaceAll("\\u00a0\\n", " ").replaceAll("·", "");
				
				System.out.println(beautifiedCode);
				
				driver.quit();
				
				response.getWriter().write(beautifiedCode.replaceAll("\u00a0\n|\u00a0", " ").replaceAll("·", " "));
			}
		} catch (Exception e) {
			driver.quit();
			e.printStackTrace();
		}
	}
}