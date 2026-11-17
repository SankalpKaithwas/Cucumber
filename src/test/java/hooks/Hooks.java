package hooks;

import context.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class Hooks {

    // Before each scenario, PicoContainer creates one TestContext and passes it to
// every class that asks for one. SO wherever a TestContext in passed in constructor, pico will pass the one
// already created by it. The constructor parameter is the only thing Pico looks at. No annotation
    // is needed, but the class should have just one public constructor.

    private final TestContext context;

    public Hooks(TestContext context){
        this.context = context;
    }

    @Before
    public void beforeScenario(Scenario scenario) {
        System.out.println(">>> Starting scenario: " + scenario.getName());
    }

    @After
    public void afterScenario(Scenario scenario) {
        if (scenario.isFailed() && context.getResponse() != null) {
            scenario.attach(context.getResponse().asString(), "text/plain", "Last API response");
        }
        context.cleanUp();

        System.out.println(">>> Ending scenario: " + scenario.getName());
    }
    //@After runs whether the scenario passed or failed, so the cleanup always happens

}
