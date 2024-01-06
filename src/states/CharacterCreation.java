package states;

import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.Script;
import utils.NameGenerator;
import utils.Sleep;
import utils.WidgetHandler;

public class CharacterCreation {
    private final Script script;
    private final WidgetHandler widgetHandler;
    private final NameGenerator nameGenerator;

    public CharacterCreation(Script script) {
        this.script = script;
        this.widgetHandler = new WidgetHandler(script);
        this.nameGenerator = new NameGenerator();
    }

    public boolean performCreation() throws InterruptedException {
        if (!widgetHandler.isWidgetVisible("Choose display name", true) ||
            !widgetHandler.isWidgetVisible("Confirm", false)){
            script.log("Skipping state, already completed");
            return true;
        } else if (!isNameSet() ) {
            attemptToSetName();
            handleNameResponse();
        }
        return completeCharacterSetup();
    }

    private boolean isNameSet() {
        script.log("Name is not set yet");
        return widgetHandler.isWidgetVisible("Confirm", false);
    }

    private void attemptToSetName() {
        if (widgetHandler.isWidgetVisible("*", true)) {
            String randomName = nameGenerator.generateRandomName();
            script.log("Typing name: " + randomName);
            script.getKeyboard().typeString(randomName);
        }
    }

    private void handleNameResponse() {
        script.log("Wait until Set name widget is visible");
        Sleep.sleepUntil(() -> widgetHandler.isWidgetVisible("Set name", false) ||
                widgetHandler.isWidgetVisible("Great!", true), 5000);

        if (widgetHandler.isWidgetVisible("Great!", true)) {
            script.log("Unique name found, confirm the name");
            confirmNameSetting();
        } else if (widgetHandler.isWidgetVisible("Set name", false)) {
            script.log("Name already exists, pick a new name");
            pickNewName();
            widgetHandler.waitForWidget("Set name", false);
            script.log("Wait until Set name widget is visible");
            confirmNameSetting();
        }
    }

    private void confirmNameSetting() {
        script.log("Confirming name");
        widgetHandler.clickWidget("Set name");
    }

    private void pickNewName() {
        script.log("Pick random name from options");
        widgetHandler.getRandomWidgetWithAction("Set name").ifPresent(RS2Widget::interact);
    }

    private boolean completeCharacterSetup() throws InterruptedException {
        script.log("Finish character setup");
        if (widgetHandler.waitForWidget("Confirm", false)) {
            widgetHandler.interactWithWidgetRandomly("Female");
            widgetHandler.interactWithAllSelectWidgets();
            widgetHandler.clickWidget("Confirm");
        }
        return widgetHandler.waitForWidgetToDisappear("Confirm", false);
    }
}

