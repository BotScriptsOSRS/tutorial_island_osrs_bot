import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import utils.NameGenerator;
import utils.Sleep;

import utils.WidgetHandler;

@ScriptManifest(name = "Tutorial Island", author = "BotScriptsOSRS", version = 1.0, info = "", logo = "")
public class MainScript extends Script {

    private WidgetHandler widgetHandler;
    private NameGenerator nameGenerator;

    @Override
    public void onStart() {
        this.widgetHandler = new WidgetHandler(this);
        this.nameGenerator = new NameGenerator();
    }

    @Override
    public int onLoop() throws InterruptedException {
        setName();
        completeCharacterSetup();
        return random(200, 300);
    }

    private void setName() {
        if (widgetHandler.isNameWidgetWorking()) {
            log("Trying to write name");
            String randomName = nameGenerator.generateRandomName();
            getKeyboard().typeString(randomName);
            Sleep.sleepUntil(() -> !widgetHandler.isDisplayNameWidgetVisible() || widgetHandler.isSetNameWidgetAvailable(), 5000);
        }
        if (widgetHandler.isSetNameWidgetAvailable()){
            log("Name already exists, picking random name");
            widgetHandler.getRandomSetNameWidget().interact();
            Sleep.sleepUntil(() -> widgetHandler.isGreatWidgetVisible(), 5000);
            if (widgetHandler.isSetNameWidgetAvailable() && widgetHandler.getRandomSetNameWidget().interact()){
                Sleep.sleepUntil(() -> !widgetHandler.isDisplayNameWidgetVisible(), 5000);
            }
        }
    }

    private void completeCharacterSetup() throws InterruptedException {
        if (widgetHandler.waitForConfirmWidget()) {
            widgetHandler.interactWithFemaleRandomly();
            widgetHandler.interactWithSelectWidgets();
            widgetHandler.clickConfirmWidget();
        }
    }
}
