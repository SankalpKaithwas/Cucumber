package runners;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.Suite;
import java.io.File;
import java.util.Collections;

import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@regression")
@ConfigurationParameter(key = FEATURES_PROPERTY_NAME, value = "src/test/resources")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "stepDefinitions")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
        value = "pretty, json:target/cucumber-reports/cucumber.json, html:target/cucumber-reports/cucumber.html")
public class TestRunner {

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                File reportOutputDirectory = new File("target/cucumber-html-reports");
                File jsonReport = new File("target/cucumber-reports/cucumber.html");

                if (jsonReport.exists()) {
                    Configuration configuration = new Configuration(reportOutputDirectory, "CucumberProductFT");
                    ReportBuilder reportBuilder = new ReportBuilder(
                            Collections.singletonList(jsonReport.getAbsolutePath()),
                            configuration
                    );
                    reportBuilder.generateReports();
                    System.out.println("Masterthought report generated at: " + reportOutputDirectory.getAbsolutePath());
                } else {
                    System.err.println("Cucumber JSON report not found at: " + jsonReport.getAbsolutePath());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }



}
