package curcul;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;

public class TestZIP {

    ClassLoader cl = TestZIP.class.getClassLoader();

    @Test
    void zipTest() throws Exception {
        ZipFile zf = new ZipFile(new File("src/test/resources/zip/1.zip"));
        ZipInputStream is = new ZipInputStream(cl.getResourceAsStream("zip/1.zip"));
        ZipEntry entry;
        while ((entry = is.getNextEntry()) != null) {
            switch (entry.getName()) {
                case ("D.pdf"):
                    assertEquals(entry.getName(), "D.pdf");
                    try (InputStream inputStream = zf.getInputStream(entry)) {
                        assert inputStream != null;
                        PDF pdf = new PDF(inputStream);
                        org.assertj.core.api.Assertions.assertThat(pdf.text).contains("СТЕРЕОСКОПИЧЕСКИЕ");
                    }
                    break;
                case ("one.csv"):
                    assertEquals(entry.getName(), "one.csv");
                    try (InputStream inputStream = zf.getInputStream(entry)) {
                        assert inputStream != null;
                        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                            List<String[]> content = reader.readAll();
                            org.assertj.core.api.Assertions.assertThat(content).contains(
                                    new String[]{"one","two","free"},
                                    new String[]{"a","b","c"}
                            );
                        }
                    }
                    break;
                case ("two.xls"):
                    assertEquals(entry.getName(), "two.xls");
                    try (InputStream inputStream = zf.getInputStream(entry)) {
                        assert inputStream != null;
                        XLS xls = new XLS(inputStream);
                        String value = xls.excel.getSheetAt(0).getRow(0).getCell(0).getStringCellValue();
                        org.assertj.core.api.Assertions.assertThat(value).isEqualTo("one ");
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + entry.getName());
            }
        }
    }

}
