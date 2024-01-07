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
    private boolean nameSet = false;
    private boolean characterSetupCompleted = false;

    public CharacterCreation(Script script, WidgetHandler widgetHandler) {
        this.script = script;
        this.widgetHandler = widgetHandler;
        this.nameGenerator = new NameGenerator();
    }

    public boolean performCreation() {
        if (characterSetupCompleted || (!widgetHandler.isWidgetVisible("Set display name", true) &&
                !widgetHandler.isWidgetVisible("Confirm", false))) {
            script.log("Skipping state, already completed");
            return true;
        }

        if (!nameSet) {
            nameSet = attemptToSetName() && handleNameResponse();
        }

        if (nameSet) {
            characterSetupCompleted = completeCharacterSetup();
        }

        return characterSetupCompleted;
    }

    private boolean attemptToSetName() {
        if (widgetHandler.isWidgetVisible("Enter name", false) && widgetHandler.clickWidget("Enter name")) {
            String randomName = nameGenerator.generateRandomName();
            script.log("Typing name: " + randomName);
            script.getKeyboard().typeString(randomName);
            return true;
        }
        return false;
    }

    private boolean handleNameResponse() {
        script.log("Wait until Set name widget is visible");
        Sleep.until(() -> widgetHandler.isWidgetVisible("Set name", false) ||
                widgetHandler.isWidgetVisible("Great!", true));

        if (widgetHandler.isWidgetVisible("Great!", true)) {
            script.log("Unique name found, confirm the name");
            return confirmNameSetting();
        } else if (widgetHandler.isWidgetVisible("Set name", false)) {
            script.log("Name already exists, pick a new name");
            pickNewName();
            widgetHandler.waitForWidget("Set name", false);
            script.log("Wait until Set name widget is visible");
            return confirmNameSetting();
        }
        return false;
    }

    private boolean confirmNameSetting() {
        script.log("Confirming name");
        return widgetHandler.clickWidget("Set name");
    }

    private void pickNewName() {
        script.log("Pick random name from options");
        widgetHandler.getRandomWidgetWithAction("Set name").ifPresent(RS2Widget::interact);
    }

    private boolean completeCharacterSetup() {
        script.log("Finish character setup");
        if (widgetHandler.waitForWidget("Confirm", false)) {
            widgetHandler.interactWithWidgetRandomly("Female");
            widgetHandler.interactWithSomeSelectWidgets();
            widgetHandler.clickWidget("Confirm");
        }
        return widgetHandler.waitForWidgetToDisappear("Confirm", false);
    }
}

