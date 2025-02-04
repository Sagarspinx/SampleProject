package URLRedirect;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class a {
    public static void main(String[] args) {
        // Path to the Excel file
        String excelFilePath = ".//Excel//RedirectURLs.xlsx";

        // Set up ChromeOptions to remove "Chrome is being controlled by automated test software" message
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        // Set up WebDriver
        System.setProperty("webdriver.chrome.driver", ".//Browser//chromedriver_132.exe");
        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();

        try {
            // Load the Excel file
            FileInputStream fis = new FileInputStream(excelFilePath);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            // Loop through each row in the Excel file (starting from row 1, skipping the header)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || row.getCell(0) == null || row.getCell(1) == null) {
                    continue; // Skip empty rows
                }

                // Get the old URL (Column A) and new URL (Column B)
                String oldURL = row.getCell(0).getStringCellValue();
                String expectedNewURL = row.getCell(1).getStringCellValue();

                System.out.println("Verifying redirection from: " + oldURL + " to: " + expectedNewURL);

                // Navigate to the old URL
                driver.get(oldURL);

                // Get the current URL after redirection
                String actualRedirectedURL = driver.getCurrentUrl();
                System.out.println("Redirected to: " + actualRedirectedURL);

                // Compare the redirected URL with the expected new URL
                boolean isRedirectedProperly = actualRedirectedURL.equalsIgnoreCase(expectedNewURL);

                // Write the result to Column C
                Cell resultCell = row.createCell(2); // Column C (index 2)
                resultCell.setCellValue(isRedirectedProperly ? "True" : "False");

                // Save the updated Excel file after processing each row
                try (FileOutputStream fos = new FileOutputStream(excelFilePath)) {
                    workbook.write(fos);
                }
                System.out.println("Result for entry " + i + " saved.");
            }

            // Close the workbook and input stream
            workbook.close();
            fis.close();

            System.out.println("Redirection verification completed. Results saved in the Excel file.");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Quit the browser
            driver.quit();
        }
    }
}
